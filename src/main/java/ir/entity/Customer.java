package ir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

/**
 * Represents a customer within the system.
 *
 * <p>The {@code Customer} class extends the {@link User} class, inheriting
 * common attributes and functionality, and introduces specific behaviors
 * and relationships relevant to customers.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Associates customers with the orders they have placed through a one-to-many relationship.</li>
 *     <li>Inherits base user properties such as first name, last name, email, and more from {@link User}.</li>
 *     <li>Uses Lombok annotations to generate boilerplate code for getters, setters, constructors, and equality checks.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@code orders} - A one-to-many relationship representing all orders placed by the customer.</li>
 * </ul>
 *
 * <p>This class is primarily used to manage and represent customer-related data.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer extends User {

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}