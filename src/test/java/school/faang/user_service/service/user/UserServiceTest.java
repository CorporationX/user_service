package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.pic.PicProcessor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    @Mock
    MultipartFile multipartFile;

    @Mock
    PicProcessor picProcessor;

    private Long id;
    private User user;

    @BeforeEach
    public void setUp() {
        id = 1L;
        user = User.builder().id(1L).build();
    }

    @Test
    public void testGetPremiumUsers_IsRunFindPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .city("Rostov")
                .experience(500)
                .build();
        userService.getPremiumUsers(userFilterDto);
        verify(userRepository, times(1)).findPremiumUsers();
    }

    @Test
    public void testSavePic_NotUserInBd() {
        when(userRepository.findById(id)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> userService.uploadProfilePicture(id, multipartFile));
    }

    @Test
    public void testGetPic() {
        when(userRepository.findById(id)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> userService.downloadProfilePicture(id));
    }

    @Test
    public void testDeletePic() {
        when(userRepository.findById(id)).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> userService.deleteProfilePicture(id));
    }
}