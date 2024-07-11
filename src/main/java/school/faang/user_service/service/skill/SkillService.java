package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.Exceptions;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private SkillRepository skillRepository;
    private UserRepository userRepository;
    private SkillMapper skillMapper;
    private Exceptions exception;

    public List<SkillDto> getUserSkills(Long userId) {
        Optional.of(userRepository.existsById(userId)).orElseThrow(exception::notFindOwnerById);

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoList(ownerSkillsList);
    }
}