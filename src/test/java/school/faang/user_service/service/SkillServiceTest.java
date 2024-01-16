package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapperImpl skillMapper;
    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    void setUp() {
        skillDto = SkillDto.builder().title("test").build();
        skill = Skill.builder().title("test").build();
    }

    @Test
    void testCreate_DataValidationException() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreate_Save() {
        skillService.create(skillDto);
        verify(skillRepository).save(skill);
    }
}