package school.faang.user_service.service.user.parse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.Person;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;

@ExtendWith(MockitoExtension.class)
public class CsvParserTest {

    @InjectMocks
    private CsvParser csvParser;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(csvParser, "THREAD_POOL_SIZE", 4);
    }

    @Nested
    class NegativeTests {

        @DisplayName("should return empty list when passed")
        @Test
        void multiParseCsvWhenEmptyListTest() {
            List<InputStream> parts = Collections.emptyList();
            List<Person> result = csvParser.multiParseCsv(parts);
            assertTrue(result.isEmpty());
        }

        @DisplayName("should throw exception when thread interrupted")
        @Test
        public void multiParseCsvWhenInterruptedThreadTest() {
            List<InputStream> parts = Collections.singletonList(new ByteArrayInputStream(new byte[0]));
            Thread.currentThread().interrupt();
            assertThrows(RuntimeException.class,
                    () -> csvParser.multiParseCsv(parts));
        }

        @DisplayName("should throw exception when data is not appropriate for csv format")
        @Test
        public void multiParseCsvWhenCorruptedStreamTest() {
            InputStream corruptedStream = new ByteArrayInputStream("\n\\n".getBytes());
            List<InputStream> parts = List.of(corruptedStream);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> csvParser.multiParseCsv(parts));
            assertEquals(INPUT_OUTPUT_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}
