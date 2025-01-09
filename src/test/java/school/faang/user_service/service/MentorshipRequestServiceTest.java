package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.MentorshipRequestService;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipMapperImpl mentorshipMapper;
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequest mentorshipRequest;

    private static final Long REQUESTER_ID = 1L;
    private static final Long REQUESTER_ID_FAIL = 23L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long RECEIVER_ID_FAIL = 24L;
    private static final String DESCRIPTION = "I want you to be my mentor";
    private static final RequestStatus REQUEST_STATUS = RequestStatus.PENDING;
    private MentorshipRequest lastRequestForMentorship;

    @Test
    public void testDtoIsNull() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        Assert.assertThrows(
                NullPointerException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testRequesterIdIsNotExist() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID_FAIL,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testReceiverIdIsNotExist() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID_FAIL,
                DESCRIPTION,
                REQUEST_STATUS);
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(false);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckIfDifferentUsers() {
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                REQUESTER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCheckLastRequest() {
        lastRequestForMentorship = new MentorshipRequest();
        lastRequestForMentorship.setUpdatedAt(LocalDateTime.now().minusDays(4));
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId()))
                .thenReturn(Optional.of(lastRequestForMentorship));
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    public void testCreateMentorshipRequest() {
        lastRequestForMentorship = new MentorshipRequest();
        lastRequestForMentorship.setUpdatedAt(LocalDateTime.now().minusMonths(6));
        MentorshipRequestDto mentorshipRequestDto = prepareDataToDto(
                REQUESTER_ID,
                RECEIVER_ID,
                DESCRIPTION,
                REQUEST_STATUS);
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId()))
                .thenReturn(Optional.of(lastRequestForMentorship));

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());
    }

    private MentorshipRequestDto prepareDataToDto(Long requesterId, Long receiverId, String description, RequestStatus requestStatus) {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requesterId);
        mentorshipRequestDto.setReceiverId(receiverId);
        mentorshipRequestDto.setDescription(description);
        mentorshipRequestDto.setStatus(requestStatus);
        return mentorshipRequestDto;
    }
}
