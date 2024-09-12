package school.faang.user_service.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RejectionMapper {
    Rejection toEntity(RejectionDto dto);

    RejectionDto toDto(RequestFilter entity);
}
