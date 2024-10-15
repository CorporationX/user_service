package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.dto.RequestFilter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RejectionMapper {
    Rejection toEntity(RejectionDto dto);

    RejectionDto toDto(RequestFilter entity);
}
