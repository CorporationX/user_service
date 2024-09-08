package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestMapper;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.Predicates;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static school.faang.user_service.service.MentorshipRequestService.MENTOR_IS_ALREADY_ACCEPTED;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    MentorshipRequestService mentorshipRequestService;

    @Mock
    MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    MentorshipRequestRepository repository;
    private final Predicates predicates = new Predicates();

    @InjectMocks
    MentorshipRequestService service;

    @Test
    void test_service_method_called_() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(dto, List.of(predicates.userExistsPredicate, predicates.sameUserPredicate, predicates.requestTimeExceededPredicate))).thenReturn(new Validated(null));
        service.requestMentorship(dto);
        verify(repository).create(dto.getRequesterId(),dto.getReceiverId(),dto.getDescription());
    }

    @Test
    void test_service_method_not_called(){
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(dto,List.of(predicates.userExistsPredicate, predicates.sameUserPredicate, predicates.requestTimeExceededPredicate))).thenReturn(new NotValidated(""));
        service.requestMentorship(dto);
        verifyNoInteractions(repository);
    }

    @Test
    void getRequests() {
        // Given
        RequestFilterDto filterDto = new RequestFilterDto("Test Description", 1L, 2L, RequestStatus.ACCEPTED);
        MentorshipRequest requestFilter = new MentorshipRequest();  // Returned by requestMapper.toEntity(filter)
        requestFilter.setRequester( User.builder().id(1L).build());
        requestFilter.setReceiver( User.builder().id(2L).build());

        MentorshipRequest request1 = new MentorshipRequest();
        request1.setDescription("Test Description");
        request1.setRequester(User.builder().id(1L).build());
        request1.setReceiver(User.builder().id(2L).build());
        request1.setStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> mockRequestList = List.of(request1);
        Optional<List<MentorshipRequest>> optionalRequestList = Optional.of(mockRequestList);

        when(repository.getRequests()).thenReturn(optionalRequestList);

        // When
        service.getRequests(filterDto);

        // Then
        verify(repository, times(1)).getRequests();  // Ensure repository.getRequests() was called once
        verify(requestMapper, times(1)).toEntity(filterDto);  // Ensure requestMapper was called

    }


    @Test
    void testGetRequestsWithEmptyRepository() {
        // Given
        RequestFilterDto filterDto = new RequestFilterDto("Test Description", 1L, 2L, RequestStatus.ACCEPTED);
        when(repository.getRequests()).thenReturn(Optional.empty());

        // When
        service.getRequests(filterDto);

        // Then
        verify(repository, times(1)).getRequests();  // Ensure repository.getRequests() was called once
        verifyNoMoreInteractions(repository);  // Ensure no further interaction with the repository
    }

    @Test
    void acceptRequest_ShouldUpdateStatus_WhenRequestIsNotAccepted() throws Exception {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        // Arrange
        when(repository.getMentorshipRequestById(1L)).thenReturn(request);

        // Act
        service.acceptRequest(1L);

        // Assert
        verify(repository, times(1)).updateMentorshipRequestStatusByRequesterId(1L, RequestStatus.ACCEPTED);
    }


    @Test
    void acceptRequest_ShouldThrowException_WhenRequestIsAlreadyAccepted() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        // Arrange
        request.setStatus(RequestStatus.ACCEPTED);
        when(repository.getMentorshipRequestById(1L)).thenReturn(request);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> service.acceptRequest(1L));
        assertEquals(MENTOR_IS_ALREADY_ACCEPTED, exception.getMessage());
        verify(repository, never()).updateMentorshipRequestStatusByRequesterId(anyLong(), any(RequestStatus.class));
    }

    @Test
    void acceptRequest_ShouldThrowException_WhenRequestIsNotFound() {
        // Arrange
        when(repository.getMentorshipRequestById(1L)).thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> service.acceptRequest(1L));
        verify(repository, never()).updateMentorshipRequestStatusByRequesterId(anyLong(), any(RequestStatus.class));
    }

    @Test
    void rejectRequesst_ShouldThrowException_WhenRequestIsNotFound() {
        when(repository.getMentorshipRequestById(1L)).thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> service.rejectRequest(1L,any()));

        verify(repository, never()).updateMentorshipRequestStatusByRequesterId(anyLong(), any(RequestStatus.class));
    }

    @Test
    void rejectRequest_ShouldCall_updateMentorshipRequestStatusByRequesterId(){
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setRejectionReason("reason");
        request.setStatus(RequestStatus.PENDING);
        when(repository.getMentorshipRequestById(1L)).thenReturn(request);
        RejectionDto rejectionDto = RejectionDto.builder().reason("reason").build();

        service.rejectRequest(request.getId(),rejectionDto);

        verify(repository).updateMentorshipRequestStatusWithReasonByRequesterId(request.getId(),RequestStatus.REJECTED, rejectionDto.getReason());
    }
}