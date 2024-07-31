package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class EventValidator {
        private final EventRepository eventRepository;
        private final SkillRepository skillRepository;
        private final UserRepository userRepository;
        private final SkillMapper skillMapper;

        public User ownerValidation(EventDto eventDto) {
            return userRepository.findById(eventDto.getOwnerId())
                    .orElseThrow(() -> new DataValidationException("Ошибка: пользователь не найден"));
        }

        public List<Skill> skillValidation(EventDto eventDto) {
            List<SkillDto> listSkillDto = eventDto.getRelatedSkills();
            List<Skill> eventSkillList = skillMapper.toEntityList(listSkillDto);
            return eventSkillList.stream()
                    .peek(skill -> {
                        if (!skillRepository.findById(skill.getId()).isPresent()) {
                            throw new DataValidationException("Ошибка: навык с ID " + skill.getId() + " не найден");
                        }
                    })
                    .collect(Collectors.toList());
        }

        public void inputDataValidation(EventDto eventDto) {
            User validatedOwner = ownerValidation(eventDto);
            List<Skill> validatedSkillList = skillValidation(eventDto);
            if (!validatedSkillList.stream()
                    .allMatch(skill -> skill.getUsers().contains(validatedOwner))) {
                throw new DataValidationException("Ошибка: пользователь не обладает всеми необходимыми навыками");
            }
        }
        public void eventValidation (Long eventId){
            if (!eventRepository.existsById(eventId)) {
                throw new ResourceNotFoundException("Ошибка: события id" + eventId + " не существует");
            }
        }

    }
