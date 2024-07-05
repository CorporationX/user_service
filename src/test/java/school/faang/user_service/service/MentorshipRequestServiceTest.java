package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestMentorship_Success() throws Exception {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("I need guidance on my project");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(mentorshipRequestMapper.toEntity(requestDto)).thenReturn(new MentorshipRequest());

        mentorshipRequestService.requestMentorship(requestDto);

        verify(mentorshipRequestRepository).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_UserNotFound() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("I need guidance on my project");



        assertThrows(Exception.class, () -> {
            mentorshipRequestService.requestMentorship(requestDto);
        });

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_SelfRequest() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(1L);
        requestDto.setDescription("I need guidance on my project");

        assertThrows(Exception.class, () -> {
            mentorshipRequestService.requestMentorship(requestDto);
        });

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_ExistingRequest() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("I need guidance on my project");


        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()));

        assertThrows(Exception.class, () -> {
            mentorshipRequestService.requestMentorship(requestDto);
        });

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }
}
