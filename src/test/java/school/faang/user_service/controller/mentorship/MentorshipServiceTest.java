package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void init() {
        userMentee = User.builder()
                .id(100)
                .username("Vasiliy")
                .build();
        List<User> menteesOfUser = new ArrayList<>();
        menteesOfUser.add(userMentee);

        userMentor = User.builder()
                .id(10)
                .username("Alex")
                .build();
        List<User> mentorsOfUser = new ArrayList<>();
        mentorsOfUser.add(userMentor);

        user = User.builder()
                .id(1)
                .mentees(menteesOfUser)
                .mentors(mentorsOfUser)
                .build();

        userDtoMentee = UserDto.builder()
                .id(100)
                .username("Vasiliy")
                .build();

        userDtoMentor = UserDto.builder()
                .id(10)
                .username("Alex")
                .build();

        mentees.add(userDtoMentee);
        mentors.add(userDtoMentor);
    }

    @Test
    public void testGetMenteesSuccessful() {
        when(userMapper.toUserDto(userMentee)).thenReturn(userDtoMentee);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(mentees.size(), mentorshipService.getMentees(1).size());
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    public void testGetMentorsSuccessful() {
        when(userMapper.toUserDto(userMentor)).thenReturn(userDtoMentor);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(mentors.size(), mentorshipService.getMentors(1).size());
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
    public void testDeleteMentorSuccessful(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(10L)).thenReturn(Optional.of(userMentor));
        mentorshipService.deleteMentor(1, 10);
        verify(userRepository).findById(1L);
        verify(userRepository).findById(10L);
        assertFalse(user.getMentors().contains(userMentor));
        verify(userRepository).save(user);
    }

    @Test
    public void testGetUserByIdShouldException() {
        long id = -1;
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getUser(id));
    }

    @Test
    void testIllegalArgumentException() {
        Throwable exception = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    throw new IllegalArgumentException("User with id not found");
                }
        );
        assertEquals("User with id not found", exception.getMessage());
    }
}