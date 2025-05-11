package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees() {
        long userId = 1L;

        User mentee = new User();
        mentee.setId(2L);
        List<User> mentees = List.of(mentee);

        MenteeDto menteeDto = new MenteeDto();
        menteeDto.setId(2L);
        List<MenteeDto> menteeDtos = List.of(menteeDto);

        Mockito.when(mentorshipRepository.findMenteesByMentorId(userId))
                .thenReturn(mentees);
        Mockito.when(mentorshipMapper.menteesToMenteesDtos(mentees))
                .thenReturn(menteeDtos);

        List<MenteeDto> result = mentorshipService.getMentees(userId);

        assertEquals(menteeDtos, result);
    }
}
