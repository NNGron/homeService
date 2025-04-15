package ir.dto.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.offer.OfferResponseDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import ir.entity.enums.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComprehensiveResponseDto {
    // User Information
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private UserStatus userStatus;
    private UserType userType;
    private Role role;

    // Order Information
    private Long orderNumber;
    private BigDecimal finalPrice;
    private LocalDate orderDate;
    private LocalDate executionDate;
    private LocalDateTime orderStartDate;
    private LocalDateTime orderEndDate;
    private String orderAddress;
    private String orderDescription;
    private OrderStatus orderStatus;
    private BigDecimal offerCustomerPrice;
    private LocalDateTime startWorkingTime;
    private LocalDateTime endWorkingTime;
    private LocalDate startWorkingDate;
    private LocalDate endWorkingDate;

    // Offers
    private List<OfferResponseDto> offers;

    // Expert Rating
    private Double expertRating;

    // Category Details
    private String categoryName;
    private String categoryDescription;
    private BigDecimal categoryBasePrice;
    private String parentCategoryName;

    // Reviews
    private List<ReviewResponseDto> reviews;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime updatedAtStart;
    private LocalDateTime updatedAtEnd;
}
