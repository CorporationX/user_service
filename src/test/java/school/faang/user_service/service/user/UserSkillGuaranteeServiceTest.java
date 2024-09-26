package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSkillGuaranteeServiceTest {

    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Test
    @DisplayName("Успех при сохранении UserSkillGuarantee")
    public void whenSaveUserSkillGuaranteeThenSuccess() {
        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        when(userSkillGuaranteeRepository.save(guarantee)).thenReturn(guarantee);

        UserSkillGuarantee userSkillGuarantee = userSkillGuaranteeService
                .saveUserSkillGuarantee(guarantee.getSkill(), guarantee.getUser(), guarantee.getGuarantor());

        assertNotNull(userSkillGuarantee);
        verify(userSkillGuaranteeRepository).save(guarantee);
    }
}