package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillMapper.class)
public interface EventMapper {

    @Mapping(target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "userToUserId")
    EventDto toDto(Event event);

    List<EventDto> toDtoList(List<Event> events);

    @Named("userToUserId")
    static Long userToUserId(User user) {
        return user.getId();
    }
}
