package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.SkillAlreadyExistsException;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.exception.skill_offer.NotEnoughSkillOffersException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user_skill_guarantee.UserSkillGuaranteeService;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private static final int MIN_SKILL_OFFERS = 3;
    private static final long SKILL_ID = 5L;
    private static final String SKILL_TITLE = "Java";
    private static final long USER_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private UserService userService;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Mock
    private UserContext userContext;

    @Mock
    private SkillValidator skillValidator;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private User user;
    private List<Long> skillIds;
    private List<Long> userIds;
    private List<SkillOffer> skillOffers;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle(SKILL_TITLE);

        skillIds = List.of(1L, 2L);
        userIds = List.of(10L, 20L);
        user = new User();
        skillOffers = new ArrayList<>();
        skillOffers.add(new SkillOffer(1L, skill, new Recommendation()));
        skillOffers.add(new SkillOffer(2L, skill, new Recommendation()));
    }

    @Test
    public void testGetSkillByIdOrThrow_successfully() {

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        Skill returnskill = skillService.getSkillByIdOrThrow(skill.getId());

        verify(skillRepository, times(1)).findById(skill.getId());
        assertEquals(skill.getId(), returnskill.getId());
    }

    @Test
    public void testGetSkillByIdOrThrow_skillNotFound() {
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.getSkillByIdOrThrow(skill.getId()));
        verify(skillRepository, times(1)).findById(skill.getId());
    }

    @Test
    void testAssignSkillsToUsers_noExistingLink() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, times(skillIds.size() * userIds.size()))
                .assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testAssignSkillsToUsers_allLinksExists() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(mock(Skill.class)));

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void testAssignSkillsToUsers_someLinksExists() {
        when(skillRepository.findUserSkill(skillIds.get(0), userIds.get(0)))
                .thenReturn(Optional.of(mock(Skill.class)));
        when(skillRepository.findUserSkill(skillIds.get(1), userIds.get(0)))
                .thenReturn(Optional.empty());
        when(skillRepository.findUserSkill(skillIds.get(0), userIds.get(1)))
                .thenReturn(Optional.empty());
        when(skillRepository.findUserSkill(skillIds.get(1), userIds.get(1)))
                .thenReturn(Optional.empty());

        skillService.assignSkillsToUsers(skillIds, userIds);

        verify(skillRepository, never()).assignSkillToUser(skillIds.get(0), userIds.get(0));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(1), userIds.get(0));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(0), userIds.get(1));
        verify(skillRepository, times(1)).assignSkillToUser(skillIds.get(1), userIds.get(1));
    }

    @Test
    void testCreateExistingSkill() {
        doThrow(new SkillAlreadyExistsException(String.format("Skill with title = %s already exists", SKILL_ID)))
                .when(skillValidator).checkSkillTitleIsUnique(any());

        assertThrows(SkillAlreadyExistsException.class, () -> skillService.create(new Skill()));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void testCreate() {
        when(skillRepository.save(any())).thenReturn(skill);

        Skill savedSkill = skillService.create(skill);

        assertEquals(skill, savedSkill);
        verify(skillValidator).checkSkillTitleIsUnique(any());
    }

    @Test
    void testGetUserSkills() {
        List<Skill> skills = List.of(skill);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);

        List<Skill> result = skillService.getUserSkills();

        assertEquals(skills, result);
    }

    @Test
    void testGetOfferedSkills() {
        List<Skill> skills = List.of(skill);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);

        List<Skill> result = skillService.getOfferedSkills();

        assertEquals(skills, result);
    }

    @Test
    void testAcquireSkillFromOffers_WhenSkillNotFound() {
        when(skillRepository.findById(SKILL_ID)).thenThrow(
                new SkillNotFoundException(String.format("Skill with id = %d not found", SKILL_ID)));

        assertThrows(SkillNotFoundException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID));
    }

    @Test
    void testAcquireSkillFromOffers_WhenUserAlreadyHasSkill() {
        when(skillRepository.findById(any())).thenReturn(Optional.ofNullable(skill));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.ofNullable(skill));

        Skill acquiredSkill = skillService.acquireSkillFromOffers(SKILL_ID);

        assertEquals(skill, acquiredSkill);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeService, never()).saveAll(any());
    }

    @Test
    void testAcquireSkillFromOffers_WhenUserHasNotEnoughSkillOffers() {
        when(skillRepository.findById(any())).thenReturn(Optional.ofNullable(skill));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferService.findAllOffersOfSkill(anyLong())).thenReturn(skillOffers);
        doThrow(new NotEnoughSkillOffersException("Not enough offers to acquire this skill")).when(skillValidator)
                .checkEnoughOffersToAcquireSkill(skillOffers);

        assertThrows(NotEnoughSkillOffersException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID));
        verify(skillRepository, never()).assignSkillToUser(SKILL_ID, USER_ID);
    }

    @Test
    void testAcquireSkillFromOffers_WhenUserNotFound() {
        when(skillRepository.findById(any())).thenReturn(Optional.ofNullable(skill));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferService.findAllOffersOfSkill(anyLong())).thenReturn(skillOffers);
        when(userService.getUserByIdOrThrow(anyLong())).thenThrow(
                new UserNotFoundException(String.format("User with id = %d not found", USER_ID)));

        assertThrows(UserNotFoundException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID));
        verify(userSkillGuaranteeService, never()).saveAll(any());
    }

    @Test
    void testAcquireSkillFromOffers() {
        skillOffers.add(new SkillOffer(3L, skill, new Recommendation()));

        when(skillRepository.findById(any())).thenReturn(Optional.ofNullable(skill));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferService.findAllOffersOfSkill(anyLong())).thenReturn(skillOffers);
        when(userService.getUserByIdOrThrow(anyLong())).thenReturn(user);

        Skill acquiredSkill = skillService.acquireSkillFromOffers(SKILL_ID);

        assertEquals(skill, acquiredSkill);
        verify(skillValidator).checkEnoughOffersToAcquireSkill(skillOffers);
        verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeService).saveAll(argThat(guarantees ->
                guarantees.size() == MIN_SKILL_OFFERS
                        && guarantees.stream().allMatch(g -> g.getUser().equals(user) && g.getSkill().equals(skill))
        ));
    }
}
