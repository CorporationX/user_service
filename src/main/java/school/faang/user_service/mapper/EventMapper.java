package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mappings({
            @Mapping(source = "owner.id", target = "ownerId"),
            @Mapping(source = "relatedSkills", target = "relatedSkills")
    })
    EventDto toDto(Event event);

    @Mappings({
            @Mapping(source = "ownerId", target = "owner.id"),
            @Mapping(source = "relatedSkills", target = "relatedSkills")
    })
    Event toEntity(EventDto eventDto);
}
