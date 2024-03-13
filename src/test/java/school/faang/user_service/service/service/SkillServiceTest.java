package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validate.SkillValidate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillValidate skillValidate;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private  UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<Skill> captorSkill;
    public final int MIN_SKILL_OFFERS = 3;

    @Test
    public void testCreateSaveSkill() {
        SkillDto skillDto = prepareData(false, "test");

        skillService.create(skillDto);


        verify(skillRepository, times(1)).save(captorSkill.capture());
        Skill skill = captorSkill.getValue();
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }


    @Test
    public void testgetOfferedSkills(){
        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(anyList());
        List <SkillCandidateDto> skillCandidateDto = skillService.getOfferedSkills(1L);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(1L);
        assertNotNull(skillCandidateDto);
    }
    @Test
    public void test() {
        Long skillId = 1L;
        Long userId = 1L;
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());


}

    private SkillDto prepareData(boolean existsByTitle, String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(title);
        return skillDto;
    }
}