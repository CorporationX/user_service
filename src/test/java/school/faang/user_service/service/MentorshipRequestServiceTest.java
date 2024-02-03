package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRejectDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    @Test
    public void testMentorshipRequestReceiverExistsIsInvalid() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setReceiverId(1L);

        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals("There are no this receiver in data base", illegalArgumentException.getMessage());
    }

    @Test
    public void testMentorshipRequestRequesterExistsIsInvalid() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);

        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals("There are no this requester in data base", illegalArgumentException.getMessage());
    }

    @Test
    public void testMentorshipRequestToYourselfIsInvalid() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setReceiverId(1L);
        mentorshipRequestDto.setRequesterId(1L);
        when(userRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
        assertEquals(illegalArgumentException.getMessage(), "You can not send a request to yourself");
    }

    @Test
    public void testRequestMentorshipExistsIsInvalid() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setReceiverId(1L);
        mentorshipRequestDto.setRequesterId(2L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals(illegalArgumentException.getMessage(), "There are not find request");
    }

    @Test
    public void testRequestMentorshipMoreThanThreeMonthsIsInvalid() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setReceiverId(1L);
        mentorshipRequestDto.setRequesterId(2L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setUpdatedAt(LocalDateTime.now().minusMonths(2));
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong())).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals(illegalArgumentException.getMessage(), "Less than 3 months have passed since last request");
    }

    @Test
    public void testRequestMentorshipIsCreated() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setReceiverId(1L);
        mentorshipRequestDto.setRequesterId(2L);
        mentorshipRequestDto.setDescription("description");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setUpdatedAt(LocalDateTime.now().minusMonths(4));
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong())).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository, times(1)).create(2L, 1L, "description");
    }

    @Test
    public void testRejectRequestExistsIsInvalid() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.rejectRequest(1L, new MentorshipRejectDto("reason")));

        assertEquals(illegalArgumentException.getMessage(), "There is blank request");
        verify(mentorshipRequestRepository, never()).save(any());
        verify(mentorshipRequestMapper, never()).toRejectionDto(any());
    }

    @Test
    public void testRejectRequestReasonIsGiven() {
        MentorshipRejectDto mentorshipRejectDto = new MentorshipRejectDto("reason");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRejectionReason("something");
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, mentorshipRejectDto);
        assertEquals("reason", mentorshipRequest.getRejectionReason());
    }

    @Test
    public void testRejectRequestStatusChanged() {
        MentorshipRejectDto mentorshipRejectDto = new MentorshipRejectDto("reason");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, mentorshipRejectDto);
        assertEquals(mentorshipRequest.getStatus(), RequestStatus.REJECTED);
    }

    @Test
    public void testRejectRequestIsSaved() {
        MentorshipRejectDto mentorshipRejectDto = new MentorshipRejectDto("reason");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, mentorshipRejectDto);
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    public void testRejectRequestMapper() {
        MentorshipRejectDto mentorshipRejectDto = new MentorshipRejectDto("reason");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, mentorshipRejectDto);
        verify(mentorshipRequestMapper, times(1)).toRejectionDto(mentorshipRequest);
    }

    @Test
    public void testAcceptRequestNotFoundRequestInDB() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(1L));
        assertEquals(illegalArgumentException.getMessage(), "There is no request in DB with this ID");
    }

    @Test
    public void testAcceptRequestIsAlreadyMentor() {
        User mentor = new User();
        User requester = new User();

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setReceiver(mentor);
        mentorshipRequest.setRequester(requester);

        requester.setMentors(new ArrayList<>());
        requester.getMentors().add(mentor);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(1L));
        assertEquals(illegalArgumentException.getMessage(), "The mentor is already the sender's mentor");
        verify(mentorshipRequestRepository, never()).save(mentorshipRequest);
    }

    @Test
    public void testAcceptRequestAddedToList() {
        User mentor = new User();
        User requester = new User();

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setReceiver(mentor);
        mentorshipRequest.setRequester(requester);

        requester.setMentors(new ArrayList<>());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(1L);
        assertTrue(requester.getMentors().contains(mentor));
    }

    @Test
    public void testAcceptRequestStatusChanged() {
        User mentor = new User();
        User requester = new User();

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setReceiver(mentor);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        requester.setMentors(new ArrayList<>());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(1L);
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    public void testAcceptRequestIsSaved() {
        User mentor = new User();
        User requester = new User();

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setReceiver(mentor);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        requester.setMentors(new ArrayList<>());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.acceptRequest(1L);
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }
}
