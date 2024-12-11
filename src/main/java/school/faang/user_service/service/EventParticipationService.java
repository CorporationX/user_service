package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.user.ParticipantDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public void registerParticipant(ParticipantDto participantDto, EventDto eventDto) {
        if (isParticipantRegistered(participantDto, eventDto)) {
            throw new DataValidationException("Пользователь уже зарегистрирован на событие");
        }
        eventParticipationRepository.register(participantDto.id(), eventDto.id());
    }

    public void unregisterParticipant(ParticipantDto participantDto, EventDto eventDto) {
        if (isParticipantNotRegistered(participantDto, eventDto)) {
            throw new DataValidationException("Пользователь не был зарегистрирован на событие");
        }
        eventParticipationRepository.unregister(participantDto.id(), eventDto.id());
    }

    public List<ParticipantDto> getParticipants(long eventId) {
        return userMapper.toDtos(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isParticipantRegistered(ParticipantDto participantDto, EventDto eventDto) {
        User user = userService.getUserById(participantDto.id());
        List<Long> participantsIds = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()).stream()
                .map(User::getId)
                .toList();
        return participantsIds.contains(user.getId());
    }

    private boolean isParticipantNotRegistered(ParticipantDto participantDto, EventDto eventDto) {
        return !isParticipantRegistered(participantDto, eventDto);
    }
}
