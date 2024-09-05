package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void testGetAllMentees() {
        when(mentorshipRepository.findMenteesByMentorId(1L)).thenReturn(new ArrayList<>());
        verify(mentorshipRepository, times(1)).findMenteesByMentorId(1L);
    }

    @Test
    void testGetMentors() {
        when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(new User()));
        verify(mentorshipRepository, times(1)).findById(2L);
    }
}