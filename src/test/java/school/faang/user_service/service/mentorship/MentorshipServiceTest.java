package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    private User mentor = new User();
    private User mentee = new User();

    private long mentorId;
    private long menteeId;
    private long wrongUserId=3L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mentorId = 1L;
        menteeId = 2L;

        mentor.setId(mentorId);
        mentee.setId(menteeId);

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);

        mentor.setMentees(mentees);
        mentee.setMentors(mentors);

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
    }

    @Test
    void testGetMentees_True() {
        mentor.setMentees(new ArrayList<>());

        mentor.setMentees(List.of(new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any", PreferredContact.EMAIL));

        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
        Mockito.when(userMapper.toDto(mentor.getMentees()))
                .thenReturn(userDtoList);

        List<UserDto> mentees = mentorshipService.getMentees(mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        assertEquals(1, mentees.size());
    }

    @Test
    void testGetMentorsThrowException() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(wrongUserId));
    }

    @Test
    void getMentors() {
        mentee.setMentors(new ArrayList<>());
        mentee.setMentors(List.of(new User(), new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any", PreferredContact.EMAIL));

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(userMapper.toDto(mentee.getMentors()))
                .thenReturn(userDtoList);

        List<UserDto> mentees = mentorshipService.getMentors(menteeId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        assertEquals(1, mentees.size());
    }

    @Test
    void testGetMenteesThrowException() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(wrongUserId));
    }

    @Test
    void testDeleteMentee() {
        mentorshipService.deleteMentee(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentor);
    }

    @Test
    void testDeleteMentor() {
        mentorshipService.deleteMentor(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentee);
    }

    @Test
    void testThrowExceptions_deleteMentee_DeleteMentor() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(menteeId, wrongUserId));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(wrongUserId, mentorId));

        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(menteeId, wrongUserId));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(wrongUserId, mentorId));
    }
}