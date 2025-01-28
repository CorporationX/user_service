package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.faang.user_service.dto.event.participant.EventParticipationDto;
import school.faang.user_service.dto.event.participant.UserParticipationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.partcipation.UserParticipationMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserParticipationMapper userParticipationMapper;

    @Transactional
    public void registerParticipation(EventParticipationDto dtoEventId, UserParticipationDto dtoUserId) throws DataValidationException {
        if (checkUserRegistration(dtoEventId.id(), dtoUserId.id())) {
            throw new DataValidationException("User already registered");
        }
        eventParticipationRepository.register(dtoEventId.id(), dtoUserId.id());
    }

    @Transactional
    public void unregisterParticipation(EventParticipationDto dtoEventId, UserParticipationDto dtoUserId) throws DataValidationException {
        if (!checkUserRegistration(dtoEventId.id(), dtoUserId.id())) {
            throw new DataValidationException("User was not registered for this event");
        }
        eventParticipationRepository.unregister(dtoEventId.id(), dtoUserId.id());
    }

    @Transactional
    public int getParticipantCount(EventParticipationDto dtoEventId) throws DataValidationException {
        List<UserParticipationDto> reg = userParticipationMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(dtoEventId.id()));
        if (reg.isEmpty()) {
            return 0;
        }
        return reg.size();
    }

    @Transactional
    public List<UserParticipationDto> getParticipant(EventParticipationDto eventId) throws DataValidationException {
        if (getParticipantCount(eventId) > 0) {
            throw new DataValidationException("Users list is empty");
        }
        return getPartcipantList(eventId.id());
    }

    private boolean checkUserRegistration(long eventId, long userId) {
        return getPartcipantList(eventId).stream().anyMatch(user -> user.id() == userId);
    }

    private List<UserParticipationDto> getPartcipantList(long eventId) {
        return userParticipationMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }
}