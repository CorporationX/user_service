package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.protobuf.generate.FollowerEventProto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FollowerEventMapper extends DateTimeMapper {

    FollowerEventProto.FollowerEvent toFollowerEvent(FollowerEvent event);
}
