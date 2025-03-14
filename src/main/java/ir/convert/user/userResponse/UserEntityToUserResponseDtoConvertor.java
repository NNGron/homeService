package ir.convert.user.userResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


import ir.dto.user.UserResponseDto;
import ir.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class UserEntityToUserResponseDtoConvertor implements Function<User, UserResponseDto> {

    @Override
    public UserResponseDto apply(User user) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(user, UserResponseDto.class);
    }

    public List<UserResponseDto> convertList(List<User> categories) {
        return categories.stream()
                .map(this)
                .toList();
    }
}