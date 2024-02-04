package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    private MentorshipValidator mentorshipValidator;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private final MentorshipValidator mentorshipValidator = new MentorshipValidator(mentorshipRepository);
    @InjectMocks
    private MentorshipController mentorshipController;

    private List<UserDto> userDtos;
    private UserDto userDto;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder().id(1L).build();
        userDtos = List.of(userDto);
        user = User.builder().id(1L).build();
        user2 = User.builder().id(2L).build();
    }

    @Test
    void testGetMentees_ShouldCallServiceMethod() {
        when(mentorshipService.getMentees(user.getId())).thenReturn(userDtos);
        mentorshipController.getMentees(user.getId());
        verify(mentorshipService, times(1)).getMentees(user.getId());
    }

    @Test
    void testGetMentors_ShouldCallServiceMethod() {
        when(mentorshipService.getMentors(user.getId())).thenReturn(userDtos);
        mentorshipController.getMentors(user.getId());
        verify(mentorshipService, times(1)).getMentors(user.getId());
    }

    @Test
    void testRemoveMentorsMentee_ShouldCallServiceMethod() {
        mentorshipController.removeMentorsMentee(user.getId(), user2.getId());
        verify(mentorshipService, times(1)).removeMenteeOfMentor(user.getId(), user2.getId());
    }

    @Test
    void testRemoveMentorOfMentee_ShouldCallServiceMethod() {
        mentorshipController.removeMentorOfMentee(user.getId(), user2.getId());
        verify(mentorshipService, times(1)).removeMentorOfMentee(user.getId(), user2.getId());
    }
}
