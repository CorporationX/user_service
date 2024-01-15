package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;
    @InjectMocks
    private SkillService skillService;
    private Skill skill;

    private SkillDto skillDto;

    @BeforeEach
    public void init () {
        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Spring");

        skillDto = SkillDto.builder().id(1L).title("Java").build();
    }

    @Test
    public void testSkillExistsByTitle () {
        Mockito.when(skillRepository.existsByTitle("Java"))
                .thenReturn(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(skillDto)
        );
    }

    @Test
    public void testCreateSkill () {
        skillService.create(skillDto);

        Mockito.verify(
                        skillRepository,
                        Mockito.times(1)
                )
                .save(skillMapper.toEntity(skillDto));
    }
}
