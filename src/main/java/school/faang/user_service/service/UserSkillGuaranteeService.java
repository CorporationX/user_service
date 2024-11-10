package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepo;
}
