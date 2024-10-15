package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.EmptyResultDataAccessException;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.Rejection;
import school.faang.user_service.model.dto.RequestFilter;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.impl.mentorship.MentorshipRequestServiceImpl;
import school.faang.user_service.util.predicate.PredicateResult;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.RequestFilterPredicate;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static school.faang.user_service.service.impl.mentorship.MentorshipRequestServiceImpl.MENTOR_IS_ALREADY_ACCEPTED;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MentorshipRequestServiceImplTest {

    @Mock
    MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    MentorshipRequestRepository repository;

    @Spy
    private RequestFilterPredicate predicates;

    @InjectMocks
    MentorshipRequestServiceImpl service;

    @Test
    void test_service_method_called_() {
        ArgumentCaptor<List<BiFunction<MentorshipRequestRepository, MentorshipRequestDto, PredicateResult>>> captor = ArgumentCaptor.forClass(List.class);
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(eq(dto)))
                .thenReturn(new Validated(null));

        service.requestMentorship(dto);

        verify(repository).create(dto.getRequesterId(),dto.getReceiverId(),dto.getDescription());
    }

    @Test
    void test_service_method_not_called(){
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(any())).thenReturn(new NotValidated(""));

        service.requestMentorship(dto);

        verifyNoInteractions(repository);
    }

    @Test
    void getRequests() {
        // Given
        RequestFilter filterDto = new RequestFilter("Test Description", 1L, 2L, RequestStatus.ACCEPTED);
        MentorshipRequest requestFilter = new MentorshipRequest();  // Returned by requestMapper.toEntity(filter)
        requestFilter.setRequester( User.builder().id(1L).build());
        requestFilter.setReceiver( User.builder().id(2L).build());

        MentorshipRequest request1 = new MentorshipRequest();
        request1.setDescription("Test Description");
        request1.setRequester(User.builder().id(1L).build());
        request1.setReceiver(User.builder().id(2L).build());
        request1.setStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> mockRequestList = List.of(request1);
       List<MentorshipRequest> optionalRequestList = mockRequestList;

        when(repository.findAll()).thenReturn(optionalRequestList);

        // When
        service.getRequests(filterDto);

        // Then
        verify(repository, times(1)).findAll();  // Ensure repository.getRequests() was called once

    }


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
    void acceptRequest_ShouldUpdateStatus_WhenRequestIsNotAccepted() throws Exception {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(request));

        // Act
        service.acceptRequest(1L);

        request.setStatus(RequestStatus.ACCEPTED);
        // Assert
        verify(repository, times(1)).save(request);
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
        assertEquals(MENTOR_IS_ALREADY_ACCEPTED, exception.getMessage());
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
    void rejectRequesst_ShouldThrowException_WhenRequestIsNotFound() {
        when(repository.findById(1L)).thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> service.rejectRequest(1L,any()));

        verify(repository,  never()).save(any());
    }

    @Test
    void rejectRequest_ShouldCall_updateMentorshipRequestStatusByRequesterId(){
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setRejectionReason("reason");
        request.setStatus(RequestStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(request));
        Rejection rejectionDto = Rejection.builder().reason("reason").build();

        service.rejectRequest(request.getId(),rejectionDto);


        verify(repository).save(request);
    }
}