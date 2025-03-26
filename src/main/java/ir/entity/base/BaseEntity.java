package ir.entity.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A base class for all entity classes in the application.
 *
 * <p>This abstract class serves as a foundation for other entity classes, providing a common
 * identifier field {@code id}. It uses the {@link MappedSuperclass} annotation, which means
 * this class is not a standalone entity itself but shares its mappings with any entity
 * class that extends it. This helps reduce redundancy and simplifies the design of entity models.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Defines a universal primary key {@code id} for all entities.</li>
 *     <li>Uses {@link GeneratedValue} with {@code GenerationType.IDENTITY} to generate unique IDs.</li>
 *     <li>Abstract class with the {@link MappedSuperclass} annotation, ensuring its fields are inherited
 *     by child classes without creating its own table in the database.</li>
 *     <li>Includes Lombok annotations like {@link Getter}, {@link Setter}, {@link NoArgsConstructor},
 *     and {@link AllArgsConstructor} to minimize boilerplate code.</li>
 * </ul>
 *
 * <p>By using {@link MappedSuperclass}, all child entities automatically inherit the {@code id}
 * field and its mapping configuration, streamlining the development process.</p>
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

















