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
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;


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
    Skill skill1;
    Skill skill2;
    List<Skill> list1;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto(1L, "flexibility");
        skill1 = skillMapper.toEntity(skillDto);
        //skill2 = new Skill(1L, "test1", null, null, null, null, null, null);
        list1 = new ArrayList<>(List.of(skill1));


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
    void testMapperFromDTOtoEntity() {
        assertInstanceOf(Skill.class, skillMapper.toEntity(skillDto));
        Skill skillResult = skillMapper.toEntity(skillDto);
        assertEquals(skillResult.getTitle(), skillDto.getTitle());
        assertEquals(skillResult.getId(), skillDto.getId());
    }

    @Test
    void testCallMethodFindSkillsOfferedToUser() {
        skillService.getOfferedSkills(1L);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(1L);
    }

    @Test
    void testMapperFromEmptySkillList() {
        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(new ArrayList<Skill>());
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);
        assertEquals(0, result.size());
    }

    @Test
    void testMapperFromOneElementSkillList() {
        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(list1);
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);

        //assertEquals(1, result.size());
    }

//    @Test
//    void testMapperFromManyElementsSkillList() {
//        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(new ArrayList<>(List.of(skillMapper.toEntity(skillDto))));
//        skillService.getOfferedSkills(1L);
//    }

}