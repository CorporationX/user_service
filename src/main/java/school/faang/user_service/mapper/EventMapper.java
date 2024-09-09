package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "toIdLoh")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "toId")
    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);

    @Named("toId")
    default User toId(EventDto eventDto, @Context UserRepository userRepository) {
        return userRepository.findById(eventDto.getOwnerId()).get();
    }

    @Named("toIdLoh")
    default Long toIdLoh(User user, @Context UserRepository userRepository) {
        return user.getId();
    }
}
