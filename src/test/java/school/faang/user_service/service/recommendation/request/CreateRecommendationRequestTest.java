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
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CreateRecommendationRequestTest {

    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    private SkillRequestRepository skillRequestRepository;

    @MockBean
    private SkillRepository skillRepository;
    private static List<Skill> skillRepositoryData;

    @MockBean
    private UserRepository userRepository;
    private static List<User> userRepositoryData;

    @Autowired
    private RecommendationRequestService recommendationRequestService;

    private static Period COOLDOWN_OF_REQUEST_RECOMMENDATION = Period.ofMonths(6);

    @BeforeAll
    public static void setUpRepository() {

    }

    private final static LocalDateTime DATE_OF_LAST_RECOMMENDATION = LocalDateTime.of(
            2024, 7, 13, 2, 58, 32
    );

    @BeforeEach
    public void setUpMocks() {
        when(skillRepository.countExisting(
                List.of(3L, 6L)
        )).thenReturn(2);
        when(skillRepository.countExisting(
                List.of(3L)
        )).thenReturn(1);
        when(skillRepository.countExisting(
                List.of(6L)
        )).thenReturn(1);

        when(userRepository.existsById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 1L || id == 2L;
        });

        when(recommendationRequestRepository.findLatestRecommendationRequestFromRequesterToReceiver(
                1L, 2L
        )).thenReturn(Optional.of(
                        RecommendationRequest.builder()
                                .id(1L)
                                .message("Test msg")
                                .requester(User.builder().id(1L).build())
                                .receiver(User.builder().id(2L).build())
                                .createdAt(DATE_OF_LAST_RECOMMENDATION)
                                .build()
                )
        );
    }

    private record CreateRecommendationRequestTestParam(
            RecommendationRequestDto request,
            ValidationException expectedException
    ) {
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(
                        "Test for message request is null",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        null,
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.BLANK_RECOMMENDATION_MESSAGE
                                )
                        )
                ),
                Arguments.of(
                        "Test for message of request is blank",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "    ",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.BLANK_RECOMMENDATION_MESSAGE
                                )
                        )
                ),
                Arguments.of(
                        "Test for message of request is empty",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.BLANK_RECOMMENDATION_MESSAGE
                                )
                        )
                ),
                Arguments.of(
                        "Test for inexistent requester",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        9L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.USER_DOES_NOT_EXIST.getMessage() + "Check the requester id.",
                                        9L
                                )
                        )
                ),
                Arguments.of(
                        "Test for inexistent receiver",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        9L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.USER_DOES_NOT_EXIST.getMessage() + "Check the receiver id.",
                                        9L
                                )
                        )
                ),
                Arguments.of(
                        "Test for self-request ",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        1L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException("Author and receiver of a recommendation request cannot be the same person")
                        )
                ),
                Arguments.of(
                        "Test for request with inexistent skills",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 1L),
                                        1L,
                                        9L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.SKILLS_DONT_EXIST
                                )
                        )
                ),
                Arguments.of(
                        "Test for cooldown reques",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).minusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).minusNanos(1)
                                ),
                                new ValidationException(
                                        ExceptionMessage.RECOMMENDATION_COOLDOWN_NOT_EXCEEDED,
                                        COOLDOWN_OF_REQUEST_RECOMMENDATION.toString()
                                )
                        )
                ),
                Arguments.of(
                        "Test for create a valid recommendation",
                        new CreateRecommendationRequestTestParam(
                                new RecommendationRequestDto(
                                        null,
                                        "msg",
                                        RequestStatus.ACCEPTED,
                                        List.of(3L, 6L),
                                        1L,
                                        2L,
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1),
                                        DATE_OF_LAST_RECOMMENDATION.plus(COOLDOWN_OF_REQUEST_RECOMMENDATION).plusNanos(1)
                                ),
                                null
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestData")
    @DisplayName("Test CreateRecommendation request")
    void testCreateRequest(String testName, CreateRecommendationRequestTestParam param) {
        if(param.expectedException() != null){
            assertThrows(
                    param.expectedException().getClass(),
                    () -> recommendationRequestService.create(param.request())
            );
        } else {
            recommendationRequestService.create(param.request());

            verify(recommendationRequestRepository, times(1)).create(
                    param.request().getRequesterId(),
                    param.request().getReceiverId(),
                    param.request().getMessage()
            );
            verify(skillRequestRepository, times(1)).create(
                    param.request().getRequesterId(),
                    param.request().getReceiverId()
            );
        }
    }
}
