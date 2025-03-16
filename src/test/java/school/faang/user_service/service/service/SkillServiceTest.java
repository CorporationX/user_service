package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testSuccessfulSkillCreate() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        Skill skillEntity = new Skill();
        skillEntity.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillRepository.save(any())).thenReturn(skillEntity);
        when(skillMapper.toEntity(any())).thenReturn(skillEntity);
        when(skillMapper.toDto(any())).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertEquals("Java", result.getTitle());
    }

    @Test
    public void testExceptionIfSkillIsExist() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
        assertEquals("That skill is already there.", exception.getMessage());
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
        assertEquals("Skill title is empty", exception.getMessage());
    }

    @Test
    public void testCreateWithNullTitle() {
        SkillDto skillDto = new SkillDto();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
        assertEquals("Skill title is empty", exception.getMessage());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Bootcamp");

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill2));

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);

        assertNotNull(offeredSkills);
        assertEquals(2, offeredSkills.size());

        assertTrue(offeredSkills.stream().anyMatch(skill -> "Java".equals(skill.getSkill().getTitle())));
        assertTrue(offeredSkills.stream().anyMatch(skill -> "Bootcamp".equals(skill.getSkill().getTitle())));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long userId = 1L;
        long skillId = 1L;

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        assertEquals("Not enough offers to acquire this skill.", exception.getMessage());
    }
}
