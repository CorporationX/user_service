package school.faang.user_service.serice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.CommonException;
import school.faang.user_service.exception.OperationStatus;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        User user = new User();
        user.setId(userId);
        if (!eventParticipationRepository.findAllParticipantsByEventId(eventId).contains(user)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new CommonException(OperationStatus.ALREADY_REGISTERED_USER_ERROR.getDescription(),
                    OperationStatus.ALREADY_REGISTERED_USER_ERROR);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        User user = new User();
        user.setId(userId);
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId).contains(user)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new CommonException(OperationStatus.IS_NOT_REGISTERED_USER_ERROR.getDescription(),
                    OperationStatus.IS_NOT_REGISTERED_USER_ERROR);
        }
    }

    public List<User> getParticipant(long eventId){
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
