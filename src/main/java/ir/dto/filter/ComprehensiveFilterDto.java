package ir.dto.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import ir.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComprehensiveFilterDto {
    // User filters
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private UserStatus userStatus;
    private UserType userType;
    private Role role;

    // Order filters
    private Long orderNumber;
    private BigDecimal finalPrice;
    private LocalDate orderStartDate; //orderDate
    private LocalDate orderEndDate;
    private LocalDate executionDate;
    private String orderAddress;
    private String orderDescription;
    private OrderStatus orderStatus;
    private BigDecimal offerCustomerPrice;

    private LocalDateTime startWorkingTime;
    private LocalDateTime endWorkingTime;

    private LocalDate startWorkingDate;
    private LocalDate endWorkingDate;

    // Offer filters
    private BigDecimal proposedPrice;
    private Integer estimatedTime;
    private LocalDateTime startTime;

    // Expert filters
    private Double expertRating;

    // Category filters
    private String categoryName;
    private String categoryDescription;
    private BigDecimal categoryBasePrice;
    private String parentCategoryName;

    // Review filters
    private Integer reviewRating;
    private String reviewComment;

    // BaseEntity filters (برای بازه زمانی)
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private LocalDateTime updatedAtStart;
    private LocalDateTime updatedAtEnd;
}
