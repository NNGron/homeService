//package ir.unit.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ir.controller.UserController;
//import ir.dto.user.UserRequestDto;
//import ir.dto.user.UserResponseDto;
//import ir.entity.enums.UserType;
//import ir.service.user.UserServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class UserControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock  // Mock the service layer
//    private UserServiceImpl userService;
//
//    @InjectMocks  // Inject mock into controller
//    private UserController userController;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    //@Test
//    void testSaveUser() throws Exception {
//        UserRequestDto userRequestDto = new UserRequestDto();
//        userRequestDto.setFirstName("firstName");
//        userRequestDto.setLastName("lastName");
//        userRequestDto.setEmail("email@gmail.com");
//        userRequestDto.setPassword("password123");
//        userRequestDto.setAddress("Tehran");
//        userRequestDto.setUserName("email@gmail.com");
//        userRequestDto.setUserType(UserType.CUSTOMER);
//
//
//        UserResponseDto userResponseDto = UserResponseDto
//                .builder()
//                .firstName("firstName")
//                .lastName("lastName")
//                .email("email@gmail.com")
//                .userName("email@gmail.com").build();
//
//        when(userService.saveUser(any(UserRequestDto.class))).thenReturn(userResponseDto);
//
//        mockMvc.perform(post("/v1/users/signup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("email@gmail.com"));
//    }
//
//}
