package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;


    public EventDto create(EventDto event) {
        validate(event);
        return eventMapper.toDTO(eventRepository.save(eventMapper.toEvent(event)));
    }

    private void validate(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow(() -> new DataValidationException("Owner doesn't found"));
        if (!ownerHasSkills(event, user)) {
            throw new DataValidationException("Owner hasn't required skills");
        }
    }

    private boolean ownerHasSkills(EventDto event, User user) {

        return user.getSkills().stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet())
                .containsAll(
                        event.getRelatedSkills().stream()
                                .map(SkillDto::getTitle)
                                .collect(Collectors.toSet()));

    }

    public EventDto getEvent(long id){
        Optional<Event> event;

        try{
            event = eventRepository.findById(id);
        }catch (IllegalArgumentException exception){
            throw new DataValidationException("ID is incorrect");
        }
        if(event.isEmpty()){
            throw new DataValidationException("There is no event with this id");
        }

        return eventMapper.toDTO(event.get());
    }

    public void deleteEvent(long id){
        try {
            eventRepository.deleteById(id);
        }catch (IllegalArgumentException exception){
            throw new DataValidationException("ID is incorrect");
        }
    }
}
