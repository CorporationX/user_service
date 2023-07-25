package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMentees_True() {
        User mentor = new User();
        long mentorId = 1L;

        mentor.setMentees(new ArrayList<>());
        mentor.setId(mentorId);

        mentor.setMentees(List.of(new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any"));

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
        User mentor = new User();
        long mentorId = 1L;

        mentor.setId(mentorId);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(2L));
    }

    @Test
    void getMentors() {
        User mentee = new User();
        long menteeId = 1L;

        mentee.setMentors(new ArrayList<>());
        mentee.setId(menteeId);
        mentee.setMentors(List.of(new User(), new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any"));

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
        User mentee = new User();
        long menteeId = 1L;

        mentee.setId(menteeId);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(2L));
    }
}