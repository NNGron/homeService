//package ir.unit.user;
//
//
//import ir.entity.Customer;
//import ir.entity.User;
//import ir.entity.enums.UserStatus;
//import ir.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll(); // Clean database before each test
//    }
//
//   // @Test
//    void testSaveUser() {
//        User user = createUser();
//        user = userRepository.save(user);
//
//        assertNotNull(user.getId());
//        assertEquals("username1", user.getUserName());
//    }
//
//   // @Test
//    void testFindById() {
//        User user = createUser();
//        user = userRepository.save(user);
//        Optional<User> foundUser = userRepository.findById(user.getId());
//        assertTrue(foundUser.isPresent());
//        assertEquals("username1", foundUser.get().getUserName());
//    }
//
//    @Test
//    void testFindByEmail() {
//        User user = createUser();
//        userRepository.save(user);
//        Optional<User> foundUser = userRepository.findByEmail("user1@gmail.com");
//        assertTrue(foundUser.isPresent());
//        assertEquals("username1", foundUser.get().getUserName());
//    }
//
//    @Test
//    void testDeleteUser() {
//        User user = createUser();
//        user = userRepository.save(user);
//        userRepository.deleteById(user.getId());
//
//        Optional<User> deletedUser = userRepository.findById(user.getId());
//        assertFalse(deletedUser.isPresent());
//    }
//
//    private User createUser() {
//        User user = new Customer();
//        user.setUserName("username1");
//        user.setPassword("password1");
//        user.setEmail("user1@gmail.com");
//        user.setFirstName("firstname");
//        user.setLastName("lastName");
//        user.setAddress("Tehran");
//        user.setStatus(UserStatus.NEW);
//        return user;
//    }
//}
