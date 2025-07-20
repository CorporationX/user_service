package school.faang.user_service.service.user.skill_guarantee;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserSkillGuaranteeServiceTest {

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private UserSkillGuaranteeServiceImpl userSkillGuaranteeService;

    @Test
    @DisplayName("Should delegate saving all user skill guarantees to repository")
    public void shouldSaveAllUserSkillGuarantees() {
        UserSkillGuarantee guarantee1 = new UserSkillGuarantee();
        UserSkillGuarantee guarantee2 = new UserSkillGuarantee();

        List<UserSkillGuarantee> guarantees = List.of(guarantee1, guarantee2);

        userSkillGuaranteeService.saveAll(guarantees);

        verify(userSkillGuaranteeRepository, times(1)).saveAll(guarantees);
    }
}