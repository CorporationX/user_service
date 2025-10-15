package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceImplTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    MentorshipServiceImpl mentorshipService;

    @BeforeEach
    void initUser() {
        mentor = new User();
        mentor.setId(8L);

        mentee = new User();
        mentee.setId(7L);
    }

    private User mentor;
    private User mentee;

    User createSingleUser(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    UserDto initUserDto(long id) {
        return new UserDto(id,
                "Dima",
                "moneyWaster@mail.ru",
                "972-98-90-34",
                "Люблю футбол, шахматы и математику"
        );
    }

    @Test
    void testAddMentorship_whenRelationNotExist() {
        mentor.setMentees(new ArrayList<>(List.of(createSingleUser(10L))));
        mentee.setMentors(new ArrayList<>(List.of(createSingleUser(12L))));

        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);

        mentorshipService.addMentorship(mentor.getId(), mentee.getId());

        verify(mentorshipRepository, times(1)).save(mentee);

        assertTrue(mentee.getMentors().contains(mentor));
        assertTrue(mentor.getMentees().contains(mentee));
    }

    @Test
    void testAddMentorship_whenRelationExist() {
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));

        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);

        mentorshipService.addMentorship(mentor.getId(), mentee.getId());

        verify(mentorshipRepository, never()).save(mentee);

        assertTrue(mentee.getMentors().contains(mentor));
        assertTrue(mentor.getMentees().contains(mentee));
    }

    @Test
    void testDeleteMentorship_whenRelationExist() {
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));

        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentorship(mentee.getId(), mentor.getId());

        verify(mentorshipRepository, times(1)).save(mentee);

        assertFalse(mentee.getMentors().contains(mentor));
        assertFalse(mentor.getMentees().contains(mentee));
    }

    @Test
    void testDeleteMentorship_whenRelationNotExist() {
        mentor.setMentees(new ArrayList<>(List.of(createSingleUser(10L))));
        mentee.setMentors(new ArrayList<>(List.of(createSingleUser(12L))));

        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentorship(mentee.getId(), mentor.getId());

        verify(mentorshipRepository, never()).save(mentee);

        assertFalse(mentee.getMentors().contains(mentor));
        assertFalse(mentor.getMentees().contains(mentee));
    }

    @Test
    void testGetMentors() {
        mentee.setMentors(new ArrayList<>(List.of(mentor)));

        UserDto mentorDto = initUserDto(mentor.getId());

        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);
        when(userMapper.toUserDto(mentor)).thenReturn(mentorDto);
        List<UserDto> result = mentorshipService.getMentors(mentee.getId());

        assertNotNull(result);
        assertEquals(List.of(mentorDto), result);
        verify(mentorshipRepository).getByIdOrThrow(mentee.getId());
        verify(userMapper, times(1)).toUserDto(mentor);
    }

    @Test
    void testGetMentees() {
        mentor.setMentees(new ArrayList<>(List.of(mentee)));

        UserDto menteeDto = initUserDto(mentee.getId());

        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(userMapper.toUserDto(mentee)).thenReturn(menteeDto);
        List<UserDto> result = mentorshipService.getMentees(mentor.getId());

        assertNotNull(result);
        assertEquals(List.of(menteeDto), result);
        verify(mentorshipRepository).getByIdOrThrow(mentor.getId());
        verify(userMapper, times(1)).toUserDto(mentee);
    }
}