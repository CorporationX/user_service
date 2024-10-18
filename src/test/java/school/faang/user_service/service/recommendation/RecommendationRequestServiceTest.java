package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    private static final long RECOMMENDATION_REQUEST_DTO_ID_ONE = 1L;
    private static final long RECOMMENDATION_REQUEST_ID_ONE = 1L;
    private static final long SKILL_REQUEST_ID_ONE = 1L;
    private static final long SKILL_REQUEST_ID_TWO = 2L;
    private static final long SKILL_REQUEST_ID_THREE = 3L;
    private static final long SKILL_REQUEST_ID_FOUR = 4L;
    private static final long SKILL_ID_ONE = 1L;
    private static final long SKILL_ID_TWO = 2L;
    private static final long REQUESTER_ID_ONE = 1L;
    private static final long RECEIVER_ID_TWO = 2L;
    private static final String TOO_SERIOUS = "Too serious!";
    private static final RequestStatus REQUEST_STATUS_ACCEPTED = RequestStatus.ACCEPTED;
    private static final RequestStatus REQUEST_STATUS_REJECTED = RequestStatus.REJECTED;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private RequestFilter requestFilter;
    @Mock
    private List<RequestFilter> requestFilters;
    private RecommendationRequestDto rqd;
    private RecommendationRequest rq;
    private RecommendationRejectionDto rejection;
    private RecommendationRequestFilterDto filters;
    private List<RecommendationRequest> requests;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        rqd = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_DTO_ID_ONE)
                .requesterId(REQUESTER_ID_ONE)
                .receiverId(RECEIVER_ID_TWO)
                .skillIds(List.of(SKILL_ID_ONE, SKILL_ID_TWO))
                .build();
        rq = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID_ONE)
                .requester(User.builder().id(REQUESTER_ID_ONE).build())
                .receiver(User.builder().id(RECEIVER_ID_TWO).build())
                .skills(List.of(SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_ONE)
                                .build(),
                        SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_TWO)
                                .build()))
                .build();
        rejection = RecommendationRejectionDto.builder()
                .reason(TOO_SERIOUS)
                .build();

        filters = RecommendationRequestFilterDto.builder()
                .status(REQUEST_STATUS_ACCEPTED)
                .build();
        requests = List.of(RecommendationRequest.builder()
                        .status(REQUEST_STATUS_REJECTED)
                        .skills(List.of(SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_ONE)
                                        .build(),
                                SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_TWO)
                                        .build()))
                        .build(),
                RecommendationRequest.builder()
                        .status(REQUEST_STATUS_ACCEPTED)
                        .skills(List.of(SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_THREE)
                                        .build(),
                                SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_FOUR)
                                        .build()
                        ))
                        .build());

        skills = List.of(Skill.builder().build(), Skill.builder().build());
    }

    private void customRecommendationRequestService() {
        requestFilters = List.of(requestFilter);
        recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                recommendationRequestMapper, userService, skillService, recommendationRequestValidator,
                skillValidator, requestFilters);
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Validate requester and receiver and skills exists in DB " +
                "set requester and receiver and make skill requests list set to Recommendation request" +
                "then save it return dto back")
        public void whenValidDtoPassedItSavedWithItsSkillRequestsThenReturnDto() {
            when(userService.getUserById(rqd.getRequesterId())).thenReturn(User.builder().id(REQUESTER_ID_ONE).build());
            when(userService.getUserById(rqd.getRequesterId())).thenReturn(User.builder().id(RECEIVER_ID_TWO).build());
            when(skillService.getAllSkills(rqd.getSkillIds())).thenReturn(skills);
            when(recommendationRequestMapper.toEntity(rqd)).thenReturn(rq);

            recommendationRequestService.create(rqd);

            verify(skillService).getAllSkills(rqd.getSkillIds());
            verify(skillValidator).validateSkillsExist(rqd.getSkillIds(), skills);
        }

        @Test
        @DisplayName("when recommendation request exist in DB don't throw exception")
        public void whenValidIdPassedThenReturnFoundDto() {
            when(recommendationRequestRepository.findById(rqd.getId())).thenReturn(Optional.of(rq));
            recommendationRequestService.getRequest(rqd.getId());
            verify(recommendationRequestRepository).findById(rqd.getId());
            assertDoesNotThrow(() -> recommendationRequestService.getRequest(rqd.getId()));
        }

        @Test
        @DisplayName("When rq status is pending and valid rejection dto passed then set rejection reason and save")
        public void whenValidRejectionRequestPassedRejectionReasonChangedAndSavedThenReturnDto() {
            when(recommendationRequestRepository.findById(rqd.getId())).thenReturn(Optional.of(rq));
            recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID_ONE, rejection);
            verify(recommendationRequestValidator)
                    .validateRequestStatus(rq);
            verify(recommendationRequestRepository)
                    .save(rq);
            assertEquals(rq.getRejectionReason(), rejection.getReason());
        }

        @Test
        @DisplayName("Check if filters is applied and apply them to stream then return List of filtered rqDtos")
        public void whenValidFilterPassedThenReturnFilteredDtoList() {
            customRecommendationRequestService();
            when(recommendationRequestRepository.findAll())
                    .thenReturn(requests);
            when(requestFilter.isApplicable(filters))
                    .thenReturn(true);
            when(requestFilter.applyFilter(any(), eq(filters)))
                    .thenReturn(requests.stream().filter(filter -> filter.getStatus() == REQUEST_STATUS_ACCEPTED));
            List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filters);
            assertEquals(1, result.size());
            verify(recommendationRequestRepository)
                    .findAll();
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("When recommendation request does not exist in DB then throw exception")
        public void whenRecommendationRequestDoesNotExistThenThrowException() {
            when(recommendationRequestRepository.findById(rqd.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(DataValidationException.class, () ->
                    recommendationRequestService.getRequest(rqd.getId()));
            assertThrows(DataValidationException.class, () ->
                    recommendationRequestService.rejectRequest(rqd.getId(), rejection));
        }
    }
}
