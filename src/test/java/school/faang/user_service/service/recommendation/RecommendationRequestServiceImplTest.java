package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.kafka.events.RecommendationRequestEvent;
import school.faang.user_service.kafka.producer.KafkaDataSenderImpl;
import school.faang.user_service.kafka.producer.KafkaTopics;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {
    private static final long REQUESTER_ID = 1L;
    private static  final long RECEIVER_ID = 2L;
    private static final long REQUEST_ID = 100L;
    private static final long SKILL_ID = 10L;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;
    @Mock
    private UserContext userContext;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private KafkaDataSenderImpl kafkaDataSender;
    @Mock
    private KafkaTopics kafkaTopics;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private User requester;
    private User receiver;
    private Skill skill;

    @BeforeEach
    void setUp() {
        requester = User.builder().id(REQUESTER_ID).build();
        receiver = User.builder().id(RECEIVER_ID).build();
        skill = Skill.builder().id(SKILL_ID).build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .message("Test message")
                .skillIds(List.of(SKILL_ID))
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(REQUEST_ID)
                .requester(requester)
                .receiver(receiver)
                .message("Test message")
                .skills(List.of())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("create method tests")
    class CreateMethodTests {

        @Test
        @DisplayName("When create is called with valid DTO, then create and return DTO")
        void testCreate_Success() {
            recommendationRequestDto = RecommendationRequestDto.builder()
                    .receiverId(RECEIVER_ID)
                    .skillIds(List.of(SKILL_ID))
                    .build();

            when(userContext.getUserId()).thenReturn(REQUESTER_ID);
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.of(requester));
            when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));

            when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID))
                    .thenReturn(Optional.empty());

            RecommendationRequest newRequest = new RecommendationRequest();
            doReturn(newRequest).when(recommendationRequestMapper).toEntity(recommendationRequestDto);

            when(recommendationRequestRepository.existsById(any())).thenReturn(false);
            when(skillRepository.findAllById(any())).thenReturn(List.of(skill));

            RecommendationRequest savedRequest = new RecommendationRequest();
            savedRequest.setId(REQUEST_ID);

            when(recommendationRequestRepository.save(newRequest)).thenReturn(savedRequest);
            when(skillRequestRepository.save(any(SkillRequest.class))).thenReturn(new SkillRequest());
            when(userMapper.toDtoNotification(any(User.class))).thenReturn(new UserDtoNotification());
            when(kafkaTopics.getRecommendationRequestTopic()).thenReturn("recommendation_request_topic");
            doNothing().when(kafkaDataSender).send(anyString(), any(RecommendationRequestEvent.class));

            RecommendationRequestDto resultDto = RecommendationRequestDto.builder()
                    .id(REQUEST_ID)
                    .build();
            doReturn(resultDto).when(recommendationRequestMapper).toDto(savedRequest);

            RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

            assertNotNull(result);
            assertEquals(REQUEST_ID, result.getId());

            verify(recommendationRequestRepository).save(newRequest);
            verify(skillRequestRepository).save(any(SkillRequest.class));
            verify(kafkaDataSender).send(eq("recommendation_request_topic"),
                    any(RecommendationRequestEvent.class));
        }

        @Test
        @DisplayName("When requester is not found, then throw EntityNotFoundException")
        void testCreate_requesterNotFound_throwsException() {
            when(userContext.getUserId()).thenReturn(REQUESTER_ID);
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> recommendationRequestService.create(recommendationRequestDto));
            assertEquals("Requester with id %s was not found".formatted(REQUESTER_ID), exception.getMessage());
        }

        @Test
        @DisplayName("When request was updated in last 6 months, then throw IllegalArgumentException")
        void testCreate_requestUpdatedRecently_throwsException() {
            recommendationRequest.setUpdatedAt(LocalDateTime.now().minus(Period.ofMonths(3)));
            when(userContext.getUserId()).thenReturn(REQUESTER_ID);
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.of(requester));
            when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
            when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID))
                    .thenReturn(Optional.of(recommendationRequest));
            when(recommendationRequestRepository.existsById(REQUEST_ID)).thenReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> recommendationRequestService.create(recommendationRequestDto));
            assertEquals("Recommendation request has already been updated in the last 6 months.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("When no skills are found, then throw EntityNotFoundException")
        void testCreate_skillsNotFound_throwsException() {
            recommendationRequestDto = RecommendationRequestDto.builder()
                    .receiverId(RECEIVER_ID)
                    .skillIds(List.of(SKILL_ID))
                    .build();
            when(userContext.getUserId()).thenReturn(REQUESTER_ID);
            when(userRepository.findById(REQUESTER_ID)).thenReturn(Optional.of(requester));
            when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
            when(recommendationRequestRepository.findLatestPendingRequest(REQUESTER_ID, RECEIVER_ID))
                    .thenReturn(Optional.empty());
            doReturn(new RecommendationRequest()).when(recommendationRequestMapper).toEntity(recommendationRequestDto);
            when(skillRepository.findAllById(any())).thenReturn(List.of());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> recommendationRequestService.create(recommendationRequestDto));
            assertEquals("Not all required skills exist in data base", exception.getMessage());

            verify(recommendationRequestRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getRequest method tests")
    class GetRequestMethodTests {
        @Test
        @DisplayName("When getRequest is called with existing ID, then return DTO")
        void testGetRequest_Success() {
            when(recommendationRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));

            RecommendationRequestDto result = recommendationRequestService.getRequest(REQUEST_ID);

            assertNotNull(result);
            assertEquals(REQUEST_ID, result.getId());
            verify(recommendationRequestMapper).toDto(recommendationRequest);
        }

        @Test
        @DisplayName("When getRequest is called with non-existing ID, then throw EntityNotFoundException")
        void testGetRequest_notFound_throwsException() {
            when(recommendationRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> recommendationRequestService.getRequest(REQUEST_ID));
            assertEquals("Recommendation request with id %s doesn't exist"
                    .formatted(REQUEST_ID), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("rejectRequest method tests")
    class RejectRequestMethodTests {

        private RejectionDto rejectionDto;

        @BeforeEach
        void setUp() {
            rejectionDto = new RejectionDto("Test rejection reason");
        }

        @Test
        @DisplayName("When rejectRequest is called for a PENDING request, then reject and return DTO")
        void testRejectRequest_Success() {
            when(recommendationRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));

            RecommendationRequestDto result = recommendationRequestService.rejectRequest(REQUEST_ID, rejectionDto);

            assertNotNull(result);
            assertEquals(RequestStatus.REJECTED, result.getStatus());
            verify(recommendationRequestRepository).save(recommendationRequest);
        }

        @Test
        @DisplayName("When rejecting a non-existent request, then throw EntityNotFoundException")
        void testRejectRequest_notFound_throwsException() {
            when(recommendationRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> recommendationRequestService.rejectRequest(REQUEST_ID, rejectionDto));
            assertEquals("Recommendation request with id %s doesn't exist"
                    .formatted(REQUEST_ID), exception.getMessage());
            verify(recommendationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("When rejecting a non-PENDING request, then throw IllegalArgumentException")
        void testRejectRequest_notPending_throwsException() {
            recommendationRequest.setStatus(RequestStatus.ACCEPTED);
            when(recommendationRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> recommendationRequestService.rejectRequest(REQUEST_ID, rejectionDto));
            assertEquals("Unable to reject request", exception.getMessage());
            verify(recommendationRequestRepository, never()).save(any());
        }
    }
}
