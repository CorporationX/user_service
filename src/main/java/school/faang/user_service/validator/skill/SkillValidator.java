package school.faang.user_service.validator.skill;

import com.github.dockerjava.api.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validSkills(List<Long> skills, long userId) {
        if (skills == null || skills.isEmpty()) {
            log.error("Идентификаторы навыков отсутствуют или пусты для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Список навыков не может быть пустым");
        }
    }

    public void validate(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }

        if (!new HashSet<>(existingSkillIds).containsAll(skillIds)) {
            throw new BadRequestException("Некоторых навыков не существует");
        }
    }
}
