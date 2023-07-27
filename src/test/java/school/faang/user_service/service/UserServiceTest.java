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
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    List<Skill> skillList;

    @Test
    void getOfferedSkillsTest() {
        long userId = 1L;
        List<Skill> skills = List.of(
                createSkill(1L, "test"),
                createSkill(2L, "test"),
                createSkill(3L, "test")
        );

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillCandidateDto> candidates = skillService.getOfferedSkills(userId);

        assertNotNull(candidates);
        assertEquals(3, candidates.size());
    }

    @Test
    void testAcquireSkillFromOffers() {
        Long skillId = 1L;
        Long userId = 2L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        List<SkillOffer> skillOffers = List.of(
                mock(SkillOffer.class),
                mock(SkillOffer.class),
                mock(SkillOffer.class)
        );

        skillRepository.assignSkillToUser(skillId, userId);

        Skill assignedSkill = createSkill(skillId, "test");
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(assignedSkill));

        SkillDto acquiredSkill = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(acquiredSkill);
        assertEquals(skillId, acquiredSkill.getId());
    }

    @Test
    void createReturnsSkillDto() {
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = skillMapper.skillToEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("title", result.getTitle());

        verify(skillRepository).existsByTitle(skillDto.getTitle());
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void getAllSkillsByIdTest() {
        Long userId = 1L;
        skillList = new ArrayList<>();

        skillList.add(createSkill(1L, "title"));
        skillList.add(createSkill(2L, "title"));

        List<SkillDto> expectedSkillDtoList = List.of(new SkillDto(1L, "title"),
                new SkillDto(1L, "title"));

        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);

        List<SkillDto> result = skillService.getUserSkills(userId, 1, 5);

        assertEquals(expectedSkillDtoList.size(), result.size());
        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void createReturnsValidationException() {
        SkillDto skillDto = new SkillDto(1L, "title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            skillService.create(skillDto);
        });

        verify(skillRepository).existsByTitle(skillDto.getTitle());
    }

    private Skill createSkill(Long id, String title) {
        Skill skill = Skill.builder()
                .id(id)
                .title(title)
                .users(List.of())
                .guarantees(List.of())
                .events(List.of())
                .goals(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return skill;
    }
}