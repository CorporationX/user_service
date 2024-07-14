package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MentorshipNoSuchElementException;
import school.faang.user_service.mapper.mentorship.MentorshipMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.impl.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipServiceImpl mentorshipService;
    @Spy
    private MentorshipMapperImpl mentorshipMapper;
    private User user;
    private User userMentee;
    private User userMentor;
    private MentorshipUserDto userDtoMentee;
    private MentorshipUserDto userDtoMentor;
    private final List<MentorshipUserDto> mentees = new ArrayList<>();
    private final List<MentorshipUserDto> mentors = new ArrayList<>();
    private static final long MENTEE_ID = 1;
    private static final long MENTOR_ID = 2;
    private static final long USER_ID = 3;

    @BeforeEach
    public void init() {
        String menteeUsername = "Messi";
        String mentorUsername = "Ronaldo";

        userMentee = User.builder()
                .id(MENTEE_ID)
                .username(menteeUsername)
                .build();
        userMentor = User.builder()
                .id(MENTOR_ID)
                .username(mentorUsername)
                .build();

        List<User> userMentees = new ArrayList<>();
        userMentees.add(userMentee);
        List<User> userMentors = new ArrayList<>();
        userMentors.add(userMentor);

        user = User.builder().id(USER_ID).mentees(userMentees).mentors(userMentors).build();
        userMentee.setMentors(new ArrayList<>(Collections.singleton(user)));
        userMentor.setMentees(new ArrayList<>(Collections.singletonList(user)));

        userDtoMentee = new MentorshipUserDto(MENTEE_ID, menteeUsername);
        userDtoMentor = new MentorshipUserDto(MENTOR_ID, mentorUsername);

        mentees.add(userDtoMentee);
        mentors.add(userDtoMentor);
    }

    @Test
    public void testGetMenteesSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<MentorshipUserDto> userMentees = mentorshipService.getMentees(USER_ID);
        assertEquals(mentees.size(), userMentees.size());
        assertTrue(userMentees.containsAll(mentees));
        Mockito.verify(mentorshipRepository).findById(USER_ID);
    }

    @Test
    public void testGetMenteesFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(MentorshipNoSuchElementException.class,
                () -> mentorshipService.getMentees(anyLong()));
    }

    @Test
    public void testGetMentorsFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(MentorshipNoSuchElementException.class,
                () -> mentorshipService.getMentors(anyLong()));
    }

    @Test
    public void testGetMentorsSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        List<MentorshipUserDto> userMentors = mentorshipService.getMentors(USER_ID);
        assertEquals(mentors.size(), userMentors.size());
        assertTrue(userMentors.containsAll(mentors));
        Mockito.verify(mentorshipRepository).findById(USER_ID);
    }

    @Test
    public void testDeleteMentorshipRelationsSuccessful() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(userMentee));
        mentorshipService.deleteMentorshipRelations(USER_ID, MENTEE_ID);
        verify(mentorshipRepository).findById(USER_ID);
        verify(mentorshipRepository).findById(MENTEE_ID);
        assertFalse(user.getMentees().contains(userMentee));
        verify(mentorshipRepository).save(user);
    }

    @Test
    public void testDeleteMentorshipRelationsFailure() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(mentorshipRepository.findById(MENTOR_ID)).thenReturn(Optional.of(userMentee));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentorshipRelations(MENTOR_ID, anyLong()));
    }
}