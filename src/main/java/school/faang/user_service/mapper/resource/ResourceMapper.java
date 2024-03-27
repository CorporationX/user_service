package school.faang.user_service.mapper.resource;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.entity.resource.Resource;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto resourceDto);

    List<ResourceDto> toDto(List<Resource> resources);
}
