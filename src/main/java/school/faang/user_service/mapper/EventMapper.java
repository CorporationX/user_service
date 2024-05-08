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
    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "getOwnerId")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "getOwner")
    Event toEntity(EventDto eventDto);

    List<EventDto> toEventsDto(List<Event> events);

    List<Event> toEvents(List<EventDto> eventsDto);

    @Named("getOwner")
    default User getOwner(Long ownerId) {  // Метод имитация получения владельца события из базы данных
        User user = new User();
        user.setId(ownerId);
        return user;
    }

    @Named("getOwnerId")
    default Long getOwnerId(User user) {
        return user.getId();
    }
}
