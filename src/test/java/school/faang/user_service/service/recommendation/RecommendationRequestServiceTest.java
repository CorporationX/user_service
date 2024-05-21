package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RecommendationRequestFilterInterface;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestRejectionException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService service;
    private RecommendationRequestValidator validator;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Spy
    private RecommendationRequestMapper mapper;
    private List<RecommendationRequestFilterInterface> filters;

    private Long id = 5L;

    private RecommendationRequestDto dto = null;
    private RecommendationRequest request = null;
    private RejectionDto rejectionDto = null;

    @BeforeEach
    public void setUp() {
        dto = new RecommendationRequestDto();
        dto.setRequesterId(id);
        dto.setReceiverId(id);
        dto.setSkills(Collections.singletonList(new SkillRequest()));
        validator = new RecommendationRequestValidator(
                userRepository,
                skillRepository,
                recommendationRequestRepository
        );
        request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        request.setMessage("Wowowow");
        request.setReceiver(new User());
        request.setRequester(new User());
        request.setId(id);
        rejectionDto = new RejectionDto();
        rejectionDto.setReason("Reject");
    }

    @Nested
    class PositiveTests{
        @Test
        void shouldCreateRecommendationRequest() {
            assertDoesNotThrow(()->service.create(dto));
            RecommendationRequest saved = verify(recommendationRequestRepository, times(1)).save(request);
            verify(skillRequestRepository, times(1)).saveAll(request.getSkills());
            verify(mapper, times(1)).toDto(saved);
        }

        @Test
        void shouldGetRequestsById() {
            when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(new RecommendationRequest()));
            assertDoesNotThrow(() -> service.getRequestById(anyLong()));
            verify(mapper, times(1)).toDto(any());
        }

        @Test
        void shouldRejectRequest() {
            when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
            verify(recommendationRequestRepository, times(1)).save(request);
            verify(mapper, times(1)).toDto(request);
            assertDoesNotThrow(() -> service.rejectRequest(anyLong(),rejectionDto));

        }
    }

    @Nested
    class NegativeTests{
        @Test
        void shouldThrowExceptionWhenRequesterIdNull() {
            dto.setRequesterId(null);
            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> service.create(dto)
            );
            assertEquals(REQUEST_REQUESTER_ID_EMPTY.getMessage(),exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenReceiverIdNull() {
            dto.setReceiverId(null);
            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> service.create(dto)
            );
            assertEquals(REQUEST_RECEIVER_ID_EMPTY.getMessage(),exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenSkillRequestsEmpty() {
            dto.setSkills(Collections.emptyList());
            DataValidationException exception = assertThrows(DataValidationException.class, () -> service.create(dto));
            assertEquals(REQUEST_SKILLS_EMPTY.getMessage(),exception.getMessage());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenRequestIdNull() {
            when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(RecommendationRequestNotFoundException.class, () -> service.getRequestById(anyLong()));
        }

        @Test
        void shouldThrowRejectExceptionWhenStatusNotPending() {
            when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
            request.setStatus(RequestStatus.REJECTED);
            verify(recommendationRequestRepository, times(0)).save(any());
            assertThrows(RecommendationRequestRejectionException.class, () -> service.rejectRequest(anyLong(), rejectionDto));
        }
    }
}