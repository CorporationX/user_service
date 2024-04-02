package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserAvatarValidatorTest {

    UserAvatarValidator userAvatarValidator = new UserAvatarValidator();

    private String contentType;

    @BeforeEach
    void setUp() {
        contentType = "image/jpg";
    }

    @Test
    void validateIfAvatarIsImage_AvatarIsImage_ShouldNotThrow() {
        assertDoesNotThrow(() -> userAvatarValidator.validateIfAvatarIsImage(contentType));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"video/mp4", "audio/wav", "text/txt"})
    void validateIfAvatarIsImage_AvatarIsNotImage_ShouldThrowDataValidationException(String invalidContentType) {
        assertThrows(DataValidationException.class,
                () -> userAvatarValidator.validateIfAvatarIsImage(invalidContentType));
    }
}
