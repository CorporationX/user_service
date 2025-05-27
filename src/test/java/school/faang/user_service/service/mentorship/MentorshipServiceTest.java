package school.faang.user_service.service.mentorship;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.GetMenteesResponse;
import school.faang.user_service.dto.mentorship.GetMentorsResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapperImpl;
import school.faang.user_service.mapper.mentorship.MentorsMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MenteeMapperImpl menteeMapperImpl;

    @Spy
    private MentorsMapperImpl mentorsMapperImpl;

    @InjectMocks
    private MentorshipService mentorshipService;


    private User createUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    public void testGetMentees() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentees = new ArrayList<>();
        mentees.add(jon);
        miras.setMentees(mentees);
        List<GetMenteesResponse> menteeList = mentees.stream()
                .map(user -> menteeMapperImpl.toDto(user)).toList();

        when(mentorshipRepository.findById(miras.getId()))
                .thenReturn(Optional.of(miras));

        mentorshipService.getMentees(miras.getId());

        assertNotNull(mentorshipService.getMentees(miras.getId()));
        assertEquals(menteeList, mentorshipService.getMentees(miras.getId()));
    }

    @Test
    public void testGetMenteesNotFound() {
        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentees(1L)
        );
    }

    @Test
    public void testGetMentors() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentors = new ArrayList<>();
        mentors.add(miras);
        jon.setMentors(mentors);
        List<GetMentorsResponse> mentorList = mentors.stream()
                .map(user -> mentorsMapperImpl.toDto(user)).toList();

        when(mentorshipRepository.findById(jon.getId()))
                .thenReturn(Optional.of(jon));

        mentorshipService.getMentors(jon.getId());

        assertNotNull(mentorshipService.getMentors(jon.getId()));
        assertEquals(mentorList, mentorshipService.getMentors(jon.getId()));
    }

    @Test
    public void testGetMentorsNotFound() {
        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentors(1L)
        );
    }

    @Test
    public void testDeleteMentee() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentees = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        mentees.add(jon);
        mentors.add(miras);
        miras.setMentees(mentees);
        jon.setMentors(mentors);

        when(mentorshipRepository.findById(miras.getId()))
                .thenReturn(Optional.of(miras));
        when(mentorshipRepository.findById(jon.getId()))
                .thenReturn(Optional.of(jon));

        mentorshipService.deleteMentee(2L, 1L);

        verify(mentorshipRepository, times(1)).save(jon);
    }
}