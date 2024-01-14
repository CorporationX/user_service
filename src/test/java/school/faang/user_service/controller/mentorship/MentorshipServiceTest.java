package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();
    @InjectMocks
    private MentorshipService mentorshipService;
    private User user;
    private User userMentee;
    private User userMentor;
    private UserDTO userDTO;
    private List<UserDTO> listOfMentee = new ArrayList<>();
    private List<UserDTO> listOfMentors = new ArrayList<>();

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

        userDTO = UserDTO.builder()
                .id(100)
                .username("Vasiliy")
                .build();

        listOfMentee.add(userDTO);
        listOfMentors.add(userDTO);
    }

    @Test
    public void testMentorshipService_GoodId() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(listOfMentee.get(0), mentorshipService.getMenteesOfUser(1).get(0));
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    public void testMentorshipService_getMentorsOfUser(){
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(listOfMentors.get(0), mentorshipService.getMentorsOfUser(1).get(0));
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    void whenEntityNotFoundException() {
        Throwable exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    throw new EntityNotFoundException("Mentor with id not found");
                }
        );
        assertEquals("Mentor with id not found", exception.getMessage());
    }

}