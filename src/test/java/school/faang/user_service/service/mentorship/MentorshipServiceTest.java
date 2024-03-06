package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private MentorshipService mentorshipService;
    private User user;
    private User userMentee;
    private User userMentor;
    private UserDto userDtoMentee;
    private UserDto userDtoMentor;
    private final List<UserDto> mentees = new ArrayList<>();
    private final List<UserDto> mentors = new ArrayList<>();
    long invalidId;

    @BeforeEach
    public void init() {
        userMentee = User.builder()
                .id(100L)
                .username("Vasiliy")
                .build();
        List<User> userMentees = new ArrayList<>();
        userMentees.add(userMentee);

        userMentor = User.builder()
                .id(10L)
                .username("Alex")
                .build();
        List<User> userMentors = new ArrayList<>();
        userMentors.add(userMentor);

        user = User.builder()
                .id(1L)
                .mentees(userMentees)
                .mentors(userMentors)
                .build();

        userDtoMentee = UserDto.builder()
                .id(100L)
                .username("Vasiliy")
                .build();

        userDtoMentor = UserDto.builder()
                .id(10L)
                .username("Alex")
                .build();

        mentees.add(userDtoMentee);
        mentors.add(userDtoMentor);
        invalidId = -1;
    }

    @Test
    public void testGetMenteesSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<UserDto> userMentees = mentorshipService.getMentees(1);
        assertEquals(mentees.size(), userMentees.size());
        assertTrue(userMentees.containsAll(mentees));
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    public void testGetMentorsSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<UserDto> userMentors = mentorshipService.getMentors(1);
        assertEquals(mentors.size(), userMentors.size());
        assertTrue(userMentors.containsAll(mentors));
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    public void testDeleteMenteeSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(100L)).thenReturn(Optional.of(userMentee));
        mentorshipService.deleteMentee(100, 1);
        verify(userRepository).findById(1L);
        verify(userRepository).findById(100L);
        assertFalse(user.getMentees().contains(userMentee));
        verify(userRepository).save(user);
    }

    @Test
    public void testDeleteMentorSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(10L)).thenReturn(Optional.of(userMentor));
        mentorshipService.deleteMentor(1, 10);
        verify(userRepository).findById(1L);
        verify(userRepository).findById(10L);
        assertFalse(user.getMentors().contains(userMentor));
        verify(userRepository).save(user);
    }

    @Test
    public void testGetMenteesFailure() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentees(anyLong()));
    }

    @Test
    public void testGetMentorsFailure() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(anyLong()));
    }

    @Test
    public void testDeleteMenteeFailure() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(100L)).thenReturn(Optional.of(userMentee));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentee(100, anyLong()));
    }


    @Test
    public void testDeleteMentorFailure() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(userMentor));
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(10, anyLong()));
    }
}