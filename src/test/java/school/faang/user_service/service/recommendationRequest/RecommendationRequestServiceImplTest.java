package school.faang.user_service.service.recommendationRequest;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.RecommendationRequestResponseDto;
import school.faang.user_service.dto.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationFrequencyException;
import school.faang.user_service.repository.genericSpecification.GenericSpecification;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.impl.RecommendationRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.testConstants.TestConstants.EXISTING_SKILLS_COUNT;
import static school.faang.user_service.testConstants.TestConstants.INVALID_RECEIVER_ID;
import static school.faang.user_service.testConstants.TestConstants.INVALID_REQUESTER_ID;
import static school.faang.user_service.testConstants.TestConstants.MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.RECOMMENDATION_REQUEST_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.RECOMMENDATION_REQUEST_NOT_FOUND_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.REJECTION_REASON;
import static school.faang.user_service.testConstants.TestConstants.SKILLS_EMPTY_OR_NULL_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.SKILL_IDS;
import static school.faang.user_service.testConstants.TestConstants.SOME_SKILLS_DOES_NOT_EXIST_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.SUCCESS_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.USER_NOT_FOUND_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.USER_SENT_RECOMMENDATION_REQUEST_LAST_SIX_MONTH_EXCEPTION_MESSAGE;
import static school.faang.user_service.testConstants.TestConstants.VALID_RECEIVER_ID;
import static school.faang.user_service.testConstants.TestConstants.VALID_RECOMMENDATION_ID;
import static school.faang.user_service.testConstants.TestConstants.VALID_REQUESTER_ID;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {

    @InjectMocks
    RecommendationRequestServiceImpl recommendationRequestService;

    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    UserRepository userRepository;

    private RecommendationRequestResponseDto recommendationRequestResponseDto;
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private SearchRequest searchRequest;
    private RejectionDto rejectionDto;

    @BeforeEach
    public void init() {
        // recommendationRequestResponseDto
        recommendationRequestResponseDto = new RecommendationRequestResponseDto();
        recommendationRequestResponseDto.setMessage(MESSAGE);

        // recommendationRequestDto
        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setRequesterId(VALID_REQUESTER_ID);
        recommendationRequestDto.setReceiverId(VALID_RECEIVER_ID);
        recommendationRequestDto.setSkillIds(SKILL_IDS);
        recommendationRequestDto.setMessage(MESSAGE);

        // recommendationRequest
        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(VALID_REQUESTER_ID);
        recommendationRequest.setMessage(MESSAGE);

        // rejectionDto
        rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason(REJECTION_REASON);

        // searchRequest
        searchRequest = new SearchRequest();
    }

    @Test
    public void testValidateUserExistence_whenInvalidRequesterId_thenEntityNotFoundExceptionIsThrown() {
        // Arrange
        when(userRepository.existsById(INVALID_REQUESTER_ID)).thenReturn(false);
        recommendationRequestDto.setRequesterId(INVALID_REQUESTER_ID);

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));

        // Assert
        assertEquals(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, INVALID_REQUESTER_ID), exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(INVALID_REQUESTER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testValidateUserExistence_whenInvalidReceiverId_thenEntityNotFoundExceptionIsThrown() {
        // Arrange
        when(userRepository.existsById(INVALID_RECEIVER_ID)).thenReturn(false);
        recommendationRequestDto.setRequesterId(INVALID_RECEIVER_ID);

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));

        // Assert
        assertEquals(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, INVALID_RECEIVER_ID), exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(INVALID_RECEIVER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testValidateRecommendationRequestFrequency_whenRequestIsLessThanSixMonths_thenThrowRecommendationFrequencyException() {
        // Arrange
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));
        when(userRepository.existsById(VALID_REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(VALID_RECEIVER_ID)).thenReturn(true);
        when(recommendationRequestRepository.findLatestRecommendationInLast6Months(VALID_REQUESTER_ID))
                .thenReturn(Optional.of(recommendationRequest));

        // Act
        RecommendationFrequencyException exception = assertThrows(RecommendationFrequencyException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));

        // Assert
        assertEquals(String.format(USER_SENT_RECOMMENDATION_REQUEST_LAST_SIX_MONTH_EXCEPTION_MESSAGE,
                VALID_REQUESTER_ID, recommendationRequest.getCreatedAt()), exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(VALID_REQUESTER_ID);
        verify(userRepository, times(1)).existsById(VALID_RECEIVER_ID);
        verify(recommendationRequestRepository, times(1))
                .findLatestRecommendationInLast6Months(VALID_REQUESTER_ID);
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository);
    }

    @Test
    public void testValidateSkillsExist_whenSkillIdsIsNullOrEmpty_thenIllegalArgumentException() {
        // Arrange
        recommendationRequestDto.setSkillIds(null);
        when(userRepository.existsById(VALID_REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(VALID_RECEIVER_ID)).thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));

        // Assert
        assertEquals(SKILLS_EMPTY_OR_NULL_EXCEPTION_MESSAGE, exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(VALID_REQUESTER_ID);
        verify(userRepository, times(1)).existsById(VALID_RECEIVER_ID);
        verify(recommendationRequestRepository, times(1))
                .findLatestRecommendationInLast6Months(VALID_REQUESTER_ID);
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository);
    }

    @Test
    public void testValidateSkillsExist_whenSkillIdsDoesNotMatchWithExistingSkillCount_thenIllegalArgumentException() {
        // Arrange
        when(userRepository.existsById(VALID_REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(VALID_RECEIVER_ID)).thenReturn(true);
        when(skillRepository.getExistingSkillCountByIds(recommendationRequestDto
                .getSkillIds())).thenReturn((long) EXISTING_SKILLS_COUNT);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));

        // Assert
        assertEquals(SOME_SKILLS_DOES_NOT_EXIST_EXCEPTION_MESSAGE, exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(VALID_REQUESTER_ID);
        verify(userRepository, times(1)).existsById(VALID_RECEIVER_ID);
        verify(skillRepository, times(1))
                .getExistingSkillCountByIds(recommendationRequestDto.getSkillIds());
        verify(recommendationRequestRepository, times(1))
                .findLatestRecommendationInLast6Months(VALID_REQUESTER_ID);
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository);
    }

    @Test
    public void testCreateRecommendationRequest_whenValidRequest_thenRecommendationRequestIsSaved() {
        // Arrange
        when(userRepository.existsById(VALID_REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(VALID_RECEIVER_ID)).thenReturn(true);
        when(skillRepository.getExistingSkillCountByIds(recommendationRequestDto.getSkillIds()))
                .thenReturn((long) recommendationRequestDto.getSkillIds().size());
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);

        // Act
        String result = recommendationRequestService.requestRecommendation(recommendationRequestDto);

        // Assert
        assertEquals(SUCCESS_MESSAGE, result);
        assertEquals(recommendationRequest.getMessage(), recommendationRequest.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsById(VALID_REQUESTER_ID);
        verify(userRepository, times(1)).existsById(VALID_RECEIVER_ID);
        verify(skillRepository, times(1))
                .getExistingSkillCountByIds(recommendationRequestDto.getSkillIds());
        verify(recommendationRequestRepository, times(1))
                .findLatestRecommendationInLast6Months(VALID_REQUESTER_ID);
        verify(recommendationRequestMapper, times(1)).toEntity(recommendationRequestDto);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);

        verifyNoMoreInteractions(userRepository, recommendationRequestRepository,
                recommendationRequestMapper, skillRepository
        );
    }

    @Test
    public void testFindRecommendationRequestById_whenInvalidIdIsProvided_thenEntityNotFoundExceptionThrown() {
        // Arrange
        when(recommendationRequestRepository.findById(INVALID_REQUESTER_ID)).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.getById(INVALID_REQUESTER_ID));

        // Assert
        assertEquals(String.format(RECOMMENDATION_REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, INVALID_REQUESTER_ID),
                exception.getMessage());

        // Verify interactions
        verify(recommendationRequestRepository, times(1)).findById(INVALID_REQUESTER_ID);
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    public void testGetById_whenValidIdProvided_thenRecommendationRequestResponseDtoReturned() {
        // Arrange
        when(recommendationRequestRepository.findById(VALID_REQUESTER_ID))
                .thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toResponse(recommendationRequest))
                .thenReturn(recommendationRequestResponseDto);

        // Act
        RecommendationRequestResponseDto result = recommendationRequestService.getById(VALID_REQUESTER_ID);

        // Assert
        assertEquals(recommendationRequestResponseDto, result);

        // Verify interactions
        verify(recommendationRequestRepository, times(1)).findById(VALID_REQUESTER_ID);
        verify(recommendationRequestMapper, times(1)).toResponse(recommendationRequest);
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    public void testRejectRequest_whenRecommendationNotFound_thenEntityNotFoundExceptionThrown() {
        // Arrange
        when(recommendationRequestRepository
                .findRecommendationRequestByIdAndStatus(anyLong(), any())).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationRequestService.rejectRequest(anyLong(), any()));

        // Assert
        assertEquals(RECOMMENDATION_REQUEST_EXCEPTION_MESSAGE, exception.getMessage());

        // Verify interactions
        verify(recommendationRequestRepository, times(1))
                .findRecommendationRequestByIdAndStatus(anyLong(), any());
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    public void testRejectRequest_whenValidRequest_thenSuccessMessage() {
        // Arrange
        when(recommendationRequestRepository
                .findRecommendationRequestByIdAndStatus(anyLong(), any())).thenReturn(Optional.of(recommendationRequest));

        // Act
        String result = recommendationRequestService.rejectRequest(VALID_RECOMMENDATION_ID, rejectionDto);

        // Assert
        assertEquals("Recommendation successfully rejected", result);

        // Verify interactions
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestRepository, times(1))
                .findRecommendationRequestByIdAndStatus(anyLong(), any());
    }

    @Test
    public void testSearch_whenValidSearchRequest_thenListOfRecommendationRequestResponseDto() {
        // Arrange
        List<RecommendationRequest> mockEntities = List.of(new RecommendationRequest(), new RecommendationRequest());
        List<RecommendationRequestResponseDto> mockResponseDtos = List.of(new RecommendationRequestResponseDto(), new RecommendationRequestResponseDto());

        when(recommendationRequestRepository.findAll(any(GenericSpecification.class))).thenReturn(mockEntities);
        when(recommendationRequestMapper.toResponse(mockEntities)).thenReturn(mockResponseDtos);

        // Act
        List<RecommendationRequestResponseDto> result = recommendationRequestService.search(searchRequest);

        // Assert
        assertEquals(mockResponseDtos, result);

        // Verify interactions
        verify(recommendationRequestRepository, times(1)).findAll(Mockito.any(GenericSpecification.class));
        verify(recommendationRequestMapper, times(1)).toResponse(mockEntities);

    }

}
