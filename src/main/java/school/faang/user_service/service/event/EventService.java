package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.ListSkillMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final ListSkillMapper listSkillMapper;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        Long ownerId = eventDto.getOwnerId();
        Event event;
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new DataValidationException("Такого пользователя не существует!");
        } else if (eventDto.getRelatedSkills()
                .equals(listSkillMapper.listSkillToDto(owner.get().getSkills()))) {
            event = eventRepository.save(eventMapper.eventDtoToEntity(eventDto));
        } else {
            throw new DataValidationException("У пользователя нет необходимых навыков," +
                    " чтобы создать данное событие!");
        }
        return eventMapper.eventToDto(event);
    }

    public EventDto getEvent(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new DataValidationException("Такого события не существует!");
        }
        return eventMapper.eventToDto(optionalEvent.get());
    }
}
