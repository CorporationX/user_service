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
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
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
}
