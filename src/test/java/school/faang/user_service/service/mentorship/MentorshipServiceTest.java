package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipValidator mentorshipValidator;

    @Mock
    private UserMapper userMapper;

    private User mentor;
    private User mentee;
    private List<UserDto> menteesDto;
    private List<UserDto> mentorsDto;
    private Optional<User> mentorOptional;
    private Optional<User> menteeOptional;

    @BeforeEach
    public void setup() {
        mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());

        mentee = new User();
        mentee.setId(2L);
        mentee.setMentors(new ArrayList<>());

        mentorsDto = new ArrayList<>();
        menteesDto = new ArrayList<>();

        mentorOptional = Optional.of(mentor);
        menteeOptional = Optional.of(mentee);

        reset(mentorshipRepository, mentorshipValidator, userMapper);
    }

    @Test
    @DisplayName("testGetMentees")
    public void testGetMentees() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(mentorOptional);
        when(userMapper.toDtoList(mentor.getMentees())).thenReturn(menteesDto);

        List<UserDto> result = mentorshipService.getMentees(mentor.getId());

        verify(mentorshipRepository).findById(mentor.getId());
        assertNotNull(result);
        assertEquals(menteesDto, result);
    }

    @Test
    @DisplayName("testGetMentors")
    public void testGetMentors() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(menteeOptional);
        when(userMapper.toDtoList(mentee.getMentors())).thenReturn(mentorsDto);

        List<UserDto> result = mentorshipService.getMentors(mentee.getId());

        verify(mentorshipRepository).findById(mentee.getId());
        assertNotNull(result);
        assertEquals(mentorsDto, result);
    }

    @Test
    @DisplayName("testDeleteMentee")
    public void testDeleteMentee() {
        mentor.getMentees().add(mentee);
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(mentorOptional);

        mentorshipService.deleteMentee(mentee.getId(), mentor.getId());

        verify(mentorshipRepository).findById(mentor.getId());
        verify(mentorshipValidator).validateMenteeAndMentorIds(mentee.getId(), mentor.getId());
        assertFalse(mentor.getMentees().contains(mentee));
    }

    @Test
    @DisplayName("testDeleteMentor")
    public void testDeleteMentor() {
        mentee.getMentors().add(mentee);
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(menteeOptional);

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(mentorshipRepository).findById(mentee.getId());
        verify(mentorshipValidator).validateMenteeAndMentorIds(mentee.getId(), mentor.getId());
        assertFalse(mentee.getMentors().contains(mentor));
    }

    @Test
    @DisplayName("testGetMenteesMentorNotFound")
    public void testGetMenteesMentorNotFound() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentor.getId()));
    }

    @Test
    @DisplayName("testGetMentorsMenteeNotFound")
    public void testGetMentorsMenteeNotFound() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentee.getId()));
    }

    @Test
    @DisplayName("testDeleteMenteeMentorNotFound")
    public void testDeleteMenteeMentorNotFound() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId()));
    }

    @Test
    @DisplayName("testDeleteMentorMenteeNotFound")
    public void testDeleteMentorMenteeNotFound() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId()));
    }
}
