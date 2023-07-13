package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    public SkillController controller;
    @Mock
    private SkillService skillService;

    @Test
    void testCreate() {
        SkillDto skill = new SkillDto(1L, "Programming");
        controller.create(skill);
        Mockito.verify(skillService, Mockito.times(1)).create(skill);
    private SkillController controller;
    @Mock
    private SkillService service;

    @Test
    void getUserSkills() {
        long userId = 1;
        controller.getUserSkills(userId);
        Mockito.verify(service).getUserSkills(userId);
    }
}