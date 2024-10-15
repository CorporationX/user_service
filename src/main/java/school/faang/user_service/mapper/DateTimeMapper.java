package school.faang.user_service.mapper;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface DateTimeMapper {
    default Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .setNanos(localDateTime.getNano())
                .build();
    }
}
