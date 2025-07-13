package school.faang.user_service.service.user.skill_guarantee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeServiceImpl implements UserSkillGuaranteeService {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Override
    public void saveAll(List<UserSkillGuarantee> guarantees) {
        userSkillGuaranteeRepository.saveAll(guarantees);
        log.info("Saved {} user-skill guarantees", guarantees.size());
    }
}
