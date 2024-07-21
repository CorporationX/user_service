package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;
    private User mentorMax;
    private User mentorDima;
    private User menteeAlex;
    private User menteeMisha;

    @BeforeEach
    void setUp() {
        mentorMax = new User();
        mentorDima = new User();
        menteeAlex = new User();
        menteeMisha = new User();
        mentorMax.setId(1L);
        mentorDima.setId(4L);
        menteeAlex.setId(2L);
        menteeMisha.setId(3L);
        List<User> listMentees = new ArrayList<>(List.of(menteeAlex, menteeMisha));
        mentorMax.setMentees(listMentees);
        mentorDima.setMentees(new ArrayList<>());
        List<User> listMentors = new ArrayList<>(List.of(mentorMax, mentorDima));
        menteeAlex.setMentors(listMentors);
        menteeMisha.setMentors(new ArrayList<>());
    }

    @Test
    public void testGetListOfMentees() {
        when(mentorshipRepository.findById(mentorMax.getId())).thenReturn(of(mentorMax));
        List<UserDto> listMenteesDto = mentorMax.getMentees().stream()
                .map(u -> userMapper.toDto(u)).toList();

        List<UserDto> listMenteesActual = mentorshipService.getMentees(mentorMax.getId());
        assertEquals(listMenteesDto, listMenteesActual);
    }

    @Test
    public void testGetEmptyListIfNotMentees() {
        when(mentorshipRepository.findById(mentorDima.getId())).thenReturn(of(mentorDima));
        List<UserDto> listMenteesDto = mentorDima.getMentees().stream()
                .map(u -> userMapper.toDto(u))
                .toList();

        List<UserDto> listMenteesActual = mentorshipService.getMentees(mentorDima.getId());
        assertEquals(listMenteesDto, listMenteesActual);
    }

    @Test
    public void testGetListMentors() {
        when(mentorshipRepository.findById(menteeAlex.getId())).thenReturn(of(menteeAlex));
        List<UserDto> listMentorsDto = menteeAlex.getMentors().stream().map(u -> userMapper.toDto(u)).toList();

        List<UserDto> listMentorsActual = mentorshipService.getMentors(menteeAlex.getId());
        assertEquals(listMentorsDto, listMentorsActual);
    }

    @Test
    public void testGetEmptyListIfNotMentors() {
        when(mentorshipRepository.findById(menteeMisha.getId())).thenReturn(of(menteeMisha));
        List<UserDto> listMentorsDto = menteeMisha.getMentors().stream()
                .map(u -> userMapper.toDto(u))
                .toList();

        List<UserDto> listMentorsActual = mentorshipService.getMentors(menteeMisha.getId());
        assertEquals(listMentorsDto, listMentorsActual);
    }

    @Test
    public void testDeleteMenteeIfNotMentorId() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.deleteMentee(menteeAlex.getId(), mentorDima.getId()));
    }

    @Test
    public void testDeleteMentee() {
        when(mentorshipRepository.findById(mentorMax.getId())).thenReturn(of(mentorMax));
        mentorshipService.deleteMentee(menteeAlex.getId(), mentorMax.getId());

        verify(mentorshipRepository, times(1)).save(mentorMax);
    }

    @Test
    public void testDeleteMentorIfNotMenteeId() {
        assertThrows(RuntimeException.class,
                () -> mentorshipService.deleteMentor(menteeAlex.getId(), mentorDima.getId()));
    }

    @Test
    public void testDeleteMentor() {
        when(mentorshipRepository.findById(menteeAlex.getId())).thenReturn(of(menteeAlex));
        mentorshipService.deleteMentor(menteeAlex.getId(), mentorMax.getId());

        verify(mentorshipRepository, times(1)).save(menteeAlex);
    }
}