package school.faang.user_service.requestformentoring.helper.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.requestformentoring.helper.exeptions.ListIsEmptyException;
import school.faang.user_service.requestformentoring.helper.exeptions.NotNullProvidedException;
import school.faang.user_service.requestformentoring.helper.exeptions.SelfRequestException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationUserTest {
    @InjectMocks
    private ValidationUser validationUser;

    @Test
    void checkUserEqualsUserThrowsException() {
        long n1 = 1;
        long n2 = 1;
        assertEquals("Пользователь не может отправить запрос сам себе",
                assertThrows(SelfRequestException.class, () ->
                        validationUser.checkUserEqualsUser(n1, n2))
                        .getMessage());
    }

    @Test
    void checkUserEqualsUserDoesNotThrows() {
        long n1 = 1;
        long n2 = 2;
        assertDoesNotThrow(() -> validationUser.checkUserEqualsUser(n1, n2));
    }

    @Test
    void testCheckOneTypeToNullThrowsException() {
        String str = null;
        assertEquals(str + " не может быть null",
                assertThrows(NotNullProvidedException.class, () ->
                        validationUser.checkOneTypeToNull(str))
                        .getMessage());
    }

    @Test
    void testCheckOneTypeToNullDoesNotThrow() {
        assertDoesNotThrow(() -> validationUser.checkOneTypeToNull("str"));
    }

}