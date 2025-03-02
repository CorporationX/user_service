package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.impl.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceImplTest {

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapper mentorshipMapper = new MentorshipMapperImpl();

    @Test
    public void test_getMentees_returnMentees_whenUserHasMentees() {
        User mentor = new User();
        String mentorName = "Ivan";
        mentor.setId(1L);
        mentor.setUsername(mentorName);

        User mentee = new User();
        String menteeName = "Oleg";
        mentee.setId(2L);
        mentee.setUsername(menteeName);

        mentor.setMentees(List.of(mentee));

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(mentor));

        final List<UserDto> mentees = mentorshipService.getMentees(1L);

        assertEquals(1, mentees.size());
        assertEquals("Oleg", mentees.get(0).getUsername());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipMapper, Mockito.times(1)).toUserDto(List.of(mentee));
    }

    @Test
    public void test_getMentees_returnEmptyList_whenUserHasNoMentees() {
        User user = new User();
        user.setId(1L);
        user.setMentees(Collections.emptyList());

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(user));

        final List<UserDto> mentees = mentorshipService.getMentees(1L);

        assertTrue(mentees.isEmpty());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipMapper, Mockito.times(1)).toUserDto(Mockito.anyList());
    }

    @Test
    public void test_getMentees_throwsException_whenUserNotFound() {

        Mockito.when(mentorshipRepository.findById(Mockito.eq(3L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(3L));
    }


    @Test
    public void test_getMentees_throwsException_whenIdIsZeroOrNegative() {
        long invalidId1 = 0L;
        long invalidId2 = -1L;

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(invalidId1));
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(invalidId2));

        Mockito.verify(mentorshipRepository, Mockito.times(2)).findById(Mockito.anyLong());
    }

    @Test
    public void test_getMentees_correctMapping_toUserDto() {
        User mentor = new User();
        String mentorName = "Ivan";
        mentor.setId(1L);
        mentor.setUsername(mentorName);

        User mentee = new User();
        String menteeName = "Kirill";
        mentee.setId(2L);
        mentee.setUsername(menteeName);
        mentor.setMentees(List.of(mentee));

        UserDto menteeDto = new UserDto();
        String menteeDtoName = "Kirill";
        menteeDto.setId(2L);
        menteeDto.setUsername(menteeDtoName);

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(mentor));
        Mockito.when(mentorshipMapper.toUserDto(Mockito.anyList())).thenReturn(List.of(menteeDto));

        List<UserDto> mentees = mentorshipService.getMentees(1L);

        assertFalse(mentees.isEmpty());
        assertEquals(1, mentees.size());
        assertEquals(menteeName, mentees.get(0).getUsername());

        Mockito.verify(mentorshipMapper, Mockito.times(1)).toUserDto(Mockito.anyList());
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(Mockito.eq(1L));
    }

    @Test
    public void test_getMentors_returnListOfUserDto_whenUserExistsWithMentors() {
        User mentee = new User();
        String menteeName = "Ivan";
        mentee.setId(1L);
        mentee.setUsername(menteeName);

        User mentor = new User();
        String mentorName = "Oleg";
        mentor.setId(2L);
        mentor.setUsername(mentorName);

        mentee.setMentors(List.of(mentor));

        UserDto mentorDto = new UserDto();
        String mentorDtoName = "Oleg";
        mentorDto.setUsername(mentorDtoName);

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipMapper.toUserDto(List.of(mentor))).thenReturn(List.of(mentorDto));

        final List<UserDto> mentors = mentorshipService.getMentors(1L);

        assertEquals(1, mentors.size());
        assertEquals(mentorName, mentors.get(0).getUsername());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipMapper, Mockito.times(2)).toUserDto(List.of(mentor));
    }

    @Test
    public void test_getMentors_ReturnsEmptyList_whenUserHasNoMentors() {
        User user = new User();
        user.setId(1L);
        user.setMentors(Collections.emptyList());

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(user));

        final List<UserDto> mentors = mentorshipService.getMentors(1L);

        assertTrue(mentors.isEmpty());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipMapper, Mockito.times(1)).toUserDto(Collections.emptyList());
    }

    @Test
    public void test_getMentors_ThrowsEntityNotFoundException_whenUserNotFound() {
        Mockito.when(mentorshipRepository.findById(Mockito.eq(3L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(3L));

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(mentorshipMapper, Mockito.never()).toUserDto(Mockito.anyList());
    }

    @Test
    public void test_deleteMentee_ThrowsEntityNotFoundException_whenMenteeNotFound() {
        User mentor = new User();
        mentor.setId(3L);
        mentor.setMentees(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(Mockito.eq(3L))).thenReturn(Optional.of(mentor));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(1L, 3L));

        assertEquals("Менти с ID 1 не найден у ментора с ID 3", exception.getMessage());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void test_deleteMentee_ThrowsEntityNotFoundException_whenMentorNotFound() {
        Mockito.when(mentorshipRepository.findById(Mockito.eq(3L))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(1L, 3L));

        assertEquals("Ментор с ID 3 не найден", exception.getMessage());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void test_deleteMentee_whenMenteeFound() {
        User mentor = new User();
        mentor.setId(3L);
        mentor.setMentees(new ArrayList<>());

        User mentee = new User();
        mentee.setId(1L);
        mentor.getMentees().add(mentee);

        Mockito.when(mentorshipRepository.findById(Mockito.eq(3L))).thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(1L, 3L);

        assertTrue(mentor.getMentees().isEmpty());
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentor);
    }

    @Test
    public void test_deleteMentor_ThrowsEntityNotFoundException_whenMenteeNotFound() {
        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(1L, 3L));

        assertEquals("Менти с ID 1 не найден", exception.getMessage());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void test_deleteMentor_ThrowsEntityNotFoundException_whenMentorNotFound() {
        User mentee = new User();
        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(mentee));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(1L, 3L));

        assertEquals("Ментор с ID 3 не найден у менти с ID 1", exception.getMessage());

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void test_deleteMentor_whenMentorFound() {
        User mentor = new User();
        mentor.setId(3L);
        mentor.setMentees(new ArrayList<>());

        User mentee = new User();
        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>(List.of(mentor)));

        Mockito.when(mentorshipRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(mentee));

        mentorshipService.deleteMentor(1L, 3L);

        assertTrue(mentee.getMentors().isEmpty());
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentee);
    }
}
