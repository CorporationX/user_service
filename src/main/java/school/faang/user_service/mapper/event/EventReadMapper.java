package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.Mapper;
import school.faang.user_service.mapper.UserReadMapper;

@Component
@RequiredArgsConstructor
public class EventReadMapper implements Mapper<Event, EventReadDto> {

    private final UserReadMapper userReadMapper;

    @Override
    public EventReadDto map(Event object) {
        return new EventReadDto(
                object.getId(),
                object.getTitle(),
                object.getStartDate(),
                object.getEndDate(),
                getDto(object.getOwner(), userReadMapper),
                object.getDescription(),
                object.getLocation(),
                object.getMaxAttendees()
        );
    }
}
