package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;
    private final UserService userService;

    public SkillDto create (SkillDto skill) throws DataValidationException {
        getSkillFromDB(skill.getTitle());

        Skill skillEntity = skillMapper.toEntity(skill);
        List<User> users = userService.getUsersByIds(skill.getUserIds());

        skillEntity.setUsers(users);
        skillRepository.save(skillEntity);
        return skillMapper.toDto(skillEntity);
    }

    private void getSkillFromDB (String skillTitle) throws DataValidationException {
        if (skillRepository.existsByTitle(skillTitle)) {
            throw new DataValidationException("Skill with name " + skillTitle + " already exists in database.");
        }
    }
}
