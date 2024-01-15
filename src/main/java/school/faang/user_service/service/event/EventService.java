package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    private final SkillMapper skillMapper;

    public EventDto create(EventDto eventDto) {
        checkUserSkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }


    private void checkUserSkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("User by ID: " + eventDto.getOwnerId() + " not found"));

        List<SkillDto> userSkills = skillMapper.toListSkillDto(user.getSkills());

        if (!userSkills.containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidationException("User by ID: " + eventDto.getOwnerId() + " does not possess all required skills for this event");
        }
    }


}



