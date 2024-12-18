package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.TestObjectGenerator;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.SkillAcquireEvent;
import school.faang.user_service.publisher.SkillAcquireEventPublisher;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSkillGuaranteeServiceTest {

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private UserService userService;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Mock
    private SkillAcquireEventPublisher eventPublisher;

    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;

    private final TestObjectGenerator testObjectGenerator = new TestObjectGenerator();
    private User user;
    private Skill skill;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        user = testObjectGenerator.createUserTest();
        skill = testObjectGenerator.createSkillTest();
        recommendation = testObjectGenerator.createRecommendationTest();
    }

    @Test
    void testAddSkillGuarantee() {
        when(userService.findUserById(recommendation.getReceiver().getId())).thenReturn(user);

        userSkillGuaranteeService.addSkillGuarantee(skill, recommendation);

        verify(recommendationValidator, times(1)).validateRecommendationExistsById(recommendation.getId());
        verify(skillValidator, times(1)).validateSkillExists(skill.getId());
        verify(userSkillGuaranteeRepository, times(1)).save(any(UserSkillGuarantee.class));
    }

    @Test
    void testPublishEventSuccess() {
        long userId = 1L;
        long skillId = 3L;

        User user = new User();
        user.setId(userId);

        User guarantor = new User();
        guarantor.setId(2L);

        Skill skill = new Skill();
        skill.setId(skillId);

        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(user)
                .skill(skill)
                .guarantor(guarantor)
                .build();

        when(userSkillGuaranteeRepository.findByUserIdAndSkillId(userId, skillId))
                .thenReturn(List.of(userSkillGuarantee));

        userSkillGuaranteeService.publishSkillAcquiredEvent(userId, skillId);

        ArgumentCaptor<SkillAcquireEvent> eventCaptor = ArgumentCaptor.forClass(SkillAcquireEvent.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());

        SkillAcquireEvent capturedEvent = eventCaptor.getValue();

        assertEquals(userId, capturedEvent.getAuthorId());
        assertEquals(2L, capturedEvent.getReceiverId());
        assertEquals(skillId, capturedEvent.getSkillId());
    }


    @Test
    void testPublishEventFailureWhenNoMatchingGuarantor() {
        long userId = 1L;
        long skillId = 3L;

        when(userSkillGuaranteeRepository.findByUserIdAndSkillId(userId, skillId))
                .thenReturn(List.of());

        userSkillGuaranteeService.publishSkillAcquiredEvent(userId, skillId);

        verify(eventPublisher, never()).publish(any(SkillAcquireEvent.class));
    }

    @Test
    void testPublishEventFailureWhenRepositoryReturnsEmptyList() {
        long userId = 1L;
        long skillId = 3L;

        when(userSkillGuaranteeRepository.findByUserIdAndSkillId(userId, skillId))
                .thenReturn(Collections.emptyList());

        userSkillGuaranteeService.publishSkillAcquiredEvent(userId, skillId);

        verify(eventPublisher, never()).publish(any(SkillAcquireEvent.class));
    }
}
