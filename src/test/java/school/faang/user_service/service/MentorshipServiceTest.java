package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;
    private User user1;
    private User user2;
    private User user3;
    private UserDto userDto;
    private List<User> users;
    private List<User> users3;
    private List<UserDto> users2;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder().id(1L).build();
        user1 = User.builder().id(1L).build();
        users2 = List.of(userDto);
        users3 = List.of(user1);
        user2 = User.builder().id(2L).mentees(users3).build();
        users = List.of(user1);
        user3 = User.builder().id(3L).mentors(users).build();
    }

    @Test
    void testGetMentees_ShouldThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentees(1L));
    }

    @Test
    void testGetMentees_ShouldReturnsListOfMenteesDto() {
        when(mentorshipRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        assertEquals(users2, mentorshipService.getMentees(2L));
    }

    @Test
    void testGetMentors_ShouldThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(1L));
    }

    @Test
    void testGetMentors_ShouldReturnsListOfMentorsDto() {
        when(mentorshipRepository.findById(3L))
                .thenReturn(Optional.of(user3));
        assertEquals(users2, mentorshipService.getMentors(3L));
    }

    @Test
    void testRemoveMentorsMenteeIds_ShouldThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.removeMentorsMentee(1L, 2L));
    }

    @Test
    void testRemoveMentorOfMentee_ShouldThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.removeMentorOfMentee(1L, 2L));
    }
}