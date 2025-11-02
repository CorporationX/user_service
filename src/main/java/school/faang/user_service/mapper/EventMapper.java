package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventDto eventDto);

    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "mapOwner")
    void update(UpdateEventDto dto, @MappingTarget Event event);

    @Mapping(target = "ownerId", source = "owner.id")
    EventDto toEventDto(Event event);

    @Named("mapOwner")
    default User mapOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        User user = new User();
        user.setId(ownerId);
        return user;
    }
}
