package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.protobuf.generate.ProfilePicEventProto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfilePicEventMapper extends DateTimeMapper {

    ProfilePicEventProto.ProfilePicEvent toProto(ProfilePicEvent event);
}