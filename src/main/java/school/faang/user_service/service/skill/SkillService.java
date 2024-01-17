package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skil.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillDto create (SkillDto skill) {
        boolean skillExistsInDB = skillRepository.existsByTitle(skill.getTitle());

        if (skillExistsInDB) {
            throw new DataValidationException("Skill with name " + skill.getTitle() + " already exists in database.");
        }

        Skill skillEntity = skillMapper.toEntity(skill);
        List<User> users = userRepository.findAllById(skill.getUserIds());
        skillEntity.setUsers(users);

        skillEntity = skillRepository.save(skillEntity);

        return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills (long userId) {

        List<Skill> skillEntityList = skillRepository.findAllByUserId(userId);

        if (skillEntityList == null) {
            throw new DataValidationException("No skills found by user id " + userId);
        }

        for (Skill skill : skillEntityList) {
            SkillDto skillDto = skillMapper.toDto(skill);
            List<User> users = userRepository.findAllById(skillDto.getUserIds());
            skill.setUsers(users);
        }

        return skillMapper.listToDto(skillEntityList);
    }
}
