package school.faang.user_service.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public void saveAll(@NonNull List<UserSkillGuarantee> guaranteeList) {
        userSkillGuaranteeRepository.saveAll(guaranteeList);
    }
}
