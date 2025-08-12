package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCleanupService {

    private final EventRepository eventRepository;

    @Async
    @Transactional
    public void deleteEventsBatch(List<Long> ids) {
        eventRepository.deleteByIds(ids);
    }
}
