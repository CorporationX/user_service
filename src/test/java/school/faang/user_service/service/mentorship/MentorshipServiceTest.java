package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapperImpl mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees() {
        long userId = 1L;

        User mentee = new User();
        mentee.setId(2L);
        List<User> mentees = List.of(mentee);

        Mockito.when(mentorshipRepository.findMenteesByMentorId(userId))
                .thenReturn(mentees);

        List<MenteeDto> result = mentorshipService.getMentees(userId);

        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void testGetMentors() {
        long userId = 2L;

        User mentor = new User();
        mentor.setId(1L);
        List<User> mentors = List.of(mentor);

        Mockito.when(mentorshipRepository.findMentorsByMenteeId(userId))
                .thenReturn(mentors);

        List<MentorDto> result = mentorshipService.getMentors(userId);

        assertEquals(1L, result.get(0).getId());
    }
}
