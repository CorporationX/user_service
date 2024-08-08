package school.faang.user_service.controller;

//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.service.UserService;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    @Test
//    @DisplayName("testing deactivateUser userService deactivate deactivateUser method execution")
//    public void testDeactivateUserWithUserServiceExecution(){
//        long userId = 1L;
//        userController.deactivateUser(userId);
//        verify(userService, times(1)).deactivateUser(userId);
//    }
//}