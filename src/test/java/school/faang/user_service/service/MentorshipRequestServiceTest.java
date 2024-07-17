package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

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
    @Test
    public void testRequestMentorshipRequesterExists() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(100L);
        dto.setDescription("123");
        when(mentorshipRepository.existsById(dto.getRequesterId())).thenReturn(false);
        assertThrows(DataValidationException.class,()->mentorshipRequestService.requestMentorship(dto));
    }
    public void testRequestMentorshipReceiverExists() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(1000L);
        dto.setDescription("123");
        when(mentorshipRepository.existsById(dto.getRequesterId())).thenReturn(false);
        assertThrows(DataValidationException.class,()->mentorshipRequestService.requestMentorship(dto));
    }

}
