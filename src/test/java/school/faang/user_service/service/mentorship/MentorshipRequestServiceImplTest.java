package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.EmptyResultDataAccessException;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.impl.mentorship.MentorshipRequestServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MentorshipRequestServiceImplTest {
    @Mock
    MentorshipRequestRepository repository;

    @Mock
    private MentorshipAcceptedEventPublisher mentorshipPublisher;

    @Mock
    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @InjectMocks
    MentorshipRequestServiceImpl service;

    @Test
    void testGetRequestsWithEmptyRepository() {
        // Given
        RequestFilter filterDto = new RequestFilter("Test Description", 1L, 2L, RequestStatus.ACCEPTED);
        when(repository.findAll()).thenReturn(List.of());

        // When
        service.getRequests(filterDto);

        // Then
        verify(repository, times(1)).findAll();  // Ensure repository.getRequests() was called once
        verifyNoMoreInteractions(repository);  // Ensure no further interaction with the repository
    }

    @Test
    void acceptRequest_ShouldUpdateStatus_WhenRequestIsNotAccepted() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setRequester(User.builder().id(1L).build());
        request.setReceiver(User.builder().id(2L).build());
        request.setStatus(RequestStatus.PENDING);
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(request));

        // Act
        service.acceptRequest(1L);

        request.setStatus(RequestStatus.ACCEPTED);
        // Assert
        verify(repository, times(2)).save(request);
    }


    @Test
    void acceptRequest_ShouldThrowException_WhenRequestIsAlreadyAccepted() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        // Arrange
        request.setStatus(RequestStatus.ACCEPTED);
        when(repository.findById(1L)).thenReturn(Optional.of(request));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> service.acceptRequest(1L));
        assertEquals("Mentor request is already accepter", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void acceptRequest_ShouldThrowException_WhenRequestIsNotFound() {
        // Arrange
        when(repository.findById(1L)).thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> service.acceptRequest(1L));
        verify(repository, never()).save(any());
    }

    @Test
    void rejectRequest_ShouldThrowException_WhenRequestIsNotFound() {
        when(repository.findById(1L)).thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> service.rejectRequest(1L, any()));

        verify(repository, never()).save(any());
    }

    @Test
    void rejectRequest_ShouldCall_updateMentorshipRequestStatusByRequesterId() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setRejectionReason("reason");
        request.setStatus(RequestStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(request));
        Rejection rejectionDto = Rejection.builder().reason("reason").build();

        service.rejectRequest(request.getId(), rejectionDto);


        verify(repository).save(request);
    }
}