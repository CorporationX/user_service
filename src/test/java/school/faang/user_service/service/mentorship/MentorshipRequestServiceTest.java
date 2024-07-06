package school.faang.user_service.service.mentorship;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.junit.jupiter.api.Assertions.*;

public class MentorshipRequestServiceTest {

    @Mock
    MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    MentorshipRequestService mentorshipRequestService;

    @Test
    public void testCorrectRequestMentorship() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(5);
        mentorshipRequestDto.setReceiverId(7);
        mentorshipRequestDto.setDescription("Text request description");
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        MentorshipRequest mentorshipRequestResult = mentorshipRequestService.requestMentorship(mentorshipRequest);
        System.out.println(mentorshipRequestResult);
        assertEquals(0, mentorshipRequestResult.getId());
    }
}