package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;



@SpringBootTest
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private  MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private  MentorshipRepository mentorshipRepository;
    @Mock
    private  UserRepository userRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;

    private AcceptMentorshipRequestDto acceptMentorshipRequestDto;
    private MentorshipRequestDto mentorshipRequestDto;
    private Long userId;
    @BeforeEach
    public void setUp() {
        userId = 1L;
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("Request");
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
    }
    @Test
    public void testMentorshipRequest(){
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

}
