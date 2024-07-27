package school.faang.user_service.service;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;

    private AcceptMentorshipRequestDto acceptMentorshipRequestDto;
    private RejectRequestDto rejectRequestDto;
    private MentorshipRequestDto mentorshipRequestDto;
    private Long userId;
    private MentorshipRequest mentorshipRequest;
    private Mentorship mentorship;

    @BeforeEach
    public void setUp() {
        mentorship = new Mentorship();
        mentorship.setId(1L);
        mentorship.setMentee(new User());
        mentorship.setMentor(new User());

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setDescription("Description");
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());

        acceptMentorshipRequestDto = new AcceptMentorshipRequestDto();
        acceptMentorshipRequestDto.setId(1L);
        acceptMentorshipRequestDto.setRequesterId(1L);
        acceptMentorshipRequestDto.setReceiverId(2L);
        userId = 1L;

        rejectRequestDto = new RejectRequestDto();
        rejectRequestDto.setId(1L);
        rejectRequestDto.setRejectReason("No reason just wish!!!");

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("Request");
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
    }

    @Test
    public void testAcceptRequest() {
        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);

        verify(mentorshipRequestRepository).findById(acceptMentorshipRequestDto.getId());
        verify(mentorshipRequestRepository).save(mentorshipRequest);
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    @DisplayName("test accept request with already accepted request")
    public void testAcceptRequestWithAlreadyAcceptedRequest() {
        when(mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                acceptMentorshipRequestDto.getRequesterId()))
                .thenReturn(Optional.of(mentorship));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.acceptRequest(acceptMentorshipRequestDto);
        });
        assertEquals("You have already accepted request!!", exception.getMessage());
        verify(mentorshipRequestRepository).findById(acceptMentorshipRequestDto.getId());
        verify(mentorshipRepository).getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                acceptMentorshipRequestDto.getRequesterId());
    }

    @Test
    public void testRejectRequest() {
        when(mentorshipRequestRepository.findById(rejectRequestDto.getId()))
                .thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);

        mentorshipRequestService.rejectRequest(rejectRequestDto);

        verify(mentorshipRequestRepository).findById(rejectRequestDto.getId());
        verify(mentorshipRequestRepository).save(mentorshipRequest);

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejectRequestDto.getRejectReason(), mentorshipRequest.getRejectionReason());
    }

    @Test
    public void testRequestMentorship() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);
        when(mentorshipRequestRepository
                .findLatestRequest(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId())).thenReturn(null);
        when(mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription())).thenReturn(mentorshipRequest);
        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        verify(userRepository).existsById(mentorshipRequestDto.getRequesterId());
        verify(userRepository).existsById(mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestRepository).findLatestRequest(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription()));
        assertEquals(mentorshipRequestDto,result);

    }

}
