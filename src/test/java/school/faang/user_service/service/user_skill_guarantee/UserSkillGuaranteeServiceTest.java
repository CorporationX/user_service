package school.faang.user_service.service.user_skill_guarantee;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.skill.UserSkillGuarantee;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserSkillGuaranteeServiceTest {
    private static final long USER_SKILL_GUARANTEE_ID = 1L;

    private static UserSkillGuarantee userSkillGuarantee;
    private static List<UserSkillGuarantee> userSkillGuarantees;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @BeforeAll
    static void init() {
        userSkillGuarantee = new UserSkillGuarantee();
        userSkillGuarantee.setId(USER_SKILL_GUARANTEE_ID);
        userSkillGuarantees = new ArrayList<>();
        userSkillGuarantees.add(userSkillGuarantee);
    }

    @Test
    void saveAll() {
        when(userSkillGuaranteeRepository.saveAll(any())).thenReturn(userSkillGuarantees);

        List<UserSkillGuarantee> savedUserSkillGuarantees = userSkillGuaranteeService.saveAll(userSkillGuarantees);

        assertEquals(userSkillGuarantees, savedUserSkillGuarantees);
        verify(userSkillGuaranteeRepository).saveAll(any());
    }
}
