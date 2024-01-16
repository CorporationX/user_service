package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Spy
    private SkillMapper skillMapper;
    @Mock
    private SkillRepository skillRepository;
    private Skill skill;
    private Skill skill2;
    private User user;
    private SkillDto skillDto;

    @BeforeEach
    public void init () {
        skill = new Skill();
        skillDto = new SkillDto();

        skill.setId(1L);
        skill.setTitle("Spring");

        skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Java");

        skillDto.setId(1L);
        skillDto.setTitle("Java");
    }

    @Test
    public void testExistsByTitle () {
        Mockito.when(skillRepository.existsByTitle("Java"))
                .thenReturn(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(skillDto)
        );
    }

    @Test
    public void testCreate () {

        SkillDto dto = new SkillDto();
        dto.setTitle("asdasd");
        dto.setId(22L);

        skillService.create(dto);

        Mockito.verify(
                        skillRepository,
                        Mockito.times(1)
                )
                .save(skillMapper.toEntity(dto));
    }

    @Test
    public void testGetUserSkills () {
        user = new User();
        user.setId(1L);
        user.setSkills(List.of(skill, skill2));

       List<Skill> skillEntityList = user.getSkills();
       List<SkillDto> skillDtoList = skillEntityList.stream().map(skillMapper::toDto).toList();

        Mockito.when(skillRepository.findAllByUserId(1L)).thenReturn(skillEntityList);

        List<SkillDto> serviceResult = skillService.getUserSkills(1L);

        assertEquals(skillDtoList, serviceResult);
    }
}
