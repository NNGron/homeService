package ir.convert.user.userResponse;


import ir.convert.category.categoryResponse.CategoryEntityToCategoryResponseDtoConvertor;
import ir.dto.category.CategoryResponseDto;
import ir.dto.user.UserResponseDto;
import ir.entity.Category;
import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class UserEntityToUserResponseDtoConvertor implements Function<User, UserResponseDto> {
    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;

    @Override
    public UserResponseDto apply(User user) {
        List<CategoryResponseDto> categoryResponseDtos = null;
        Double balance = null;
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            balance = customer.getCredit();
        }
        if (user instanceof Expert) {
            Expert expert = (Expert) user;
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
                .categories(categoryResponseDtos)
                .build();
    }

    public List<UserResponseDto> convertList(List<User> users) {
        return users.stream()
                .map(this)
                .toList();
    }
}