package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private User mentee1;
    private User mentee2;
    private User mentor1;
    private User mentor2;
    private User mentee3;
    private User mentor3;


    @BeforeEach
    void beforeEach() {
        mentee1 = getMentee1();
        mentee2 = getMentee2();

        mentor1 = getMentor1();
        mentor2 = getMentor2();

        mentee3 = getMentee3();
        mentor3 = getMentor3();
    }

    @Test
    void testGetMentorsException() {
        when(mentorshipRepository.findById(mentor2.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentor2.getId()));
    }

    @Test
    void testGetMentorsList() {
        when(mentorshipRepository.findById(mentor2.getId())).thenReturn(Optional.of(mentor2));

        mentorshipService.getMentors(mentor2.getId());

        verify(mentorshipRepository).findById(mentor2.getId());
        verify(userMapper).toDto(mentorshipRepository.findById(mentor2.getId()).get().getMentees());
    }

    @Test
    void testGetMenteesException() {
        when(mentorshipRepository.findById(mentee1.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentee1.getId()));
    }

    @Test
    void testGetMenteesList() {
        when(mentorshipRepository.findById(mentee1.getId())).thenReturn(Optional.of(mentee1));

        mentorshipService.getMentees(mentee1.getId());

        verify(mentorshipRepository).findById(mentee1.getId());
        verify(userMapper).toDto(mentorshipRepository.findById(mentee1.getId()).get().getMentees());
    }


    @Test
    public void testDeleteMentee() {
        when(mentorshipRepository.findById(mentor1.getId())).thenReturn(Optional.of(mentor1));
        when(mentorshipRepository.findById(mentee2.getId())).thenReturn(Optional.of(mentee2));

        mentorshipService.deleteMentee(mentee2.getId(), mentor1.getId());

        verify(mentorshipRepository, times(1)).save(mentor1);
    }

    @Test
    public void testDeleteMentor() {
        when(mentorshipRepository.findById(mentee3.getId())).thenReturn(Optional.of(mentee3));
        when(mentorshipRepository.findById(mentor2.getId())).thenReturn(Optional.of(mentor2));

        mentorshipService.deleteMentor(mentee3.getId(), mentor2.getId());

        verify(mentorshipRepository, times(1)).save(mentee3);
    }

    @Test
    public void testDeleteEmptyMentee() {
        when(mentorshipRepository.findById(mentor2.getId())).thenReturn(Optional.of(mentor2));
        when(mentorshipRepository.findById(mentee2.getId())).thenReturn(Optional.of(mentee2));
        mentor2.setMentees(new ArrayList<>(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(mentee2.getId(), mentor2.getId()));
    }

    @Test
    public void testDeleteEmptyMentor() {
        when(mentorshipRepository.findById(mentee1.getId())).thenReturn(Optional.of(mentee1));
        when(mentorshipRepository.findById(mentor1.getId())).thenReturn(Optional.of(mentor1));
        mentee1.setMentors(new ArrayList<>(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(mentee1.getId(), mentor1.getId()));
    }

    @Test
    public void testDeleteMenteeThatNotOnTheList() {
        when(mentorshipRepository.findById(mentor1.getId())).thenReturn(Optional.of(mentor1));
        when(mentorshipRepository.findById(mentee3.getId())).thenReturn(Optional.of(mentee3));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(mentee3.getId(), mentor1.getId()));
    }

    @Test
    public void testDeleteMentorThatNotOnTheList() {
        when(mentorshipRepository.findById(mentee3.getId())).thenReturn(Optional.of(mentee3));
        when(mentorshipRepository.findById(mentor3.getId())).thenReturn(Optional.of(mentor3));

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(mentee3.getId(), mentor3.getId()));
    }

    @Test
    public void testDeleteMenteeNotValidIdMentee() {
        when(mentorshipRepository.findById(mentor1.getId())).thenReturn(Optional.of(mentor1));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(-1L, mentor1.getId()));
    }

    @Test
    public void testDeleteMenteeNotValidIdMentor() {
        when(mentorshipRepository.findById(mentor1.getId())).thenReturn(Optional.of(mentor1));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(-1L, mentor1.getId()));
    }

    @Test
    public void testDeleteMentorNotValidIdMentor() {
        when(mentorshipRepository.findById(mentee3.getId())).thenReturn(Optional.of(mentee3));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(mentee3.getId(), -1L));
    }

    @Test
    public void testDeleteMentorNotValidIdMentee() {
        when(mentorshipRepository.findById(mentee3.getId())).thenReturn(Optional.of(mentee3));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(mentee3.getId(), -1L));
    }

    private static User getMentee1() {
        return User.builder()
                .id(101L)
                .build();
    }

    private static User getMentee2() {
        return User.builder()
                .id(102L)
                .build();
    }

    private User getMentor1() {
        return User.builder()
                .id(201L)
                .mentees(new ArrayList<>(List.of(mentee2, mentee1)))
                .build();
    }

    private static User getMentor2() {
        return User.builder()
                .id(202L)
                .build();
    }

    private static User getMentor3() {
        return User.builder()
                .id(103L)
                .build();
    }

    private User getMentee3() {
        return User.builder()
                .id(203L)
                .mentors(new ArrayList<>(List.of(mentor2, mentor1)))
                .build();
    }
}
