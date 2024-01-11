package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private final SkillDto inputSkillDto = new SkillDto("Навык");

    @Test
    public void create_whenSkillFromSkillDtoIsNew_thenSaveToDbAndReturnSkillDto() {
        // Arrange

        Skill skill = new Skill();
        skill.setTitle(inputSkillDto.getTitle());

        Mockito.when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(false);
        Mockito.when(skillMapper.toEntity(inputSkillDto)).thenReturn(skill);
        Mockito.when(skillRepository.save(skill)).thenReturn(skill);
        Mockito.when(skillMapper.toDto(skill)).thenReturn(new SkillDto("Навык"));

        // Act
        SkillDto result = skillService.create(inputSkillDto);

        // Assert
        Mockito.verify(skillRepository, Mockito.times(1)).existsByTitle(inputSkillDto.getTitle());
        Mockito.verify(skillMapper, Mockito.times(1)).toEntity(inputSkillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
        Mockito.verify(skillMapper, Mockito.times(1)).toEntity(inputSkillDto);

    }

    @Test
    public void create_whenTitleOfSkillDtoIsExist_thenThrowRuntimeException() {
        // Arrange
        Mockito.when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(true);

        // Act & Assert
        Assert.assertThrows(RuntimeException.class, () -> skillService.create(inputSkillDto));
    }
}