package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @Mock
    private SkillService skillService;
    @InjectMocks
    private SkillController controller;

    @Test
    public void testCreate() {
        SkillDto moreDto = createSkillDto("More Dto");
        when(skillService.create(any())).thenReturn(moreDto);

        assertEquals(moreDto, controller.create(moreDto));
        verify(skillService, times(1)).create(any());
    }

    @Test
    public void testGetUserSkills() {
        Long randomId = 1L;
        SkillDto moreDto = createSkillDto("One more Dto");
        when(skillService.getUserSkills(randomId)).thenReturn(List.of(moreDto));

        assertEquals(List.of(moreDto), controller.getUserSkills(randomId));
    }

    @Test
    public void testgetOfferedSkills() {
        Long randomId = 1L;
        SkillCandidateDto candidate = new SkillCandidateDto(createSkillDto("Skill"), 1L);
        when(skillService.getOfferedSkills(randomId)).thenReturn(List.of(candidate));

        assertEquals(List.of(candidate), controller.getOfferedSkills(randomId));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        Long skillId = 1L;
        Long userId = 2L;
        SkillDto dto = createSkillDto("Title");
        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(dto);

        assertEquals(dto, controller.acquireSkillFromOffers(skillId, userId));
    }


    private SkillDto createSkillDto(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(title);
        skillDto.setId(11323L);
        return skillDto;
    }
}
