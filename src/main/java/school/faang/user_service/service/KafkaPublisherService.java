package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalCompletedEvent;

public interface KafkaPublisherService {

    void sendMessage(GoalCompletedEvent message);
}
