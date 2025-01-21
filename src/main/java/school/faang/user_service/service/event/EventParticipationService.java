package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void eventValidationExist(long eventID) throws DataValidationException {
        int eventCountById = eventParticipationRepository.countParticipants(eventID);
        if (eventCountById == 0) {
            throw new DataValidationException("Событие не найдено!");
        }
    }

    public void userValidationExist(long userID) throws DataValidationException {
        List<User> participationList = eventParticipationRepository.findAllParticipantsByEventId(userID);
        boolean userWasRegister = participationList.stream().anyMatch(user -> user.getId() == userID);
        if (userWasRegister) {
            throw new DataValidationException("Пользователь уже был зарегистрирован на событии!");
        }
    }

    public void userNotFound(long eventId) throws DataValidationException {
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId) == null) {
            throw new DataValidationException("Пользователи не найдены!");
        }
    }

    @Transactional
    public void registerParticipant(long eventID, long userID) {
        eventValidationExist(eventID);
        userValidationExist(userID);
        eventParticipationRepository.register(eventID, userID);
    }

    @Transactional
    public void unregisterParticipant(long eventID, long userID) {
        eventValidationExist(eventID);
        List<User> participationList = eventParticipationRepository.findAllParticipantsByEventId(eventID);
        boolean userWasRegister = participationList.stream().anyMatch(user -> user.getId() == userID);
        if (!userWasRegister) {
            throw new DataValidationException("Пользователь не был зарегистрирован на событии!");
        }
        eventParticipationRepository.unregister(eventID, userID);
    }

    @Transactional
    public List<User> getParticipant(long eventId) {
        eventValidationExist(eventId);
        userNotFound(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    @Transactional
    public int getParticipantCounts(long eventId) {
        eventValidationExist(eventId);
        userNotFound(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}