package ir.convert.user;

import ir.convert.category.category_response.CategoryEntityToCategoryResponseDtoConvertor;
import ir.dto.category.CategoryResponseDto;
import ir.dto.user.UserResponseDto;
import ir.entity.Category;
import ir.entity.Expert;
import ir.entity.User;
import ir.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Converter to transform a {@link User} entity into a {@link UserResponseDto}.
 * This class handles both single user conversions and batch conversions for lists of users.
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserEntityToUserResponseDtoConvertor implements Function<User, UserResponseDto> {

    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;

    @Override
    public UserResponseDto apply(User user) {
        log.debug("Converting User entity with username: {}", user.getUserName());

        List<CategoryResponseDto> categoryResponseDtos = null;
        Double averageRate = null;

        BigDecimal balance = (user.getWallet() == null || user.getWallet().getCredit() == null)
                ? BigDecimal.ZERO : user.getWallet().getCredit();
        // BigDecimal balance = user.getWallet().getCredit() == null ? BigDecimal.ZERO : user.getWallet().getCredit();

        if (user instanceof Expert expert) {
            log.debug("User is an Expert with username: {}", expert.getUserName());

            averageRate = expert.getRating() == null ? 0 : expert.getRating();
            List<Category> categoryList = expert.getCategories();
            categoryResponseDtos = categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
        }

        log.info("Successfully converted User entity with username: {}", user.getUserName());
        return UserResponseDto
                .builder()
                .userName(user.getUserName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .address(user.getAddress())
                .balance(balance)
                .status(user.getStatus())
                .averageRate(averageRate)
                .categories(categoryResponseDtos)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .userType(UserType.valueOf(user.getClass().getSimpleName().toUpperCase()))
                .build();
    }

    public List<UserResponseDto> convertList(List<User> users) {
        log.debug("Starting batch conversion for {} User entities", users.size());

        log.info("Successfully converted {} User entities to DTOs", users.size());
        return users.stream()
                .map(this)
                .toList();
    }
}