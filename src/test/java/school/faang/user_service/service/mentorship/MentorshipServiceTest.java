package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    private MentorshipService mentorshipService;
    private UserMapperImpl userMapper;
    private MentorshipRepository mentorshipRepository;
    private List<UserFilter> filters;

    @BeforeEach
    void init() {
        mentorshipRepository = Mockito.mock(MentorshipRepository.class);
        userMapper = Mockito.spy(UserMapperImpl.class);
        UserFilter filterMock = Mockito.mock(UserFilter.class);
        filters = List.of(filterMock);
        mentorshipService = new MentorshipService(mentorshipRepository, filters, userMapper);
    }

    @Test
    public void testGetMenteesWithNegativeId() {
        User user = prepareData(-1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(user.getId(), new UserFilterDto()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMenteesWithNonExistentId() {
        User user = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(user.getId(), new UserFilterDto()));

        assertEquals(expectedMessage, exception.getMessage());
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMenteesWithEmptyList() {
        User user = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(filters.get(0).isApplicable(new UserFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.empty());

        List<UserDto> mentees = mentorshipService.getMentees(user.getId(), new UserFilterDto());

        assertEquals(0, mentees.size());
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMenteesWithFilledList() {
        User firstUser = prepareData(1L, new ArrayList<>(), new ArrayList<>());
        User secondUser = prepareData(2L, new ArrayList<>(), new ArrayList<>());
        User user = prepareData(1212L, new ArrayList<>(List.of(firstUser, secondUser)), new ArrayList<>());
        UserDto firstUserDto = userMapper.toDto(firstUser);
        UserDto secondUserDto = userMapper.toDto(secondUser);
        List<UserDto> expectedMentees = new ArrayList<>(List.of(firstUserDto, secondUserDto));
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(filters.get(0).isApplicable(new UserFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(firstUser, secondUser));

        List<UserDto> mentees = mentorshipService.getMentees(user.getId(), new UserFilterDto());

        assertEquals(expectedMentees, mentees);
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMentorsWithNegativeId() {
        User user = prepareData(-1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(user.getId(), new UserFilterDto()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMentorsWithNonExistentId() {
        User user = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(user.getId(), new UserFilterDto()));

        assertEquals(expectedMessage, exception.getMessage());
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMentorsWithEmptyList() {
        User user = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(filters.get(0).isApplicable(new UserFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.empty());

        List<UserDto> mentors = mentorshipService.getMentors(user.getId(), new UserFilterDto());

        assertEquals(0, mentors.size());
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMentorsWithFilledList() {
        User firstUser = prepareData(1L, new ArrayList<>(), new ArrayList<>());
        User secondUser = prepareData(2L, new ArrayList<>(), new ArrayList<>());
        User user = prepareData(1212L, new ArrayList<>(), new ArrayList<>(List.of(firstUser, secondUser)));
        UserDto firstUserDto = userMapper.toDto(firstUser);
        UserDto secondUserDto = userMapper.toDto(secondUser);
        List<UserDto> expectedMentors = new ArrayList<>(List.of(firstUserDto, secondUserDto));
        when(mentorshipRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(filters.get(0).isApplicable(new UserFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(firstUser, secondUser));

        List<UserDto> mentors = mentorshipService.getMentors(user.getId(), new UserFilterDto());

        assertEquals(expectedMentors, mentors);
        verify(mentorshipRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testDeleteMenteeWithNegativeMentorId() {
        User mentor = prepareData(-1212L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeWithNonExistentMentorId() {
        User mentor = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeFromSingleValueList() {
        User mentee = prepareData(1313L, new ArrayList<>(), new ArrayList<>());
        User mentor = prepareData(1212L, new ArrayList<>(List.of(mentee)), new ArrayList<>());
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        boolean resultAfterDelete = mentorshipService.deleteMentee(mentee.getId(), mentor.getId());
        List<User> mentees = mentor.getMentees();

        assertTrue(resultAfterDelete);
        assertEquals(0, mentees.size());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    public void testDeleteMenteeFromListWithMultipleValues() {
        User firstMentee = prepareData(1313L, new ArrayList<>(), new ArrayList<>());
        User secondMentee = prepareData(1414L, new ArrayList<>(), new ArrayList<>());
        User mentor = prepareData(1212L, new ArrayList<>(List.of(firstMentee, secondMentee)), new ArrayList<>());
        when(mentorshipRepository.findById(mentor.getId()))
                .thenReturn(Optional.of(mentor));

        boolean resultAfterDelete = mentorshipService.deleteMentee(firstMentee.getId(), mentor.getId());
        List<User> mentees = mentor.getMentees();

        assertTrue(resultAfterDelete);
        assertEquals(1, mentees.size());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    public void testDeleteMentorWithNegativeMenteeId() {
        User mentor = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(-1212L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User's id cannot be less than zero";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithNonExistentMenteeId() {
        User mentor = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313L, new ArrayList<>(), new ArrayList<>());
        String expectedMessage = "User not found";
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(mentee.getId(), mentor.getId()));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testDeleteMentorFromSingleValueList() {
        User mentor = prepareData(1212L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1313L, new ArrayList<>(), new ArrayList<>(List.of(mentor)));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        boolean resultAfterDelete = mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
        List<User> mentors = mentee.getMentors();

        assertTrue(resultAfterDelete);
        assertEquals(0, mentors.size());
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    public void testDeleteMentorFromListWithMultipleValues() {
        User firstMentor = prepareData(1313L, new ArrayList<>(), new ArrayList<>());
        User secondMentor = prepareData(1414L, new ArrayList<>(), new ArrayList<>());
        User mentee = prepareData(1212L, new ArrayList<>(), new ArrayList<>(List.of(firstMentor, secondMentor)));
        when(mentorshipRepository.findById(mentee.getId()))
                .thenReturn(Optional.of(mentee));

        boolean resultAfterDelete = mentorshipService.deleteMentor(mentee.getId(), secondMentor.getId());
        List<User> mentors = mentee.getMentors();

        assertTrue(resultAfterDelete);
        assertEquals(1, mentors.size());
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    private User prepareData(long userId, List<User> mentees, List<User> mentors) {
        User user = new User();
        user.setId(userId);
        user.setMentees(mentees);
        user.setMentors(mentors);
        return user;
    }
}
