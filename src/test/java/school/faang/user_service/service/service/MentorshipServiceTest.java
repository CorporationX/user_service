package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void testGetMenteesWithNotExistingMentor(){

        when(mentorshipRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        assertThrows(IllegalArgumentException.class, ()->mentorshipService.getMentees(1L));
    }
}
