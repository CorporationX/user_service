package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "userToId")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    @Named("userToId")
    static Long userToId(User user) {
        return user.getId();
    }
}
