package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private long userId;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .title("title")
                .id(1L)
                .build();
        userId = 1L;
    }

    @Test
    public void testCreate_Success() {
        Skill expectedSkill = Skill.builder()
                .title(skill.getTitle())
                .id(skill.getId())
                .build();
        when(skillRepository.existsByTitle(anyString()))
                .thenReturn(false);
        when(skillRepository.save(skill))
                .thenReturn(skill);

        Skill actualSkill = skillService.create(skill);

        assertEquals(expectedSkill, actualSkill);
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    public void testCreate_AlreadyExist() {
        when(skillRepository.existsByTitle(anyString()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }

    @Test
    void findSkillById_ShouldReturnSkill() {
        Skill skill = new Skill();
        when(skillRepository.findById(anyLong())).thenReturn(Optional.of(skill));

        Optional<Skill> result = skillService.findSkillById(1L);

        assertTrue(result.isPresent());
        assertEquals(skill, result.get());
    }

    @Test
    void findSkillById_ShouldReturnEmpty_WhenSkillNotFound() {
        when(skillRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Skill> result = skillService.findSkillById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void assignSkillsFromGoalToUsers_ShouldInvokeRepositoryAssignSkillToUser() {
        User user = new User();
        user.setId(1L);
        List<User> users = List.of(user);

        doNothing().when(skillRepository).assignSkillToUser(anyLong(), anyLong());

        skillService.assignSkillsFromGoalToUsers(1L, users);

        verify(skillRepository, times(1)).assignSkillToUser(1L, 1L);
    }

    @Test
    public void testGetUserSkills() {
        List<Skill> expectedSkills = List.of(skill);
        when(skillRepository.findAllByUserId(anyLong()))
                .thenReturn(expectedSkills);

        List<Skill> actualSkills = skillService.getUserSkills(userId);

        assertEquals(expectedSkills, actualSkills);
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetOfferedSkills() {
        List<Skill> expectedOfferedSkills = List.of(skill);
        when(skillRepository.findSkillsOfferedToUser(anyLong()))
                .thenReturn(expectedOfferedSkills);

        List<Skill> actualSkills = skillService.getOfferedSkills(userId);

        assertEquals(expectedOfferedSkills, actualSkills);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testAcquireSkillFromOffers_Success() {
        Recommendation recommendation = Recommendation.builder()
                .receiver(User.builder()
                        .id(1L)
                        .build())
                .author(User.builder()
                        .id(2L)
                        .build())
                .build();
        List<SkillOffer> skillOffers = List.of(
                SkillOffer.builder()
                        .skill(skill)
                        .recommendation(recommendation)
                        .build(),
                SkillOffer.builder()
                        .skill(skill)
                        .recommendation(recommendation)
                        .build()
                , SkillOffer.builder()
                        .skill(skill)
                        .recommendation(recommendation)
                        .build()
        );
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong()))
                .thenReturn(skillOffers);


        Skill actualSkill = skillService.acquireSkillFromOffers(userId, skill.getId());

        assertEquals(skill, actualSkill);
        verify(userSkillGuaranteeRepository, times(1)).saveAll(anyList());
        verify(skillRepository, times(1)).assignSkillToUser(skill.getId(), userId);
        verify(skillRepository, times(2)).findUserSkill(skill.getId(), userId);
    }

    @Test
    public void testAcquireSkillFromOffers_UserAlreadyHasSkill() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(skill));

        assertThrows(DataValidationException.class, () ->
                skillService.acquireSkillFromOffers(userId, skill.getId()));
        verify(skillRepository, times(1)).findUserSkill(anyLong(), anyLong());
    }

    @Test
    public void testAcquireSkillFromOffers_NotEnoughSkillOffers() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong()))
                .thenReturn(List.of(new SkillOffer()));

        assertThrows(DataValidationException.class, () ->
                skillService.acquireSkillFromOffers(userId, skill.getId()));
        verify(skillRepository, times(1)).findUserSkill(anyLong(), anyLong());
    }
}