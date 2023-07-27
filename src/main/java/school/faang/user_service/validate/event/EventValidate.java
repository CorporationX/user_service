package school.faang.user_service.validate.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.entity.Skill;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Component
@RequiredArgsConstructor
public class EventValidate {
    private  final UserRepository userRepository;

    public void validateEvent(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title must not be empty");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event must have a start date");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner Id must not be null");
        }
    }

    public void checkThatUserHasNecessarySkills(EventDto eventDto)  {
        List<Long> eventSkillIds = eventDto.getSkillIds();
        long ownerId = eventDto.getOwnerId();

        List<Long> userSkillIds = userRepository.findById(ownerId)
                .map(user -> user.getSkills().stream().map(Skill::getId).toList())
                .orElseThrow(() -> new DataValidationException("User with id " + ownerId + " not found"));

        if (!userSkillIds.containsAll(eventSkillIds)) {
            throw new DataValidationException("Not enough necessary skills for event");
        }
    }

//    public void checkThatUserHasNecessarySkills(EventDto eventDto) {
//        List<Long> eventSkillIds = eventDto.getSkillIds();
//        long ownerId = eventDto.getOwnerId();
//
//        User user = userRepository.findById(ownerId)
//                .orElseThrow(() -> new DataValidationException("User with id " + ownerId + " not found"));
//
//        List<Long> userSkillIds = user.getSkills().stream()
//                .map(Skill::getId)
//                .toList();
//
//        if (!userSkillIds.containsAll(eventSkillIds)) {
//            throw new DataValidationException("Not enough necessary skills for event");
//        }
//    }
}