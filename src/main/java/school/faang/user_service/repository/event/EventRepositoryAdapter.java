package school.faang.user_service.repository.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.NotFoundException;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryAdapter {
    private final EventRepository eventRepository;

    public Event findById(long id) {
        log.debug("Execution of the method Event findById, parameters: id = {}", id);
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User with id - %d not found", id)));
    }
}
