package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    SkillRequestRepository skillRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private User requester;
    private User receiver;
    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private RecommendationRequest recommendationRequest;
    private SkillRequest skillRequest1;
    private SkillRequest skillRequest2;
    private SkillRequest skillRequest3;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .id(1L)
                .username("Requester")
                .build();

        receiver = User.builder()
                .id(2L)
                .username("Receiver")
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .title("Java")
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .title("Kotlin")
                .build();

        skill3 = Skill.builder()
                .id(3L)
                .title("Hibernate")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(0L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .message("Please confirm my skills")
                .build();

        skillRequest1 = SkillRequest.builder()
                .id(1L)
                .request(recommendationRequest)
                .skill(skill1)
                .build();

        skillRequest2 = SkillRequest.builder()
                .id(2L)
                .request(recommendationRequest)
                .skill(skill2)
                .build();

        skillRequest3 = SkillRequest.builder()
                .id(3L)
                .request(recommendationRequest)
                .skill(skill3)
                .build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(null)
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .status(RequestStatus.PENDING)
                .createdAt(recommendationRequest.getCreatedAt())
                .updatedAt(recommendationRequest.getUpdatedAt())
                .skillIds(Arrays.asList(1L, 2L, 3L))
                .message(recommendationRequest.getMessage())
                .build();
    }

    @Test
    @DisplayName("Create Recommendation Request Successfully")
    void testCreateRecommendationRequestSuccessfully() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(3L)).thenReturn(true);
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    if (savedRequest.getId() < 1L) {
                        savedRequest.setId(1L);
                        return savedRequest;
                    } else {
                        savedRequest.setSkills(List.of(skillRequest1, skillRequest2, skillRequest3));
                        return savedRequest;
                    }
                });
        when(skillRequestRepository.create(1L, 1L)).thenReturn(skillRequest1);
        when(skillRequestRepository.create(1L, 2L)).thenReturn(skillRequest2);
        when(skillRequestRepository.create(1L, 3L)).thenReturn(skillRequest3);


        RecommendationRequestDto requestFromDB = recommendationRequestService.create(recommendationRequestDto);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(recommendationRequestRepository, times(2)).save(any(RecommendationRequest.class));
        verify(skillRequestRepository, times(1)).create(eq(1L), eq(1L));
        verify(skillRequestRepository, times(1)).create(eq(1L), eq(2L));
        verify(skillRequestRepository, times(1)).create(eq(1L), eq(3L));
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);

        verify(recommendationRequestRepository, Mockito.times(2))
                .save(recommendationRequestCaptor.capture());
        assertEquals(recommendationRequestDto.getMessage(), recommendationRequestCaptor.getValue().getMessage());
        verify(skillRequestRepository).create(anyLong(), eq(1L));

        assertNotNull(requestFromDB);
        assertEquals(1L, requestFromDB.getId());
        assertEquals(recommendationRequestDto.getRequesterId(), requestFromDB.getRequesterId());
        assertEquals(recommendationRequestDto.getReceiverId(), requestFromDB.getReceiverId());
        assertEquals(recommendationRequestDto.getMessage(), requestFromDB.getMessage());
        assertEquals(recommendationRequestDto.getStatus(), requestFromDB.getStatus());
        assertEquals(recommendationRequestDto.getCreatedAt(), requestFromDB.getCreatedAt());
        assertEquals(recommendationRequestDto.getUpdatedAt(), requestFromDB.getUpdatedAt());
        assertEquals(recommendationRequestDto.getSkillIds(), requestFromDB.getSkillIds());
    }

    @Test
    @DisplayName("MessageIsEmpty")
    void testCreateRecommendationRequest_MessageIsEmpty() {

        RecommendationRequestDto badRequestDto = RecommendationRequestDto.builder()
                .createdAt(LocalDateTime.now().minusMonths(7))
                .message(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(badRequestDto)
        );

        assertEquals("Message cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("RequestPeriodIsNotExceeded")
    void testCreateRecommendationRequest_RequestPeriodIsNotExceeded() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Recommendation request must be sent once in 6 months", exception.getMessage());
    }

    @Test
    @DisplayName("SkillIdNotExist")
    void testCreateRecommendationRequest_SkillIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(3L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Skill with id = 3 not exist", exception.getMessage());
    }
}


