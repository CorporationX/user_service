package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private static final long USER_ID = 1;
    private static final long MENTOR_ID = 2;
    private static final long MENTEE_ID = 3;
    private static final long NON_EXIST_ID = 7;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user1;
    private User mentee;
    private User mentor;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(USER_ID)
                .mentees(List.of())
                .mentors(List.of())
                .build();
        mentor = User.builder()
                .id(MENTOR_ID)
                .build();
        mentee = User.builder()
                .id(MENTEE_ID)
                .build();
        user2 = User.builder()
                .id(4L)
                .username("user1")
                .email("example1@domen")
                .phone("12312312312")
                .build();
        User user3 = User.builder()
                .id(5L)
                .username("user2")
                .email("example2@domen")
                .phone("3215412512")
                .build();
        mentee.setMentors(new ArrayList<>(List.of(user2)));
        mentor.setMentees(new ArrayList<>(List.of(user2, user3)));
    }

    @Test
    @DisplayName("Получение пустого списка менти у пользователя без менти")
    public void testGetEmptyMentees() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user1));
        List<MentorshipUserDto> result = mentorshipService.getMentees(USER_ID);
        assertEquals(user1.getMentees().size(), result.size());
        verify(mentorshipRepository).findById(USER_ID);
        verify(userMapper).toMentorshipUserDtos(user1.getMentees());
    }

    @Test
    @DisplayName("Получение списка менти у пользователя с существующим id")
    public void testGetMentees() {
        when(mentorshipRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        List<MentorshipUserDto> result = mentorshipService.getMentees(MENTOR_ID);
        assertEquals(mentor.getMentees().size(), result.size());
        List<Long> resultIds = result.stream()
                        .map(MentorshipUserDto::id)
                        .toList();
        assertTrue(resultIds.containsAll(List.of(4L, 5L)));
        verify(mentorshipRepository).findById(MENTOR_ID);
        verify(userMapper).toMentorshipUserDtos(mentor.getMentees());
    }

    @Test
    @DisplayName("Получение списка менти у пользователя с отрицательным id")
    public void testGetMenteesWithNegativeId() {
        long negativeId = -1;
        assertThrows(DataValidationException.class, () -> mentorshipService.getMentees(negativeId));
    }

    @Test
    @DisplayName("Получение списка менти пользователя с несуществующим id")
    public void testGetMenteesWithNonExistId() {
        when(mentorshipRepository.findById(NON_EXIST_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(NON_EXIST_ID));
    }

    @Test
    @DisplayName("Получение пустого списка менторов у пользователя без менторов")
    public void testGetEmptyMentors() {
        when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(user1));
        List<MentorshipUserDto> result = mentorshipService.getMentors(USER_ID);
        assertEquals(user1.getMentors().size(), result.size());
        verify(mentorshipRepository).findById(USER_ID);
        verify(userMapper).toMentorshipUserDtos(user1.getMentors());
    }

    @Test
    @DisplayName("Получение менторов у пользователя с существующим id")
    public void testGetMentors() {
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        List<MentorshipUserDto> result = mentorshipService.getMentors(MENTEE_ID);
        assertEquals(mentee.getMentors().size(), result.size());
        List<Long> resultIds = result.stream()
                .map(MentorshipUserDto::id)
                .toList();
        assertTrue(resultIds.contains(4L));
        verify(mentorshipRepository).findById(MENTEE_ID);
        verify(userMapper).toMentorshipUserDtos(mentee.getMentors());
    }

    @Test
    @DisplayName("Получение менторов у пользователя с отрицательным id")
    public void testGetMentorsWithNegativeId() {
        long negativeId = -1;
        assertThrows(DataValidationException.class, () -> mentorshipService.getMentors(negativeId));
    }

    @Test
    @DisplayName("Получение менторов пользователя с несуществующим id")
    public void testGetMentorsWithNonExistId() {
        when(mentorshipRepository.findById(NON_EXIST_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(NON_EXIST_ID));
    }

    @Test
    @DisplayName("Позитивный тест для удаления связи ментор-менти")
    public void testSuccessDeleteMentorship() {
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentorship(mentee.getId(), user2.getId());
        verify(mentorshipRepository).findById(MENTEE_ID);
        assertFalse(mentee.getMentors().contains(user2));
        verify(mentorshipRepository).save(mentee);
    }

    @Test
    @DisplayName("Удаление несуществующего ментора у менти")
    public void testDeleteNonExistMentor() {
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorship(MENTEE_ID, NON_EXIST_ID));
    }

    @Test
    @DisplayName("Удаление несуществующего менти у ментора")
    public void testDeleteNonExistMentee() {
        when(mentorshipRepository.findById(NON_EXIST_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorship(NON_EXIST_ID, USER_ID));
    }

    @Test
    @DisplayName("Удаление ментора с отрицательным id у менти")
    public void testDeleteMentorWithNegativeId() {
        long negativeId = -1;
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(negativeId, USER_ID));
    }

    @Test
    @DisplayName("Удаление ментора у менти с отрицательным id")
    public void testDeleteMentorByMenteeWithNegativeId() {
        long negativeId = -1;
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(USER_ID, negativeId));
    }

    @Test
    @DisplayName("Удаление ментора и менти с одинаковым id")
    public void testDeleteMentorAndMenteeWithSimilarId() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(1L, 1L));
    }
}
