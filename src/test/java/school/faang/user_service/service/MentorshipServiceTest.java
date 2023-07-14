package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MenteeMapper menteeMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void getMentees_When() {
        when(mentorshipRepository.existsById(anyLong())).thenReturn(false);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(anyLong()));

        assertEquals("User with id not found", runtimeException.getMessage());
    }

    @Test
    public void getMentees_CorrectAnswer() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("ser");
        user1.setEmail("sssss@s.ru");
        user1.setPhone("12345");
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("len");
        user2.setEmail("llll@l.ru");
        user2.setPhone("54321");

        List<User> list = List.of(user1, user2);
        List<MenteeDto> menteeDtoList = MenteeMapper.INSTANCE.toUserDto(list); //mentee

        when(mentorshipRepository.getAllByMentorId(anyLong())).thenReturn(list);
        when(mentorshipRepository.existsById(anyLong())).thenReturn(true);
        when(menteeMapper.toUserDto(anyList())).thenReturn(menteeDtoList);

        List<MenteeDto> dtoList = mentorshipService.getMentees(1);

        assertNotNull(dtoList);
        assertEquals(menteeDtoList.get(0).getUsername(), dtoList.get(0).getUsername());
        assertEquals(2, dtoList.size());
    }
}