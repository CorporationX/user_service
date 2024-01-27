package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Mock
    private SkillRepository skillRepository;

    @Captor
    ArgumentCaptor<Skill> skillCaptor;

    @Test
    public void shouldThrowExceptionForExistingSkill () {
        SkillDto dto = setSkillDto(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(dto)
        );
    }

    @Test
    public void shouldCreateSkill () {
        SkillDto dto = setSkillDto(false);

        SkillDto result = skillService.create(dto);

        verify(skillRepository, times(1))
                .save(skillCaptor.capture());
        Skill skill = skillCaptor.getValue();

        assertNotNull(result);
        assertEquals(dto.getTitle(), skill.getTitle());
        assertEquals(dto.getTitle(), result.getTitle());
    }

    private SkillDto setSkillDto (boolean existsByTitle) {
        SkillDto dto = SkillDto.builder().id(1L).title("Asdasd").build();
        when(skillRepository.existsByTitle(dto.getTitle())).thenReturn(existsByTitle);

        return dto;
    }
}
