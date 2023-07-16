package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void testCreateSkillToDb() {
        SkillDto skillDto = new SkillDto(1L, "Skill", List.of(1L), null, null);
        Skill skill = skillMapper.toEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        SkillDto createdSkill = skillService.create(skillDto);

        assertNotNull(createdSkill);
        assertEquals(skillDto.getTitle(), createdSkill.getTitle());
    }

    @Test
    void testValidationSkillWithBlankTitle() {
        SkillDto skillDto = new SkillDto(1L, "", List.of(1L), null, null);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));

        assertEquals("Enter skill title, please", validationException.getMessage());
    }

    @Test
    void testValidationSkillNotFoundTitle() {
        SkillDto skillDto = new SkillDto(1L, "Skill", List.of(1L), null, null);

        when(skillRepository.existsByTitle(anyString())).thenReturn(true);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));

        assertEquals("Skill with title " + skillDto.getTitle() + " already exists",
                validationException.getMessage());
    }
}