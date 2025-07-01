package school.faang.user_service.service.user_skill_guarantee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.skill.UserSkillGuarantee;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public List<UserSkillGuarantee> saveAll(List<UserSkillGuarantee> userSkillGuarantees) {
        return userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }
}
