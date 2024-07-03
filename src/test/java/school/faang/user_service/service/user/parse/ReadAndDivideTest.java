package school.faang.user_service.service.user.parse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_IS_EMPTY;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;
import static school.faang.user_service.service.user.parse.Util.getInputStream;

@ExtendWith(MockitoExtension.class)
public class ReadAndDivideTest {

    @InjectMocks
    private ReadAndDivide readAndDivide;

    @Nested
    class PositiveTests {

        @DisplayName("should return data with 4 elements when passed")
        @Test
        void testForToCsvPartDivider() throws FileNotFoundException {
            assertEquals(4, readAndDivide.toCsvPartDivider(getInputStream()).size());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should throw exception when reader.readLine()")
        @Test
        void toCsvPartDividerIfInputStreamIsCloseTest() throws IOException {
            InputStream inputStream = getInputStream();
            inputStream.close();
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> readAndDivide.toCsvPartDivider(inputStream));
            assertEquals(INPUT_OUTPUT_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when lines.isEmpty()")
        @Test
        void toCsvPartDividerIfInputIsEmptyTest() {
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> readAndDivide.toCsvPartDivider(new ByteArrayInputStream(new byte[0])));
            assertEquals(INPUT_IS_EMPTY.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when inputStream == null")
        @Test
        void toCsvPartDividerIfInputIsNullTest() {
            assertThrows(RuntimeException.class,
                    () -> readAndDivide.toCsvPartDivider(null));
        }
    }
}
