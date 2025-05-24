package ir.service.order;

import ir.convert.category.category_response.CategoryEntityToCategoryResponseDtoConvertor;
import ir.convert.filter.ComprehensiveEntityToResponseDtoConvertor;
import ir.convert.filter.FilteredOrderToOrderResponseDtoConverter;
import ir.convert.order.order_response.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.bankcard.BankCardInfoDto;
import ir.dto.category.CategoryResponseDto;
import ir.dto.filter.ComprehensiveFilterDto;
import ir.dto.filter.ComprehensiveResponseDto;
import ir.dto.filter.FilterOrderByWorkingDateDto;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.*;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.OrderRepository;
import ir.repository.filter.ComprehensiveSpecification;
import ir.repository.filter.OrderSpecifications;
import ir.repository.filter.WorkingDateAndTimeSpecification;
import ir.security.JwtTokenProvider;
import ir.service.card.CardService;
import ir.service.category.CategoryService;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service implementation for managing orders.
 *
 * <p>This class handles order-related operations such as creating, retrieving,
 * and validating orders for both customers and experts. It ensures proper
 * authentication and status validation for each operation.</p>
 */
@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;
    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;
    private final ComprehensiveEntityToResponseDtoConvertor comprehensiveEntityToResponseDtoConvertor;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;
    private final WorkingDateAndTimeSpecification workingDateAndTimeSpecification;
    private final FilteredOrderToOrderResponseDtoConverter orderResponseDtoConverter;
    private final OrderSpecifications orderSpecifications;

    @Override
    public List<OrderResponseDto> getOrder(String token) {
        log.info("Fetching orders for expert with token");

        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isEmpty() || !(userOptional.get() instanceof Expert)) {
            log.error("User with token is not an expert or not found.");
            throw new CustomException("User not found or not an expert.", ErrorCode.NOT_FOUND.getCode());
        }
        Expert expert = (Expert) userOptional.get();

        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with username {} is not approved to retrieve orders.", username);
            throw new CustomException("Expert should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        log.info("Fetching orders based on expert categories for token.");
        List<Category> categories = expert.getCategories();
        if (categories == null || categories.isEmpty()) {
            log.error("Expert with username {} has no associated categories.", username);
            throw new CustomException("Expert has no categories.", ErrorCode.NOT_FOUND.getCode());
        }

        List<Order> ordersByCategories = findOrdersByCategories(categories);
        if (ordersByCategories.isEmpty()) {
            log.info("No orders found for expert with categories: {}", categories);
            return List.of(); // بازگشت لیست خالی در صورت نبود سفارش
        }
        log.info("Successfully fetched {} orders for expert with username: {}", ordersByCategories.size(), username);
        return orderEntityToOrderResponseDtoConvertor.convertList(ordersByCategories);
    }

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderDto, String token) {
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isPresent() && userOptional.get() instanceof Customer customer) {
            validateCustomer(customer, customer.getEmail());

            log.info("Checking for duplicate orders...");
            checkForDuplicateOrders(orderDto, customer.getEmail());

            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDescription(orderDto.getOrderDescription());
            order.setOrderDate(orderDto.getOrderDate());
            order.setOrderAddress(orderDto.getOrderAddress());
            order.setStatus(OrderStatus.WAITING_FOR_EXPERT_PROPOSALS);
            order.setExecutionDate(orderDto.getExecutionDate());
            order.setOfferCustomerPrice(orderDto.getOfferCustomerPrice());

            // Validate Main Category
            String mainCategoryName = orderDto.getMainCategoryName();
            log.info("Validating main category with name: {}", mainCategoryName);
            Optional<Category> mainCategoryOptional = categoryService.findMainCategoryByName(mainCategoryName);
            if (mainCategoryOptional.isEmpty()) {
                log.error("Main category not found with name: {}", mainCategoryName);
                throw new CustomException("Main category not found", ErrorCode.NOT_FOUND.getCode());
            }

            // Validate Sub Category
            String subCategoryName = orderDto.getSubCategoryName();
            log.info("Validating subcategory with name: {}", subCategoryName);
            Optional<Category> subCategoryOptional = categoryService.findSubCategoryByName(subCategoryName);
            if (subCategoryOptional.isEmpty()) {
                log.error("Subcategory not found with name: {}", subCategoryName);
                throw new CustomException("Sub category not found", ErrorCode.NOT_FOUND.getCode());
            }

            // Setting categories
            order.setCategory(subCategoryOptional.get());
            CategoryResponseDto mainCategoryResponse = categoryEntityToCategoryResponseDtoConvertor.apply(mainCategoryOptional.get());
            CategoryResponseDto subCategoryResponse = categoryEntityToCategoryResponseDtoConvertor.apply(subCategoryOptional.get());

            order = orderRepository.save(order);
            log.info("Order saved successfully for customer with");
            OrderResponseDto orderResponse = orderEntityToOrderResponseDtoConvertor.apply(order);
            orderResponse.setMainCategory(mainCategoryResponse);
            orderResponse.setSubCategory(subCategoryResponse);

            return orderResponse;
        }

        log.error("Authentication failed for user with token. User not found.");
        throw new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
    }

    private void validateCustomer(User user, String email) {
        if (user.getStatus().equals(UserStatus.DELETED)) {
            log.error("User with email {} is deleted and cannot create orders.", email);
            throw new CustomException("User already deleted", ErrorCode.NOT_FOUND.getCode());
        }

        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved to create orders.", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        log.info("Customer with email {} is valid.", email);
    }

    private void checkForDuplicateOrders(OrderRequestDto orderDto, String email) {
        List<Order> existingOrders = orderRepository.findAllByCustomerEmail(email);
        boolean isDuplicate = existingOrders.stream().anyMatch(existingOrder ->
                existingOrder.getOrderDescription().equals(orderDto.getOrderDescription()) &&
                        existingOrder.getOrderDate().equals(orderDto.getOrderDate()) &&
                        existingOrder.getExecutionDate().equals(orderDto.getExecutionDate()) &&
                        existingOrder.getOrderAddress().equals(orderDto.getOrderAddress()) &&
                        existingOrder.getCategory().getName().equals(orderDto.getSubCategoryName()) &&
                        existingOrder.getOfferCustomerPrice().compareTo(orderDto.getOfferCustomerPrice()) == 0
        );

        if (isDuplicate) {
            log.warn("Duplicate order found for customer with email: {}", email);
            throw new CustomException("Your order is a duplicate", ErrorCode.DUPLICATE_ORDER.getCode());
        }

        log.info("No duplicate order found for customer with email: {}", email);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerEmail(String token) {
        log.info("Fetching orders for customer using token.");

        // Step 1: Extract username from token and validate user
        String username = jwtTokenProvider.extractUsername(token);
        User user = userService.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User with username {} not found.", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        // Step 2: Validate user type
        if (!(user instanceof Customer)) {
            log.error("Unauthorized attempt to fetch orders by user with username: {}", username);
            throw new CustomException("Only customers can fetch their orders.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 3: Fetch orders for the customer
        log.info("Fetching orders for customer with username: {}", username);
        List<Order> orders = orderRepository.findByCustomer_UserName(username);

        if (orders.isEmpty()) {
            log.warn("No orders found for customer with username: {}", username);
            throw new CustomException("No orders found for the provided username.", ErrorCode.NOT_FOUND.getCode());
        }

        // Step 4: Convert orders to DTO
        log.info("Successfully fetched {} orders for customer with username: {}", orders.size(), username);
        return orders.stream()
                .map(orderEntityToOrderResponseDtoConvertor)
                .toList();
    }

    /**
     * Finds orders belonging to the specified categories.
     *
     * @param categories the list of categories to filter orders by
     * @return a list of {@link Order} entities matching the given categories
     */
    @Override
    public List<Order> findOrdersByCategories(List<Category> categories) {
        log.info("Fetching orders for the specified categories.");
        Optional<List<Order>> optionalOrderList = orderRepository.findAllByCategoryIn(categories);
        return optionalOrderList.orElse(new ArrayList<>());
    }

    /**
     * Finds an order by its unique ID.
     *
     * @param id the unique ID of the order to retrieve
     * @return the {@link Order} entity matching the given ID
     * @throws CustomException if the order is not found
     */
    @Override
    public Order findById(Long id) {
        log.info("Searching for order with ID: {}", id);

        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder
                .orElseThrow(() -> new CustomException("Order not found with provided order number",
                        ErrorCode.NOT_FOUND.getCode()));
    }

    /**
     * Saves the given order to the database.
     *
     * @param order the {@link Order} entity to be saved
     * @return the saved {@link Order} entity
     */
    @Override
    public Order save(Order order) {
        log.info("Saving order with details: {}", order);
        return orderRepository.save(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(String token,
                                              OrderStatus orderStatus,
                                              Long orderNumber,
                                              Long offerNumber) {
        log.info("Starting updateOrderStatus for token: {}, orderNumber: {}, offerNumber: {}",
                token, orderNumber, offerNumber);

        // Step 1: Validate token and extract username
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isEmpty()) {
            throw new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
        }

        User user = userOptional.get();
        if (user.getStatus().equals(UserStatus.NEW)) {
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 2: Validate order
        Order order = findById(orderNumber);
        if (order == null) {
            throw new CustomException("Order not found with provided order number.", ErrorCode.NOT_FOUND.getCode());
        }

        if (order.getStatus().equals(orderStatus)) {
            throw new CustomException("Order is already in the specified status.", ErrorCode.ORDER_ALREADY_IN_STATUS.getCode());
        }

        // Step 3: Validate offer
        Offer offer = order.getOffers().stream()
                .filter(o -> o.getId().equals(offerNumber))
                .findFirst()
                .orElseThrow(() -> new CustomException("Offer not found", ErrorCode.NOT_FOUND.getCode()));

        // Step 4: Handle status-specific updates
        switch (orderStatus) {
            case WAITING_FOR_EXPERT_ARRIVAL:
                BigDecimal proposedPrice = offer.getProposedPrice();
                order.setFinalPrice(proposedPrice);
                break;

            case STARTED:
                order.setStartWorkingTime(LocalDateTime.now());
                order.setStartWorkingDate(LocalDate.now());
                break;

            case COMPLETED:
                order.setEndWorkingTime(LocalDateTime.now());
                order.setEndWorkingDate(LocalDate.now());

                //  long actualDuration = ChronoUnit.HOURS.between(order.getStartWorkingTime(), order.getEndWorkingTime());
                long actualDuration = ChronoUnit.SECONDS.between(order.getStartWorkingTime(), order.getEndWorkingTime());
                Expert expert = offer.getExpert();
                if (actualDuration > offer.getEstimatedTime() && expert.getRating() != 0) {
                    int delayHours = (int) (actualDuration - offer.getEstimatedTime());
                    double penalty = delayHours * 0.05;

                    expert.setRating(expert.getRating() - penalty);
                    userService.saveUser(expert);

                    // No direct call to expertService.save, manage through cascading
                    offer.setExpert(expert); // Update the offer's expert directly
                }
                break;

            default:
                throw new CustomException("Unsupported order status.", ErrorCode.INVALID_DATA.getCode());
        }

        // Step 5: Update order status and save
        order.setStatus(orderStatus);
        orderRepository.save(order); // Cascade ensures changes to expert are saved
        log.info("Order status updated and saved for order number: {}", orderNumber);

        // Step 6: Convert order to response DTO
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    @Override
    public OrderResponseDto paid(String token,
                                 Long orderNumber,
                                 Long offerNumber) {
        log.info("Processing payment for orderNumber: {}, offerNumber: {}, token", orderNumber, offerNumber);

        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isEmpty() || !(userOptional.get() instanceof Customer)) {
            log.error("User with token is not a customer or not found.");
            throw new CustomException("User not found or not a customer.", ErrorCode.NOT_FOUND.getCode());
        }

        Customer customer = (Customer) userOptional.get();

        Order order = findById(orderNumber);
        if (order == null) {
            log.error("Order not found with orderNumber: {}", orderNumber);
            throw new CustomException("Order not found with provided order number.", ErrorCode.NOT_FOUND.getCode());
        }

        Optional<Offer> offerOptional =
                order.getOffers().stream().filter(offer -> offer.getId().equals(offerNumber)).findFirst();
        if (offerOptional.isEmpty()) {
            log.error("Offer not found for offerNumber: {}", offerNumber);
            throw new CustomException("Offer not found with provided offer number.", ErrorCode.NOT_FOUND.getCode());
        }

        Offer offer = offerOptional.get();
        Expert expert = offer.getExpert();
        BigDecimal finalPrice = order.getFinalPrice();

        if (customer.getWallet().getCredit() == null || customer.getWallet().getCredit().compareTo(finalPrice) < 0) {
            log.error("Insufficient credit for customer with token.");
            throw new CustomException("Credit is not enough.", ErrorCode.CREDIT_NOT_ENOUGH.getCode());
        }

        BigDecimal expertCredit = expert.getWallet().getCredit() != null ? expert.getWallet().getCredit() : BigDecimal.ZERO;
        customer.getWallet().setCredit(customer.getWallet().getCredit().subtract(finalPrice));
        expert.getWallet().setCredit(expertCredit.add(BigDecimal.valueOf(finalPrice.doubleValue() * 0.7))); // 70% of finalPrice added to expert credit
        userService.saveUser(customer);
        userService.saveUser(expert);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Order marked as paid successfully. orderNumber: {}", orderNumber);

        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    @Override
    public OrderResponseDto updateOrder(Long orderId,
                                        OrderRequestDto orderDto,
                                        String token) {
        log.info("Attempting to update order with ID: {} for customer with token", orderId);

        // Step 1: Extract username from token and validate customer
        String username = jwtTokenProvider.extractUsername(token);
        User user = userService.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User with username {} not found.", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        if (!(user instanceof Customer customer)) {
            log.error("Unauthorized update attempt by user with username: {}", username);
            throw new CustomException("Only customers can update orders.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 2: Check customer status
        if (customer.getStatus().equals(UserStatus.NEW)) {
            log.error("Customer with username {} is not approved to update orders.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 3: Retrieve the order and validate its existence
        log.info("Validating order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order with ID {} not found.", orderId);
                    return new CustomException("Order not found with order ID: " + orderId, ErrorCode.NOT_FOUND.getCode());
                });

        // Step 4: Ensure the order's status is valid for updates
        if (order.getStatus() != OrderStatus.WAITING_FOR_EXPERT_PROPOSALS) {
            log.error("Order with ID: {} cannot be updated because its status is not valid for updates.", orderId);
            throw new CustomException("You cannot update this order because it has already been used.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        // Step 5: Ensure the order belongs to the customer
        if (!order.getCustomer().equals(customer)) {
            log.error("Unauthorized attempt to update order with ID: {} by customer with username: {}", orderId, username);
            throw new CustomException("This order does not belong to the customer.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 6: Update order details
        log.info("Updating order details for order with ID: {}", orderId);
        order.setOrderDescription(orderDto.getOrderDescription());
        order.setOrderDate(orderDto.getOrderDate());
        order.setOrderAddress(orderDto.getOrderAddress());
        order.setStatus(OrderStatus.WAITING_FOR_EXPERT_PROPOSALS);
        order.setExecutionDate(orderDto.getExecutionDate());
        order.setOfferCustomerPrice(orderDto.getOfferCustomerPrice());

        // Step 7: Save the updated order
        orderRepository.save(order);
        log.info("Order with ID: {} updated successfully", orderId);

        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    @Override
    public void deleteOrder(Long orderId, String token) {
        log.info("Attempting to delete order with ID: {} for customer with token", orderId);

        // Step 1: Extract username from token and validate the customer
        String username = jwtTokenProvider.extractUsername(token);
        User user = userService.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User with username {} not found.", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        if (!(user instanceof Customer customer)) {
            log.error("Unauthorized delete attempt by user with username: {}", username);
            throw new CustomException("Only customers can delete orders.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 2: Check customer status
        if (customer.getStatus().equals(UserStatus.NEW)) {
            log.error("Customer with username {} is not approved to delete orders.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 3: Validate the order existence
        log.info("Validating order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order with ID {} not found.", orderId);
                    return new CustomException("Order not found with order ID: " + orderId, ErrorCode.NOT_FOUND.getCode());
                });

        // Step 4: Check the order's status
        if (order.getStatus() != OrderStatus.WAITING_FOR_EXPERT_PROPOSALS) {
            log.error("Order with ID: {} cannot be deleted because its status is not valid for deletion.", orderId);
            throw new CustomException("You cannot delete this order because it has already been used.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        // Step 5: Ensure the order belongs to the customer
        if (!order.getCustomer().equals(customer)) {
            log.error("Unauthorized attempt to delete order with ID: {} by customer with username: {}", orderId, username);
            throw new CustomException("This order does not belong to the customer.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 6: Delete the order
        log.info("Deleting order with ID: {}", orderId);
        orderRepository.delete(order);
        log.info("Order with ID: {} deleted successfully.", orderId);
    }

    @Transactional
    @Override
    public String payOnlineByCard(String token, BankCardInfoDto bankCardInfoDto, Long orderNumber, Long offerNumber) {
        String username = jwtTokenProvider.extractUsername(token);
        User user = userService.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User with username {} not found.", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        BankCard bankCard = cardService.findByOwner(user).orElseThrow(() -> {
            log.error("User with username {} has not a bank-card.", username);
            return new CustomException("Bank card not found.", ErrorCode.NOT_FOUND.getCode());
        });


        String cardNumber = bankCardInfoDto.getCardNumber();
        if (!bankCard.getCardNumber().equals(cardNumber)) {
            throw new CustomException("Bank card number is invalid.", ErrorCode.BAD_REQUEST.getCode());
        }

        String password = bankCardInfoDto.getPassword();
        if (!passwordEncoder.matches(password, bankCard.getPassword())) {
            throw new CustomException("Bank card password is invalid.", ErrorCode.BAD_REQUEST.getCode());
        }
        Integer cvv2 = bankCardInfoDto.getCvv2();
        if (!bankCard.getCvv2().equals(cvv2)) {
            throw new CustomException("Bank card cvv2 is invalid.", ErrorCode.BAD_REQUEST.getCode());
        }

        Order order = findById(orderNumber);
        if (order == null) {
            log.error("Order not found with orderNumber: {}", orderNumber);
            throw new CustomException("Order not found with provided order number.", ErrorCode.NOT_FOUND.getCode());
        }

        Optional<Offer> offerOptional =
                order.getOffers().stream().filter(offer -> offer.getId().equals(offerNumber)).findFirst();
        if (offerOptional.isEmpty()) {
            log.error("Offer not found for offerNumber: {}", offerNumber);
            throw new CustomException("Offer not found with provided offer number.", ErrorCode.NOT_FOUND.getCode());
        }

        Offer offer = offerOptional.get();
        Expert expert = offer.getExpert();
        BigDecimal finalPrice = order.getFinalPrice();

        if (bankCard.getBalance().compareTo(finalPrice) < 0) {
            log.error("Insufficient credit for band card: {}", bankCard.getCardNumber());
            throw new CustomException("Credit is not enough.", ErrorCode.CREDIT_NOT_ENOUGH.getCode());
        }

        BigDecimal expertCredit = expert.getWallet().getCredit() != null ? expert.getWallet().getCredit() : BigDecimal.ZERO;
        BigDecimal newBalanceCard = bankCard.getBalance().subtract(finalPrice);
        bankCard.setBalance(newBalanceCard);
        expert.getWallet().setCredit(expertCredit.add(BigDecimal.valueOf(finalPrice.doubleValue() * 0.7))); // 70% of finalPrice added to expert credit
        cardService.save(bankCard);
        userService.saveUser(expert);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Order marked as paid successfully. orderNumber: {}", orderNumber);

        return "Paid successfully. orderNumber: " + orderNumber;
    }

    @Override
    public Map<String, Long> getOrderReport() {
        Map<String, Long> report = new HashMap<>();

        // Count all requests submitted (orders with any status)
        long totalRequestsCount = orderRepository.count();

        // Count orders that are COMPLETED or PAID
        long completedOrPaidCount = orderRepository.countByStatusIn(List.of(OrderStatus.COMPLETED, OrderStatus.PAID));

        // Add data to the report
        report.put("Number of requests submitted", totalRequestsCount);
        report.put("Number of orders completed", completedOrPaidCount);

        return report;
    }

    @Override
    public List<ComprehensiveResponseDto> filterComprehensiveOrders(ComprehensiveFilterDto filterDto, Pageable pageable) {
        log.info("Fetching filtered comprehensive orders with criteria: {}", filterDto);
        Specification<Order> spec = ComprehensiveSpecification.filterComprehensive(filterDto);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        List<Order> orderList = orderPage.getContent();

        log.info("Successfully retrieved {} comprehensive orders.", orderList.size());
        return comprehensiveEntityToResponseDtoConvertor.convertList(orderList);
    }

    @Override
    public List<OrderResponseDto> getOrdersByWorkingDate(FilterOrderByWorkingDateDto filterOrderByWorkingDateDto) {
        Specification<Order> specification = orderSpecifications.filterOrdersWithOffersAndExpertsByWorkingDate(filterOrderByWorkingDateDto);
        List<Order> orders = orderRepository.findAll(specification);
        return orderResponseDtoConverter.convertList(orders);
    }

}