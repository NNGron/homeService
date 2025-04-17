//package ir.service.user;
//
//import ir.entity.Customer;
//import ir.entity.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//
//class UserServiceImplTest {
//
//    @Mock
//    private UserServiceImpl userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void itShouldCustomerFindByUserNameTest() {
//        //given
//        String username = "Customer1";
//        User user = new Customer();
//
//        //when
//        Mockito.when(userService.findByUserName(anyString())).thenReturn(Optional.of(user));
//
//        //then
//        assertEquals(Optional.of(user), userService.findByUserName(username));
//    }
//}