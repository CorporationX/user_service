package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;



@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserService userService;
    SkillDto skillDto;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto(1L, "flexibility");

    }

    @Test
    void testCreateExistingSkillThrowsException() {
        when(skillRepository.existsByTitle(anyString()))
                .thenReturn(true);
        assertTrue(skillRepository.existsByTitle(anyString()));
        assertThrows(DataValidationException.class,
                () -> skillService.create(new SkillDto(1L, "flexibility")));
    }

    @Test
    void testCreateSkill() {
        skillService.create(skillDto);
        verify(skillRepository, times(1)).save(any());
    }

    @Test
    void testCallMethodFindByIdAndFindAllByUserId() {
        skillService.getUserSkills(1L);
        verify(userService, times(1)).checkUserById(1L);
        verify(skillRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void testMapperFromDTOtoEntity(){
        assertInstanceOf(Skill.class, skillMapper.toEntity(skillDto));
        Skill skillResult = skillMapper.toEntity(skillDto);
        assertEquals(skillResult.getTitle(), skillDto.getTitle());
        assertEquals(skillResult.getId(), skillDto.getId());
    }
}