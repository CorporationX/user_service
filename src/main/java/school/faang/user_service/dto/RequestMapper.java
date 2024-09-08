package school.faang.user_service.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    RequestFilter toEntity(RequestFilterDto dto);

    RequestFilterDto toDto(RequestFilter entity);
}

