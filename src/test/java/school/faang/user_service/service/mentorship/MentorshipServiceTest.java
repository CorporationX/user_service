package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipValidator mentorshipValidator;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private UserDto mentorDto;
    private UserDto menteeDto;
    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .id(3L)
                .build();
        mentor = User.builder()
                .id(1L)
                .build();
        mentorDto = UserDto.builder()
                .id(mentor.getId())
                .build();
        mentee = User.builder()
                .id(2L)
                .build();
        menteeDto = UserDto.builder()
                .id(mentee.getId())
                .build();
        goal.setMentor(mentor);
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
        mentee.setGoals(new ArrayList<>(List.of(goal)));
    }

    @Test
    void testGetMentees() {
        when(userService.getUserById(mentor.getId())).thenReturn(mentor);

        assertEquals(List.of(menteeDto), mentorshipService.getMentees(mentor.getId()));
        verify(userService).getUserById(mentor.getId());
        verify(userMapper).toDto(List.of(mentee));
    }

    @Test
    void testGetMentors() {
        when(userService.getUserById(mentee.getId())).thenReturn(mentee);

        assertEquals(List.of(mentorDto), mentorshipService.getMentors(mentee.getId()));
        verify(userService).getUserById(mentee.getId());
        verify(userMapper).toDto(List.of(mentor));
    }

    @Test
    void testDeleteMentee() {
        when(userService.getUserById(mentee.getId())).thenReturn(mentee);
        when(userService.getUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());

        verify(userService, times(2)).getUserById(anyLong());
        verify(mentorshipValidator, times(1)).validateMentorMenteeIds(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).save(mentor);
        assertEquals(Collections.emptyList(), mentor.getMentees());
    }

    @Test
    void testDeleteMentor() {
        when(userService.getUserById(mentee.getId())).thenReturn(mentee);
        when(userService.getUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(userService, times(2)).getUserById(anyLong());
        verify(mentorshipValidator, times(1)).validateMentorMenteeIds(mentee.getId(), mentor.getId());
        verify(userRepository, times(1)).save(mentee);
        assertEquals(Collections.emptyList(), mentee.getMentors());
    }

    @Test
    void deleteMentorForAllHisMentees_MentorDeletedMenteesUpdated_ThenMenteesSavedToDb() {
        mentorshipService.deleteMentorForAllHisMentees(mentor.getId(), List.of(mentee));

        assertAll(
                () -> verify(mentorshipRepository, times(1)).saveAll(List.of(mentee)),
                () -> assertEquals(Collections.emptyList(), mentee.getMentors()),
                () -> assertEquals(mentee.getGoals().get(0).getMentor(), mentee)
        );
    }
}
