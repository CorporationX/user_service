package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.GetMenteesResponse;
import school.faang.user_service.dto.mentorship.GetMentorsResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorsMapper;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Spy
    MenteeMapper menteeMapper;

    @Spy
    MentorsMapper mentorsMapper;

    @Mock
    MentorshipService mentorshipService;

    @InjectMocks
    MentorshipController mentorshipController;

    private User createUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    public void testGetMentees() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentee.setMentors(List.of(mentor));

        List<GetMenteesResponse> mentees = mentee.getMentors()
                .stream()
                .map(user -> menteeMapper.toDto(user))
                .toList();

        when(mentorshipService.getMentees(mentor.getId()))
                .thenReturn(mentees);

        List<GetMenteesResponse> result = mentorshipController.getMentees(mentor.getId());

        assertEquals(mentees, result);
    }


    @Test
    public void testGetMentors() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentor.setMentees(List.of(mentee));


        List<GetMentorsResponse> mentors = mentor.getMentees()
                .stream()
                .map(user -> mentorsMapper.toDto(user))
                .toList();

        when(mentorshipService.getMentors(mentee.getId()))
                .thenReturn(mentors);

        List<GetMentorsResponse> result = mentorshipController.getMentors(mentee.getId());

        assertEquals(mentors, result);
    }


    @Test
    public void testDeleteMentee() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));

        mentorshipController.deleteMentee(mentee.getId(), mentor.getId());

        verify(mentorshipService, times(1)).deleteMentee(mentee.getId(), mentor.getId());

    }
}
