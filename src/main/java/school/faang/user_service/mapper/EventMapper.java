package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapToOwnerId")
    @Mapping(source = "event.relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapToOwnerUser")
    @Mapping(source = "eventDto.relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapToOwnerId")
    @Mapping(source = "event.relatedSkills", target = "relatedSkills")
    List<EventDto> toDtoList(List<Event> event);

    @Named("mapToOwnerId")
    default Long mapToOwnerId(User owner) {
        return owner.getId();
    }

    @Named("mapToOwnerUser")
    default User mapToOwnerId(Long id) {
        return User.builder().id(id).build();
    }
}
