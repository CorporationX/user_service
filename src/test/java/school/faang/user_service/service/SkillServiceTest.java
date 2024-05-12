package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationExeption;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    private static final long SKILL_NEW_ID = 0L;
    private static final long SKILL_CREATED_ID = 1L;
    private static final String SKILL_TITLE = "Java";
    private static final long USER_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillCandidateMapper skillCandidateMapper;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    SkillDto skillDto;
    SkillDto savedSkillDto;
    Skill savedSkill;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto(SKILL_NEW_ID, SKILL_TITLE, null);
        savedSkillDto = new SkillDto(SKILL_CREATED_ID, SKILL_TITLE, null);
        savedSkill = Skill.builder()
                .id(SKILL_CREATED_ID)
                .title(SKILL_TITLE)
                .build();
    }

    @Test
    public void createSuccess() {
        Skill skill = Skill.builder()
                .id(SKILL_NEW_ID)
                .title(SKILL_TITLE)
                .build();

        when(skillRepository.save(skill)).thenReturn(savedSkill);
        when(skillMapper.dtoToSkill(skillDto)).thenReturn(skill);
        when(skillMapper.skillToDto(savedSkill)).thenReturn(savedSkillDto);

        SkillDto actualResult = skillService.create(skillDto);
        assertEquals(savedSkillDto, actualResult);
    }

    @Test
    public void getAllSkillsByUserIdWhenNoSkills() {
        List<SkillDto> actualResult = skillService.getUserSkills(USER_ID);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void getOfferedSkills() {
        List<Skill> skills = List.of(savedSkill);
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(savedSkillDto, 1);

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);
        when(skillCandidateMapper.skillToCandidateDto(savedSkill, 1)).thenReturn(skillCandidateDto);

        List<SkillCandidateDto> actualResult = skillService.getOfferedSkills(USER_ID);

        assertEquals(1, actualResult.size());
        assertEquals(skillCandidateDto, actualResult.get(0));
    }

    @Test
    public void getAllOfferedSkillsByUserIdWhenNoSkills() {
        List<SkillCandidateDto> actualResult = skillService.getOfferedSkills(USER_ID);
        assertTrue(actualResult.isEmpty());
    }
}