package school.faang.user_service.service;

import school.faang.user_service.model.entity.User;

import java.util.List;

public interface EventParticipationService {
    int getParticipantsCount(long eventId);

    List<User> getParticipant(long eventId);

    void manageParticipation(long eventId, long userId, boolean isRegistering);
}
