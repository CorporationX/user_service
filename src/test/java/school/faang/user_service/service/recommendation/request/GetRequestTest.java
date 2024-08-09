package school.faang.user_service.service.recommendation.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GetRequestTest {

    @MockBean
    private static RecommendationRequestRepository recommendationRequestRepository;

    @Autowired
    private RecommendationRequestService recommendationRequestService;

    private record GetRequestTestParam(
            RecommendationRequestFilter filter,
            Set<RecommendationRequestDto> expectedRecommendations
    ) {
    }

    private static List<Arguments> testData;
    private static List<RecommendationRequest> allRecommendationRequests;

    @BeforeAll
    public static void fillRecommendationRequestRepository() {

        final long ACCEPTED_REQUEST_ID = 1;
        final long REJECTED_REQUEST_ID = 2;
        final long PENDING_REQUEST_ID = 3;

        final LocalDateTime CREATE_AT = LocalDateTime.now();
        final LocalDateTime UPDATE_AT = CREATE_AT.plusDays(1);

        var acceptedRecommendationRequest1 = RecommendationRequest.builder()
                .id(ACCEPTED_REQUEST_ID)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(2).build())
                .skills(List.of(
                        SkillRequest.builder().id(1L).build(),
                        SkillRequest.builder().id(2L).build()
                ))
                .message("other_message")
                .status(RequestStatus.ACCEPTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var acceptedRecommendationRequest2 = RecommendationRequest.builder()
                .id(4L)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(2).build())
                .skills(List.of(
                        SkillRequest.builder().id(3L).build(),
                        SkillRequest.builder().id(2L).build()
                ))
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var rejectedRecommendationRequest1 = RecommendationRequest.builder()
                .id(REJECTED_REQUEST_ID)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(4).build())
                .skills(List.of(
                        SkillRequest.builder().id(3L).build(),
                        SkillRequest.builder().id(2L).build()
                ))
                .message("Test message")
                .status(RequestStatus.REJECTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var rejectedRecommendationRequest2 = RecommendationRequest.builder()
                .id(5L)
                .requester(User.builder().id(5).build())
                .receiver(User.builder().id(3).build())
                .skills(List.of(
                        SkillRequest.builder().id(6L).build()
                ))
                .message("Catboost top")
                .status(RequestStatus.REJECTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var pendingRecommendationRequest = RecommendationRequest.builder()
                .id(PENDING_REQUEST_ID)
                .requester(User.builder().id(3).build())
                .receiver(User.builder().id(2).build())
                .skills(List.of(SkillRequest.builder().id(1L).build()))
                .message("Text")
                .status(RequestStatus.PENDING)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        allRecommendationRequests = List.of(
                acceptedRecommendationRequest1,
                acceptedRecommendationRequest2,
                rejectedRecommendationRequest1,
                rejectedRecommendationRequest2,
                pendingRecommendationRequest
        );

        // ------------------------- filling params for tests --------------------------------------

        RecommendationRequestMapperImpl mapper = new RecommendationRequestMapperImpl();
        testData = new LinkedList<>();

        // Test 1

        var messageFilterByWholeWord = RecommendationRequestFilter.builder().
                message("Text")
                .build();


        var expectedListOfRequestFilteredByWholeWord = Set.of(
                mapper.toDto(pendingRecommendationRequest)
        );

        testData.add(Arguments.of(
                "Test to find recommendations by whole word",
                new GetRequestTestParam(
                        messageFilterByWholeWord,
                        expectedListOfRequestFilteredByWholeWord
                )
        ));

        // Test 2

        var substringFilter = RecommendationRequestFilter.builder()
                .message("boost")
                .build();

        var expectedListOfRequestFilteredBySubstring = Set.of(
                mapper.toDto(rejectedRecommendationRequest2)
        );

        testData.add(Arguments.of(
                "Test to find recommendation by substring",
                new GetRequestTestParam(
                        substringFilter,
                        expectedListOfRequestFilteredBySubstring
                )
        ));

        // Test 3

        // combine 2 cases above
        var combinedMessageFilter = RecommendationRequestFilter.builder()
                .message("message")
                .build();

        var expectedListOfRequestFilteredByMessage = Set.of(
                mapper.toDto(acceptedRecommendationRequest1),
                mapper.toDto(acceptedRecommendationRequest2),
                mapper.toDto(rejectedRecommendationRequest1)
        );

        testData.add(Arguments.of(
                "Test for finding several queries containing a common substring",
                new GetRequestTestParam(
                        combinedMessageFilter,
                        expectedListOfRequestFilteredByMessage
                )
        ));

        // Test 4

        var requesterFilter = RecommendationRequestFilter.builder()
                .requesterId(1L)
                .build();

        var expectedListOfRequestFilteredByRequester = Set.of(
                mapper.toDto(acceptedRecommendationRequest1),
                mapper.toDto(acceptedRecommendationRequest2),
                mapper.toDto(rejectedRecommendationRequest1)
        );

        testData.add(Arguments.of(
                "Test for search only according to the requester",
                new GetRequestTestParam(
                        requesterFilter,
                        expectedListOfRequestFilteredByRequester
                )
        ));

        // Test 5

        var receiverFilter = RecommendationRequestFilter.builder()
                .receiverId(2L)
                .build();

        var expectedListOfRequestFilteredByReceiver = Set.of(
                mapper.toDto(acceptedRecommendationRequest1),
                mapper.toDto(acceptedRecommendationRequest2),
                mapper.toDto(pendingRecommendationRequest)
        );

        testData.add(Arguments.of(
                "Test for search only according to the receiver",
                new GetRequestTestParam(
                        receiverFilter,
                        expectedListOfRequestFilteredByReceiver
                )
        ));

        // Test 6

        // only one request with skillId=6
        var skillFilter = RecommendationRequestFilter.builder()
                .skillIds(Set.of(6L))
                .build();

        var expectedListOfRequestFilteredBySkill = Set.of(
                mapper.toDto(rejectedRecommendationRequest2)
        );

        testData.add(Arguments.of(
                "Test for search using only a unique id skill",
                new GetRequestTestParam(
                        skillFilter,
                        expectedListOfRequestFilteredBySkill
                )
        ));

        // Test 7

        var skillsFilter = RecommendationRequestFilter.builder()
                .skillIds(Set.of(2L, 3L))
                .build();

        var expectedListOfRequestFilteredBySkills = Set.of(
                mapper.toDto(acceptedRecommendationRequest2),
                mapper.toDto(rejectedRecommendationRequest1)
        );

        testData.add(Arguments.of(
                "Test for search request by multiple skills",
                new GetRequestTestParam(
                        skillsFilter,
                        expectedListOfRequestFilteredBySkills
                )
        ));

        // Test 8

        var skillFilterWithoutMatch = RecommendationRequestFilter.builder()
                .skillIds(Set.of(1L, 6L))
                .build();

        Set<RecommendationRequestDto> expectedListOfRequestWithoutMatching = Set.of();

        testData.add(Arguments.of(
                "Test for search by not existing set of skills",
                new GetRequestTestParam(
                        skillFilterWithoutMatch,
                        expectedListOfRequestWithoutMatching
                )
        ));

        // Test 9

        var acceptedFilter = RecommendationRequestFilter.builder()
                .status(RequestStatus.ACCEPTED)
                .build();

        var expectedListOfRequestFilteredByAccepted = Set.of(
                mapper.toDto(acceptedRecommendationRequest1),
                mapper.toDto(acceptedRecommendationRequest2)
        );

        testData.add(Arguments.of(
                "Test for search only accepted requests",
                new GetRequestTestParam(
                        acceptedFilter,
                        expectedListOfRequestFilteredByAccepted
                )
        ));

        // Test 10

        var rejectedFilter = RecommendationRequestFilter.builder()
                .status(RequestStatus.REJECTED)
                .build();

        var expectedListOfRequestFilteredByRejected = Set.of(
                mapper.toDto(rejectedRecommendationRequest1),
                mapper.toDto(rejectedRecommendationRequest2)
        );

        testData.add(Arguments.of(
                "Test for search only rejected requests",
                new GetRequestTestParam(
                        rejectedFilter,
                        expectedListOfRequestFilteredByRejected
                )
        ));

        // Test 11

        var pendingFilter = RecommendationRequestFilter.builder()
                .status(RequestStatus.PENDING)
                .build();

        var expectedListOfRequestFilteredByPending = Set.of(
                mapper.toDto(pendingRecommendationRequest)
        );

        testData.add(Arguments.of(
                "Test for search only pended requests",
                new GetRequestTestParam(
                        pendingFilter,
                        expectedListOfRequestFilteredByPending
                )
        ));

        // Test 12

        var combinedFilter = RecommendationRequestFilter.builder()
                .message("message")
                .requesterId(1L)
                .skillIds(Set.of(2L))
                .build();

        var expectedListOfRequestFilteredByMultipleParams = Set.of(
                mapper.toDto(acceptedRecommendationRequest1),
                mapper.toDto(acceptedRecommendationRequest2),
                mapper.toDto(rejectedRecommendationRequest1)
        );

        testData.add(Arguments.of(
                "Test for search requests by multiple parameters",
                new GetRequestTestParam(
                        combinedFilter,
                        expectedListOfRequestFilteredByMultipleParams
                )
        ));
    }

    @BeforeEach
    void setUp() {
        when(recommendationRequestRepository.findAll()).thenReturn(allRecommendationRequests);
    }

    private static Stream<Arguments> provideTestData() {
        return testData.stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestData")
    @DisplayName("Test GetRecommendationRequest")
    void testGetRecommendationRequest(String testName, GetRequestTestParam param) {

        Set<RecommendationRequestDto> actualRecommendation = new HashSet<>(recommendationRequestService.getRequests(
                param.filter()
        ));

        Set<RecommendationRequestDto> expectedRecommendation = param.expectedRecommendations();

        assertEquals(expectedRecommendation, actualRecommendation);
    }
}
