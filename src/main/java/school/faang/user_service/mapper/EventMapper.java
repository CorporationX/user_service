package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(source = "attendees", target = "attendees", qualifiedByName = "toIds")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(source = "attendees", target = "attendees",  ignore = true)
    Event toEntity(EventDto eventDto);

    @Named("toIds")
    default List<Long> userToId(List<User> attendees) {
        return attendees.stream().map(User::getId).toList();
    }

    @Named("toUsers")
    default List<User> idToUsers(List<Long> attendees) {
        return attendees.stream().map(userId -> User.builder().id(userId).build()).toList();
    }


}
