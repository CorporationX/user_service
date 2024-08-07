package school.faang.user_service.service.recommendation.request;

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
import school.faang.user_service.dto.recommendation.RejectionRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RejectRecommendationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class RejectRecommendationRequestTest {
    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;

    @Autowired
    private RecommendationRequestService recommendationRequestService;

    private record RejectRequestTestParam(
            long rejectedRequestID,
            RejectionRequestDto rejectionRequestDto,
            Optional<RecommendationRequest> mockRecommendationRequest,
            Class<? extends Throwable> expectedException,
            RecommendationRequestDto expectedDto
    ) {
    }

    private static Stream<Arguments> provideTestData() {

        final long NOT_EXISTED_REQUEST_ID = -1;
        final long ACCEPTED_REQUEST_ID = 1;
        final long REJECTED_REQUEST_ID = 2;
        final long PENDING_REQUEST_ID = 3;

        final LocalDateTime CREATE_AT = LocalDateTime.now();
        final LocalDateTime UPDATE_AT = CREATE_AT.plusDays(1);

        var acceptedRecommendationRequest = RecommendationRequest.builder()
                .id(ACCEPTED_REQUEST_ID)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(2).build())
                .message("Test message")
                .status(RequestStatus.ACCEPTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var rejectedRecommendationRequest = RecommendationRequest.builder()
                .id(REJECTED_REQUEST_ID)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(2).build())
                .message("Test message")
                .status(RequestStatus.REJECTED)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var pendingRecommendationRequest = RecommendationRequest.builder()
                .id(PENDING_REQUEST_ID)
                .requester(User.builder().id(1).build())
                .receiver(User.builder().id(2).build())
                .skills(List.of(SkillRequest.builder().id(1L).build()))
                .message("Test message")
                .status(RequestStatus.PENDING)
                .createdAt(CREATE_AT)
                .updatedAt(UPDATE_AT)
                .build();

        var rejectionRequest = new RejectionRequestDto("Wrong recommendation");

        var expectedRecommendationRequest = new RecommendationRequestDto(
                PENDING_REQUEST_ID,
                "Test message",
                RequestStatus.REJECTED,
                List.of(1L),
                1L,
                2L,
                CREATE_AT,
                UPDATE_AT
        );

        return Stream.of(
                Arguments.of(
                        "Test for rejecting a non-existent request",
                        new RejectRequestTestParam(
                                NOT_EXISTED_REQUEST_ID,
                                null,
                                Optional.empty(),
                                NoSuchElementException.class,
                                null
                        )
                ),
                Arguments.of(
                        "Test for rejecting an already accepted request",
                        new RejectRequestTestParam(
                                ACCEPTED_REQUEST_ID,
                                null,
                                Optional.of(acceptedRecommendationRequest),
                                RejectRecommendationException.class,
                                null
                        )
                ),
                Arguments.of(
                        "Test for rejecting an already rejected request",
                        new RejectRequestTestParam(
                                REJECTED_REQUEST_ID,
                                null,
                                Optional.of(rejectedRecommendationRequest),
                                RejectRecommendationException.class,
                                null
                        )
                ),
                Arguments.of(
                        "Test for rejecting a pending request",
                        new RejectRequestTestParam(
                                PENDING_REQUEST_ID,
                                rejectionRequest,
                                Optional.of(pendingRecommendationRequest),
                                null,
                                expectedRecommendationRequest
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestData")
    @DisplayName("Test RejectRecommendationRequest")
    void testRejectRecommendationRequest(String testName, RejectRequestTestParam param) {
        long id = param.rejectedRequestID();

        when(recommendationRequestRepository.findById(id)).thenReturn(param.mockRecommendationRequest());

        if (param.expectedException() != null) {
            assertThrows(param.expectedException(), () -> recommendationRequestService.rejectRequest(
                    param.rejectedRequestID(), param.rejectionRequestDto()
            ));
        } else {
            RecommendationRequestDto result = recommendationRequestService.rejectRequest(
                    param.rejectedRequestID(), param.rejectionRequestDto()
            );

            String actualReason = recommendationRequestRepository.findById(id).get().getRejectionReason();

            assertEquals(param.expectedDto(), result);
            assertEquals(param.rejectionRequestDto.getReason(), actualReason);
        }
    }
}
