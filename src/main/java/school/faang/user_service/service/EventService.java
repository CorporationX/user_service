package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.filter_dto.EventFilterDto;

import java.util.List;

public interface EventService {
    @Transactional
    List<EventDto> getFilteredEvents(EventFilterDto filterDto, Long callingUserId);

    void clearPastEvents();

    void setBatchSize(int batchSize);
}
