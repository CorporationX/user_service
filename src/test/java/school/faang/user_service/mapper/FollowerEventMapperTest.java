package school.faang.user_service.mapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FollowerEventMapperTest {

    private final FollowerEventMapperImpl mapper = new FollowerEventMapperImpl();

    @Test
    void testLocalDateTimeToTimestamp_withValidDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(
                2024,
                10,
                9,
                12,
                0,
                0,
                500
        );
        long expectedSeconds = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        int expectedNanos = localDateTime.getNano();

        Timestamp timestamp = mapper.localDateTimeToTimestamp(localDateTime);

        assertNotNull(timestamp);
        assertEquals(expectedSeconds, timestamp.getSeconds());
        assertEquals(expectedNanos, timestamp.getNanos());
    }

    @Test
    void testLocalDateTimeToTimestamp_withNullDateTime() {
        LocalDateTime localDateTime = null;

        Timestamp timestamp = mapper.localDateTimeToTimestamp(localDateTime);

        assertNull(timestamp);
    }
}