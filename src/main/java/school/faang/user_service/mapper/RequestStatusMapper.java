package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.entity.RequestStatus;

@Mapper(componentModel = "spring")
public interface RequestStatusMapper {

    @Mapping(target = "status", expression = "java(requestStatus.name())")
    RequestStatusDto requestStatusToRequestStatusDto(RequestStatus requestStatus);

    default RequestStatus requestStatusDtoToRequestStatus(RequestStatusDto requestStatusDto) {
        if (requestStatusDto == null || requestStatusDto.getStatus() == null) {
            return null;
        }
        return RequestStatus.valueOf(requestStatusDto.getStatus());
    }
}
