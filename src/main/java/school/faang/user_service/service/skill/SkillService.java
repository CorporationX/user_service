package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;

    public List<SkillDto> getUserSkills(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage());
        }

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoList(ownerSkillsList);
    }

}
