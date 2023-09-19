package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.commonMessages.ErrorMessagesForEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RegistrationUserForEventException;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.text.MessageFormat;
import java.util.List;

import static school.faang.user_service.commonMessages.ErrorMessagesForEvent.USER_IS_ALREADY_REGISTERED_FORMAT;
import static school.faang.user_service.commonMessages.ErrorMessagesForEvent.USER_IS_NOT_REGISTERED_FORMAT;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final MapperUserDto mapper;

    @Transactional
    public void registerParticipant(Long eventId, Long userId) {
        validateInputData(eventId, userId);
        List<User> users = getParticipantsByEventId(eventId);

        if (isUserRegisteredForEvent(users, userId)) {
            String errorMessage = MessageFormat.format(USER_IS_ALREADY_REGISTERED_FORMAT, userId, eventId);
            throw new RegistrationUserForEventException(errorMessage);
        }

        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(Long eventId, Long userId) {
        validateInputData(eventId, userId);
        List<User> users = getParticipantsByEventId(eventId);

        if (!isUserRegisteredForEvent(users, userId)) {
            String errorMessage = MessageFormat.format(USER_IS_NOT_REGISTERED_FORMAT, userId, eventId);
            throw new RegistrationUserForEventException(errorMessage);
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(Long eventId) {
        validateEventId(eventId);
        return getParticipantsByEventId(eventId).stream().map(mapper::toDto).toList();
    }

    public long getParticipantsCount(Long eventId) {
        validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private List<User> getParticipantsByEventId(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    private boolean isUserRegisteredForEvent(List<User> users, long userId) {
        return users.stream()
                .anyMatch(curUser -> curUser.getId() == userId);
    }

    private void validateInputData(Long eventId, Long userId) {
        validateEventId(eventId);
        validateUserId(userId);
    }

    private void validateEventId(Long eventId) {
        if (eventId == null) {
            String errorMessage = ErrorMessagesForEvent.EVENT_ID_IS_NULL;
            throw new RegistrationUserForEventException(errorMessage);
        }
        if (eventId < 0) {
            String errorMessage = ErrorMessagesForEvent.NEGATIVE_EVENT_ID;
            throw new RegistrationUserForEventException(errorMessage);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            String errorMessage = ErrorMessagesForEvent.USER_ID_IS_NULL;
            throw new RegistrationUserForEventException(errorMessage);
        }
        if (userId < 0) {
            String errorMessage = ErrorMessagesForEvent.NEGATIVE_USER_ID;
            throw new RegistrationUserForEventException(errorMessage);
        }
    }
}
