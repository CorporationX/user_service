package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    private static final long RANDOM_ID_FOR_REQUESTER = 18L;
    private static final long RANDOM_ID_FOR_RECEIVER = 121L;
    private static final int MORE_THEN_HALF_YEAR_IN_DAYS = 190;
    private static final long RANDOM_ID_FOR_SKILL_REQUEST_JAVA = 1L;
    private static final long RANDOM_ID_FOR_SKILL_REQUEST_PYTHON = 2L;
    private static final long RANDOM_ID_FOR_SKILL_REQUEST_KOTLIN = 3L;
    private static final RequestStatus RANDOM_REQUEST_STATUS = RequestStatus.PENDING;
    private static final String RANDOM_MESSAGE = "Any";
    private static final long RANDOM_ID_FOR_RECOMMENDATION = 13L;
    private static final long RANDOM_ID_FOR_FILTER = 134L;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    private User requester;
    private User receiver;
    private RecommendationRequest recommendationRequest;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requester = new User();
        receiver = new User();
        SkillRequest java = new SkillRequest();
        SkillRequest python = new SkillRequest();
        SkillRequest kotlin = new SkillRequest();
        java.setId(RANDOM_ID_FOR_SKILL_REQUEST_JAVA);
        python.setId(RANDOM_ID_FOR_SKILL_REQUEST_PYTHON);
        kotlin.setId(RANDOM_ID_FOR_SKILL_REQUEST_KOTLIN);
        List<SkillRequest> skillRequests = List.of(java, python, kotlin);
        Recommendation recommendation = new Recommendation();
        recommendation.setId(RANDOM_ID_FOR_RECOMMENDATION);
        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setSkills(skillRequests);
        recommendationRequest.setMessage(RANDOM_MESSAGE);
        recommendationRequest.setStatus(RANDOM_REQUEST_STATUS);
        recommendationRequest.setRecommendation(recommendation);
        requester.setId(RANDOM_ID_FOR_REQUESTER);
        receiver.setId(RANDOM_ID_FOR_RECEIVER);
        requestFilterDto = new RequestFilterDto(RANDOM_ID_FOR_FILTER, requester.getId(), receiver.getId(),
                RANDOM_REQUEST_STATUS, RANDOM_MESSAGE, recommendation.getId(), LocalDateTime.now());

    }

    private void mockUserRepoAndSkillRepo(List<Skill> skills) {
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        Mockito.when(skillRepository.findAllById(Mockito.anyCollection())).thenReturn(skills);
    }

    private void assertThrowsCreateMethod() {
        assertThrows(
                IllegalArgumentException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    private void verifyCreateMethod() {
        recommendationRequestService.create(recommendationRequest);
        Mockito.verify(recommendationRequestRepository).create(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyLong());
    }

    @Test
    void testReceiverIsNotInDb() {
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        assertThrowsCreateMethod();
    }

    @Test
    void testRequesterIsNotInDb() {
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

        assertThrowsCreateMethod();
    }

    @Test
    void testNotAllRequestSkillsInDb() {
        List<Skill> skills = List.of(new Skill(), new Skill());
        mockUserRepoAndSkillRepo(skills);

        assertThrowsCreateMethod();
    }

    @Test
    void testAllRequestSkillsInDb() {
        List<Skill> skills = List.of(new Skill(), new Skill(), new Skill());
        mockUserRepoAndSkillRepo(skills);

        verifyCreateMethod();
    }

    @Test
    void testLastRequestLessThanSixMonthsAgo() {
        List<Skill> skills = List.of(new Skill(), new Skill(), new Skill());
        recommendationRequest.setCreatedAt(LocalDateTime.now());

        mockUserRepoAndSkillRepo(skills);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrowsCreateMethod();
    }

    @Test
    void testLastRequestMoreThanSixMonthsAgo() {
        List<Skill> skills = List.of(new Skill(), new Skill(), new Skill());
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(MORE_THEN_HALF_YEAR_IN_DAYS));

        mockUserRepoAndSkillRepo(skills);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.of(recommendationRequest));

        verifyCreateMethod();
    }

    @Test
    void testFirstRequest() {
        List<Skill> skills = List.of(new Skill(), new Skill(), new Skill());
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(MORE_THEN_HALF_YEAR_IN_DAYS));

        mockUserRepoAndSkillRepo(skills);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.empty());

        verifyCreateMethod();
    }

    @Test
    void testAddSkillInSkillRequestDb() {
        List<Skill> skills = List.of(new Skill(), new Skill(), new Skill());
        recommendationRequest.setCreatedAt(LocalDateTime.now());

        mockUserRepoAndSkillRepo(skills);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.empty());

        recommendationRequestService.create(recommendationRequest);
        Mockito.verify(
                skillRequestRepository,
                Mockito.times(recommendationRequest.getSkills().size())).create(Mockito.anyLong(), Mockito.anyLong()
        );
    }

    @Test
    void testFindAllRequestsFromDb() {
        recommendationRequestService.getRequests(requestFilterDto);
        Mockito.verify(recommendationRequestRepository).findAll();
    }

    @Test
    void testRequestIdInDb() {
        Mockito.when(recommendationRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(recommendationRequest));
        recommendationRequestService.getRequest(Mockito.anyLong());
    }

    @Test
    void testRequestIdNotInDb() {
        Mockito.when(recommendationRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NullPointerException.class,
                () -> recommendationRequestService.getRequest(Mockito.anyLong()));
    }

    @Test
    void testRequestStatusRejected() {
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        Mockito.when(recommendationRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(
                IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(Mockito.anyLong(), new RejectionDto()));
    }

    @Test
    void testRequestStatusAccepted() {
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        Mockito.when(recommendationRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(
                IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(Mockito.anyLong(), new RejectionDto()));
    }
}