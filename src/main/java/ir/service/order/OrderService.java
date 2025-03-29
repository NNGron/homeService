package ir.service.order;

import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Category;
import ir.entity.Order;
import ir.entity.enums.OrderStatus;

import java.util.List;

/**
 * Service interface for managing orders.
 *
 * <p>This interface provides methods for creating, retrieving, updating,
 * deleting, and managing the payment and status of orders.</p>
 */
public interface OrderService {

    /**
     * Saves a new order for the authenticated user.
     *
     * @param orderDto the details of the order to be saved
     * @param email    the user's email for authentication
     * @param password the user's password for authentication
     * @return the saved order as a {@link OrderResponseDto}
     */
    OrderResponseDto saveOrder(OrderRequestDto orderDto,
                               String email,
                               String password);

    /**
     * Retrieves a list of orders for a customer using their email and password.
     *
     * <p>This method authenticates the customer based on their email and password,
     * and fetches all orders associated with the customer's email. It ensures
     * that the user is valid and is a registered customer before proceeding.</p>
     *
     * @param email    the customer's email for authentication
     * @param password the customer's password for authentication
     * @return a list of {@link OrderResponseDto} representing the customer's orders
     */
    List<OrderResponseDto> getOrdersByCustomerEmail(String email, String password);

    /**
     * Finds orders based on the given categories.
     *
     * @param categories the list of categories to filter orders by
     * @return a list of {@link Order} entities matching the categories
     */
    List<Order> findOrdersByCategories(List<Category> categories);

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @return a list of {@link OrderResponseDto} representing the user's orders
     */
    List<OrderResponseDto> getOrder(String email, String password);

    /**
     * Retrieves an order by its unique ID.
     *
     * @param id the unique ID of the order to retrieve
     * @return the {@link Order} entity matching the given ID
     */
    Order findById(Long id);

    /**
     * Saves the provided order.
     *
     * @param order the {@link Order} entity to be saved
     * @return the saved {@link Order} entity
     */
    Order save(Order order);

    /**
     * Updates the status of a specified order.
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @param orderStatus the new status to set for the order
     * @param orderNumber the number of the order to update
     * @param offerNumber the number of the offer linked to the order
     * @return the updated order as a {@link OrderResponseDto}
     */
    OrderResponseDto updateOrderStatus(String email, String password,
                                       OrderStatus orderStatus,
                                       Long orderNumber, Long offerNumber);

    /**
     * Marks a specified order as paid.
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @param orderNumber the number of the order to update
     * @param offerNumber the number of the offer linked to the order
     * @return the updated order as a {@link OrderResponseDto}
     */
    OrderResponseDto paid(String email,
                          String password,
                          Long orderNumber,
                          Long offerNumber);

    /**
     * Updates the details of a specified order.
     *
     * @param orderNumber the number of the order to update
     * @param orderDto the new details of the order
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @return the updated order as a {@link OrderResponseDto}
     */
    OrderResponseDto updateOrder(Long orderNumber,
                                 OrderRequestDto orderDto,
                                 String email,
                                 String password);

    /**
     * Deletes a specified order for the authenticated user.
     *
     * @param orderId the ID of the order to delete
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     */
    void deleteOrder(Long orderId, String email, String password);
}
