package ir.service.order;

import ir.convert.category.category_response.CategoryEntityToCategoryResponseDtoConvertor;
import ir.convert.order.order_response.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.category.CategoryResponseDto;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.*;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.OrderRepository;
import ir.service.category.CategoryService;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ir.entity.enums.OrderStatus.WAITING_FOR_EXPERT_PROPOSALS;

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
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;
    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;


    /**
     * Saves a new order for the authenticated customer.
     *
     * <p>Validates the customer status and the specified subcategory before saving the order.</p>
     *
     * @param orderDto the details of the order to save
     * @param email    the customer's email for authentication
     * @param password the customer's password for authentication
     * @return the saved order as a {@link OrderResponseDto}
     * @throws CustomException if the user is not found, not approved, deleted, or if the subcategory is invalid
     */
    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderDto, String email, String password) {
        log.info("Attempting to save order for customer with email: {}", email);

        User user = userService.findByEmailAndPassword(email, password);
        if (user instanceof Customer customer) {
            validateCustomer(user, email);

            log.info("Checking for duplicate orders...");
            checkForDuplicateOrders(orderDto, email);

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
            log.info("Order saved successfully for customer with email: {}", email);
            OrderResponseDto orderResponse = orderEntityToOrderResponseDtoConvertor.apply(order);
            orderResponse.setMainCategory(mainCategoryResponse);
            orderResponse.setSubCategory(subCategoryResponse);

            return orderResponse;
        }

        log.error("User not found with email: {}", email);
        throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
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


    /**
     * Retrieves a list of orders associated with a customer using their email and password.
     *
     * <p>This method authenticates the customer using their email and password. It ensures
     * that the user is a valid registered customer. After successful authentication, it fetches
     * all orders linked to the customer's email and converts them into a list of DTOs.</p>
     *
     * @param email    the email of the customer for authentication
     * @param password the password of the customer for authentication
     * @return a list of {@link OrderResponseDto} representing the customer's orders
     * @throws CustomException if the user is not a customer, not found, or authentication fails
     */
    @Override
    public List<OrderResponseDto> getOrdersByCustomerEmail(String email, String password) {
        log.info("Fetching orders for customer with email: {}", email);

        User user = userService.findByEmailAndPassword(email, password);
        if (!(user instanceof Customer)) {
            log.error("User is not a customer or not found. Email: {}", email);
            throw new CustomException("User not found or is not a customer", ErrorCode.NOT_FOUND.getCode());
        }

        List<Order> orders = orderRepository.findAllByCustomerEmail(email);
        log.info("Successfully fetched {} orders for customer with email: {}", orders.size(), email);

        List<OrderResponseDto> responseDtos = orderEntityToOrderResponseDtoConvertor.convertList(orders);
        log.info("Successfully converted {} orders to DTO format for customer with email: {}",
                responseDtos.size(), email);

        return responseDtos;
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
     * Retrieves orders for the authenticated expert.
     *
     * <p>Filters orders based on the categories the expert is associated with.</p>
     *
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     * @return a list of {@link OrderResponseDto} representing the matching orders
     * @throws CustomException if the user is not found or not approved
     */
    @Override
    public List<OrderResponseDto> getOrder(String email, String password) {
        log.info("Fetching orders for expert with email: {}", email);

        User user = userService.findByEmailAndPassword(email, password);
        if (user != null) {
            Expert expert = (Expert) user;

            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("Expert with email {} is not approved to retrieve orders.", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }

            log.info("Fetching orders based on expert categories for email: {}", email);
            List<Category> categories = expert.getCategories();
            List<Order> ordersByCategories = findOrdersByCategories(categories);

            log.info("Successfully fetched {} orders for expert with email: {}", ordersByCategories.size(), email);
            return orderEntityToOrderResponseDtoConvertor.convertList(ordersByCategories);
        }
        log.error("User not found with email: {}", email);
        throw new CustomException("User not found with provided email and password",
                ErrorCode.NOT_FOUND.getCode());
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

    /**
     * Updates the status of a specified order and validates associated data.
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @param orderStatus the new status to set for the order
     * @param orderNumber the unique number of the order to update
     * @param offerNumber the unique number of the offer linked to the order
     * @return the updated order as a {@link OrderResponseDto}
     * @throws CustomException if validation fails or data is not found
     */
    @Override
    public OrderResponseDto updateOrderStatus(String email, String password,
                                              OrderStatus orderStatus,
                                              Long orderNumber, Long offerNumber) {

        log.info("Starting updateOrderStatus for email: {}, orderNumber: {}, offerNumber: {}",
                email, orderNumber, offerNumber);

        if (orderStatus == null) {
            log.error("Order status is null");
            throw new CustomException("Order status must not be null", ErrorCode.INVALID_DATA.getCode());
        }

        if (OrderStatus.PAID.equals(orderStatus)) {
            log.error("Invalid order status: PAID is not allowed");
            throw new CustomException("Order status should not be 'PAID'",
                    ErrorCode.ORDER_STATUS_SHOULD_NOT_BE_PAID.getCode());
        }

        log.info("Validating user with email: {}", email);
        User user = userService.findByEmailAndPassword(email, password);
        if (user == null) {
            log.error("User not found for email: {}", email);
            throw new CustomException("User not found with provided email and password",
                    ErrorCode.NOT_FOUND.getCode());
        }

        log.info("Validating order with orderNumber: {}", orderNumber);
        Order order = findById(orderNumber);
        if (order == null) {
            log.error("Order not found with orderNumber: {}", orderNumber);
            throw new CustomException("Order not found with provided order number",
                    ErrorCode.NOT_FOUND.getCode());
        }

        if (OrderStatus.WAITING_FOR_EXPERT_ARRIVAL.equals(order.getStatus()) &&
                OrderStatus.WAITING_FOR_EXPERT_ARRIVAL.equals(orderStatus)) {
            log.error("Order with orderNumber: {} is already 'WAITING_FOR_EXPERT_ARRIVAL'", orderNumber);
            throw new CustomException("Order is already 'WAITING_FOR_EXPERT_ARRIVAL'",
                    ErrorCode.ORDER_ALREADY_WAITING_FOR_EXPERT_ARRIVAL.getCode());
        }

        if (OrderStatus.STARTED.equals(order.getStatus()) && OrderStatus.STARTED.equals(orderStatus)) {
            log.error("Order with orderNumber: {} is already 'STARTED'", orderNumber);
            throw new CustomException("Order is already 'STARTED'", ErrorCode.ORDER_ALREADY_STARTED.getCode());
        }

        if (OrderStatus.COMPLETED.equals(order.getStatus()) && OrderStatus.COMPLETED.equals(orderStatus)) {
            log.error("Order with orderNumber: {} is already completed", orderNumber);
            throw new CustomException("Order is already completed", ErrorCode.ORDER_ALREADY_COMPLETE.getCode());
        }

        log.info("Validating offer with offerNumber: {}", offerNumber);
        Optional<Offer> offerOptional = order.getOffers().stream()
                .filter(offer -> offer.getId().equals(offerNumber))
                .findFirst();
        if (offerOptional.isEmpty()) {
            log.error("Offer not found for offerNumber: {}", offerNumber);
            throw new CustomException("Offer not found with provided offer number",
                    ErrorCode.NOT_FOUND.getCode());
        }
        Offer offer = offerOptional.get();

        if (OrderStatus.WAITING_FOR_EXPERT_ARRIVAL.equals(orderStatus)) {
            BigDecimal proposedPrice = offer.getProposedPrice();
            log.info("Proposed price for the offer: {}", proposedPrice);
            order.setFinalPrice((proposedPrice));
        }

        log.info("Updating order status to: {}", orderStatus);
        order.setStatus(orderStatus);

        log.info("Saving updated order with orderNumber: {}", orderNumber);
        orderRepository.save(order);

        log.info("Converting updated order to OrderResponseDto");
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    /**
     * Marks a specified order as paid and updates user credits.
     *
     * @param email the customer's email for authentication
     * @param password the customer's password for authentication
     * @param orderNumber the unique number of the order to mark as paid
     * @param offerNumber the unique number of the offer linked to the order
     * @return the updated order as a {@link OrderResponseDto}
     * @throws CustomException if validation fails or credits are insufficient
     */
    @Override
    public OrderResponseDto paid(String email,
                                 String password,
                                 Long orderNumber,
                                 Long offerNumber) {
        log.info("Processing payment for orderNumber: {}, offerNumber: {}, email: {}", orderNumber, offerNumber, email);
        User user = userService.findByEmailAndPassword(email, password);
        if (!(user instanceof Customer)) {
            log.error("User authentication failed for email: {}", email);
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }
        Order order = findById(orderNumber);
        if (order == null) {
            log.error("Offer not found for offerNumber: {}", offerNumber);
            throw new CustomException("Order not found with provided order number", ErrorCode.NOT_FOUND.getCode());
        }
        Optional<Offer> offerOptional =
                order.getOffers().stream().filter(offer -> offer.getId().equals(offerNumber)).findFirst();
        if (offerOptional.isPresent()) {
            Offer offer = offerOptional.get();
            Expert expert = offer.getExpert();
            BigDecimal finalPrice = order.getFinalPrice();
            Customer customer = (Customer) user;
            if (customer.getCredit() == null || customer.getCredit().compareTo(finalPrice) < 0) {
                log.error("Insufficient credit for customer with email: {}", email);
                throw new CustomException("Credit is not enough", ErrorCode.CREDIT_NOT_ENOUGH.getCode());
            }
            BigDecimal expertCredit = expert.getCredit() != null ? expert.getCredit() : BigDecimal.ZERO;
            customer.setCredit(customer.getCredit().subtract(finalPrice));
            expert.setCredit(expertCredit.add(finalPrice));
            userService.saveUser(customer);
            userService.saveUser(expert);
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Order marked as paid successfully. orderNumber: {}", orderNumber);
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    /**
     * Updates the details of a specified order for the authenticated customer.
     *
     * <p>Validates customer authentication and ensures the order status allows updates.
     * Also checks that the order belongs to the authenticated customer.</p>
     *
     * @param orderId the ID of the order to update
     * @param orderDto the updated details for the order
     * @param email the customer's email for authentication
     * @param password the customer's password for authentication
     * @return the updated order as a {@link OrderResponseDto}
     * @throws CustomException if validation fails or the order cannot be updated
     */
    @Override
    public OrderResponseDto updateOrder(Long orderId,
                                        OrderRequestDto orderDto,
                                        String email,
                                        String password) {
        log.info("Attempting to update order with ID: {} for customer with email: {}", orderId, email);

        User user = userService.findByEmailAndPassword(email, password);
        if (!(user instanceof Customer customer)) {
            log.error("Unauthorized update attempt by user with email: {}", email);
            throw new CustomException("Only customers can update orders", ErrorCode.UNAUTHORIZED.getCode());
        }

        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved to update orders", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }
        log.info("Validating order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(
                        "Order not found with order number: " + orderId, ErrorCode.NOT_FOUND.getCode()));

        if (order != null && order.getStatus() != WAITING_FOR_EXPERT_PROPOSALS) {
            log.error("Order with ID: {} cannot be updated because its status is not valid for updates", orderId);
            throw new CustomException("You cannot update this order because it has already been used.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        assert order != null;
        if (!order.getCustomer().equals(customer)) {
            log.error("Unauthorized attempt to update order with ID: {} by customer with email: {}", orderId, email);
            throw new CustomException("This order does not belong to the customer", ErrorCode.UNAUTHORIZED.getCode());
        }
        log.info("Updating order details for order with ID: {}", orderId);
        order.setOrderDescription(orderDto.getOrderDescription());
        order.setOrderDate(orderDto.getOrderDate());
        order.setOrderAddress(orderDto.getOrderAddress());
        order.setStatus(OrderStatus.WAITING_FOR_EXPERT_PROPOSALS);
        order.setExecutionDate(orderDto.getExecutionDate());
        order.setOfferCustomerPrice(orderDto.getOfferCustomerPrice());

        orderRepository.save(order);
        log.info("Order with ID: {} updated successfully", orderId);
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    /**
     * Deletes a specified order for the authenticated customer.
     *
     * <p>Validates customer authentication and ensures the order status allows deletion.
     * Also checks that the order belongs to the authenticated customer.</p>
     *
     * @param orderId the ID of the order to delete
     * @param email the customer's email for authentication
     * @param password the customer's password for authentication
     * @throws CustomException if validation fails or the order cannot be deleted
     */
    @Override
    public void deleteOrder(Long orderId, String email, String password) {
        log.info("Attempting to delete order with ID: {} for customer with email: {}", orderId, email);

        User user = userService.findByEmailAndPassword(email, password);
        if (user == null) {
            log.error("User not found with email: {}", email);
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }

        if (!(user instanceof Customer customer)) {
            log.error("Unauthorized delete attempt by user with email: {}", email);
            throw new CustomException("Only customers can delete orders", ErrorCode.UNAUTHORIZED.getCode());
        }

        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved to delete orders", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        log.info("Validating order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(
                        "Order not found with order ID: " + orderId, ErrorCode.NOT_FOUND.getCode()));

        if (order != null && order.getStatus() != WAITING_FOR_EXPERT_PROPOSALS) {
            log.error("Order with ID: {} cannot be deleted because its status is not valid for deletion", orderId);
            throw new CustomException("You cannot delete this order because it has already been used.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        if (order == null || !order.getCustomer().equals(customer)) {
            log.error("Unauthorized attempt to delete order with ID: {} by customer with email: {}", orderId, email);
            throw new CustomException("This order does not belong to the customer", ErrorCode.UNAUTHORIZED.getCode());
        }
        log.info("Deleting order with ID: {}", orderId);
        orderRepository.delete(order);
        log.info("Order with ID: {} deleted successfully", orderId);
    }
}