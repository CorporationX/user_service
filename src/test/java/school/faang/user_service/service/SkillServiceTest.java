package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Spy
    private SkillMapperImpl skillMapper;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillCandidateMapper skillCandidateMapper;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<Skill> captor;

    @InjectMocks
    private SkillService skillService;

    public static SkillDto anySkillDto(String title) {
        SkillDto skill = new SkillDto();
        skill.setTitle(title);
        return skill;
    }

    @Test
    public void testCreateExistsByTitle() {
        SkillDto skill = anySkillDto("title");
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }

    @Test
    public void testCreateNotExisting() {
        SkillDto skill = anySkillDto("title");
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(false);

        skillService.create(skill);
        verify(skillRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        int expectedSize = 2;
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(new Skill(), new Skill()));
        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(SkillDto.class, result.get(0).getClass());
        assertEquals(expectedSize, result.size());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        skillService.getOfferedSkills(userId);
        List<Skill> candidateSkills = skillRepository.findSkillsOfferedToUser(userId);

        verify(skillCandidateMapper, times(1)).toCandidateDto(candidateSkills);
    }

    @Test
    public void testAcquireSkillFromOffersEmpty() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        Optional<SkillDto> result = skillService.acquireSkillFromOffers(anyLong(), anyLong());

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testAcquireSkillFromOffersLessThree() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Skill()));
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong()))
                .thenReturn(List.of(new SkillOffer()));
        Optional<SkillDto> result = skillService.acquireSkillFromOffers(anyLong(), anyLong());

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testAcquireSkillFromOffersOverThree() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(Skill.builder().guarantees(new ArrayList<>()).build()));
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong()))
                .thenReturn(List.of(new SkillOffer(), new SkillOffer(), new SkillOffer()
                , new SkillOffer()));
        skillService.acquireSkillFromOffers(anyLong(), anyLong());
        verify(skillRepository).assignSkillToUser(anyLong(), anyLong());
    }
}
