package ir.convert.user;


import ir.convert.category.categoryResponse.CategoryEntityToCategoryResponseDtoConvertor;
import ir.dto.category.CategoryResponseDto;
import ir.dto.user.UserResponseDto;
import ir.entity.Category;
import ir.entity.Expert;
import ir.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class UserEntityToUserResponseDtoConvertor implements Function<User, UserResponseDto> {

    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;

    @Override
    public UserResponseDto apply(User user) {
        List<CategoryResponseDto> categoryResponseDtos = null;
        Double averageRate = null;
        BigDecimal balance = user.getCredit() == null ? BigDecimal.ZERO : user.getCredit();

        if (user instanceof Expert expert) {
            averageRate = expert.getRating() == null ? 0 : expert.getRating();
            List<Category> categoryList = expert.getCategories();
            categoryResponseDtos = categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
        }

        return UserResponseDto
                .builder()
                .userName(user.getUserName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .balance(balance)
                .status(user.getStatus())
                .averageRate(averageRate)
//                .photo(photo)
                .categories(categoryResponseDtos)
                .build();
    }

    public List<UserResponseDto> convertList(List<User> users) {
        return users.stream()
                .map(this)
                .toList();
    }
}