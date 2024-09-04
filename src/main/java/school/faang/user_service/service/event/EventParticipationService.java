package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        if(eventParticipationRepository.eventExistsById(eventId)) {
            if (!isRegisteredParticipant(eventId, userId)) {
                eventParticipationRepository.register(eventId, userId);
                System.out.println("User " + userId + " has been registered with event " + eventId);
            } else {
                throw new IllegalArgumentException("User is already registered");
            }
        } throw new IllegalArgumentException("Event does not exist");
    }

    public void unregisterParticipant(long eventId, long userId) {
        if(eventParticipationRepository.eventExistsById(eventId)) {
            if (isRegisteredParticipant(eventId, userId)) {
                eventParticipationRepository.unregister(eventId, userId);
                System.out.println("User " + userId + " has been unregistered with event " + eventId);
            } else {
                throw new IllegalArgumentException("User is not registered for this event");
            }
        } throw new IllegalArgumentException("Event does not exist");
    }

    public List<EventUserDto> getParticipants(long eventId) {
        if (eventParticipationRepository.existsById(eventId)) {
            List<EventUserDto> userDtoList = userMapper.EventUserListToDto(eventParticipationRepository.findAllParticipantsByEventId(eventId));
            if (userDtoList != null && !userDtoList.isEmpty()) {
                return userDtoList;
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Event does not exist");
        }
    }

    public Integer getParticipantsCount(long eventId) {
        if (eventParticipationRepository.existsById(eventId)) {
            return eventParticipationRepository.countParticipants(eventId);
        } else {
            throw new IllegalArgumentException("Event does not exist");
        }
    }

    private boolean isRegisteredParticipant(long eventId, long userId) {
        User userForEventById = eventParticipationRepository.findParticipantById(eventId, userId);
        return userForEventById != null;
    }

}
