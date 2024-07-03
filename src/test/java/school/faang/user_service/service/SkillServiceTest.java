package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    SkillService skillService;

    @Mock
    SkillRepository skillRepository;

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void testCreateWhenExistsByTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSaveToDb() {
        SkillDto skillDto = new SkillDto(0L, "Title");
        Skill skill = skillMapper.toEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        skillService.create(skillDto);

        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void testGetUserSkills() {
        long userId = 1;
        Skill skill = new Skill();
        List<SkillDto> skillDtos = List.of(skillMapper.toDto(skill));

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));
        List<SkillDto> returnedSkillDtos = skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
        assertEquals(skillDtos, returnedSkillDtos);
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1;
        Skill skill = new Skill();
        List<SkillCandidateDto> skillCandidateDtos = List.of(skillMapper.toSkillCandidateDto(skill));

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        List<SkillCandidateDto> returnedSkillCandidateDtos = skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        assertEquals(skillCandidateDtos, returnedSkillCandidateDtos);
    }
}