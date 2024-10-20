package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.dto.RequestFilterDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    RequestFilter toEntity(RequestFilterDto dto);

    RequestFilterDto toDto(RequestFilter entity);
}

