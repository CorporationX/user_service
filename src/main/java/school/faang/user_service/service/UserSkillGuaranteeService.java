package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public UserSkillGuarantee saveUserSkillGuarantee(Skill skill, User receiver, User author) {
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .skill(skill)
                .guarantor(author)
                .user(receiver)
                .build();

        return userSkillGuaranteeRepository.save(userSkillGuarantee);
    }
}
