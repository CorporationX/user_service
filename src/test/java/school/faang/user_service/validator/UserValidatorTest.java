package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    private static final long MAX_PIC_SIZE = 5_242_880L;

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private User user;

    @Mock
    private UserProfilePic userProfilePic;

    private Long userId;

    @BeforeEach
    public void setUd() {
        userId = 1L;
    }

    @Test
    public void testValidateUserInDBWitchIsExists() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> userValidator.validateUserExists(userId));
    }

    @Test
    public void testValidateUserInDBWitchIsNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> userValidator.validateUserExists(userId));
    }

    @Test
    public void testCheckMaxSizePic_FileTooLarge() {
        when(multipartFile.getSize()).thenReturn(MAX_PIC_SIZE + 1);

        assertThrows(DataValidationException.class, () -> {
            userValidator.checkMaxSizePic(multipartFile);
        });
    }

    @Test
    public void testCheckExistPicId_ProfilePicIsnull() {
        when(user.getUserProfilePic()).thenReturn(null);
        assertThrows(DataValidationException.class, () -> userValidator.checkExistPicId(user));
    }

    @Test
    public void testCheckExistPicId_FileIdIsnull() {
        when(user.getUserProfilePic()).thenReturn(userProfilePic);
        when(userProfilePic.getFileId()).thenReturn(null);
        assertThrows(DataValidationException.class, () -> userValidator.checkExistPicId(user));
    }
}