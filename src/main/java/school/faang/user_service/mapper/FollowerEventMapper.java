package school.faang.user_service.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.protobuf.generate.FollowerEventProto;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FollowerEventMapper {

    @Mapping(target = "eventTime", expression = "java(localDateTimeToTimestamp(event.getEventTime()))")
    FollowerEventProto.FollowerEvent toFollowerEvent(FollowerEvent event);

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
