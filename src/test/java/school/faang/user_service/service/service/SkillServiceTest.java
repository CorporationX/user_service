package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

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

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateWithNullTitle() {
        SkillDto skillDto = new SkillDto();

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateExistedSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }
}
