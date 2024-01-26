package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    private SkillDto skillDto;
    private Skill skill;
    private List<User> user;
    private SkillCandidateDto candidateDto;
    private List<SkillCandidateDto> skillCandidateDto;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        user = new ArrayList<>();
        skill = Skill.builder()
                .title("test")
                .users(user)
                .build();
        skillDto = SkillDto.builder()
                .id(0L)
                .title("test")
                .userIds(List.of())
                .build();
    }

    @Test
    void testCreateDataValidationException() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSaveSuccessful() {
        //добавь проверку на то, что возвращаемая SkillEntity валидна
        skillService.create(skillDto);
        verify(skillRepository).save(skill);
    }

    @Test
    void testGetUserSkillsSuccessful() {
        when(skillRepository.findAllByUserId(1L)).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);
        List<SkillDto> skillDtoList = skillService.getUserSkills(1L);
        assertTrue(skillDtoList.contains(skillDto));
    }

    @Test
    void testGetOfferedSkills() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("test1")
                .users(user)
                .build();
        Skill skill2 = Skill.builder()
                .id(1L)
                .title("test1")
                .users(user)
                .build();
        Skill skill3 = Skill.builder()
                .id(1L)
                .title("test1")
                .users(user)
                .build();
        Skill skill4 = Skill.builder()
                .id(2L)
                .title("test2")
                .users(user)
                .build();

        skills = List.of(skill1, skill2, skill3, skill4);

        candidateDto = new SkillCandidateDto(
                skillMapper.toDto(skill1),
                3L);
        List<SkillCandidateDto> listCandidateDto = new ArrayList<>();
        listCandidateDto.add(candidateDto);

        Mockito.when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(skills);
        skillCandidateDto = skillService.getOfferedSkills(1L);
        assertEquals(listCandidateDto, skillCandidateDto);
    }
}