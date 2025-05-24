//package ir.unit.user;
//
//import ir.convert.user.UserEntityToUserResponseDtoConvertor;
//import ir.dto.user.UserRequestDto;
//import ir.dto.user.UserResponseDto;
//import ir.entity.Customer;
//import ir.entity.User;
//import ir.entity.enums.UserType;
//import ir.repository.UserRepository;
//import ir.service.category.CategoryService;
//import ir.service.user.UserServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private CategoryService categoryService;
//    @Mock
//    private  UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void saveUserTest() {
//        //given
//        UserRequestDto userRequestDto = new UserRequestDto();
//        userRequestDto.setFirstName("firstName");
//        userRequestDto.setLastName("lastName");
//        userRequestDto.setEmail("email");
//        userRequestDto.setPassword("password123");
//        userRequestDto.setAddress("Tehran");
//        userRequestDto.setUserType(UserType.CUSTOMER);
//
//        User user = new Customer();
//        user.setFirstName("firstName");
//        user.setLastName("lastName");
//        user.setEmail("email");
//        user.setPassword("password123");
//        user.setAddress("Tehran");
//
//        UserResponseDto userResponseDto = UserResponseDto
//                .builder()
//                .firstName("firstName")
//                .lastName("lastName")
//                .email("email")
//                .userName("userName").build();
//
//
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userEntityToUserResponseDtoConvertor.apply(any(User.class))).thenReturn(userResponseDto);
//
//        UserResponseDto responseDto = userService.saveUser(userRequestDto);
//
//        assertNotNull(responseDto);
//        assertEquals("email", responseDto.getEmail());
//
//    }
//
//}
