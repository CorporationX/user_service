package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private final Long userId = 2L;
    private final Long mentor = 1L;
    private final Long mentee = 3L;
    private final List<User> userList = Collections.emptyList();

    @Test
    void shouldReturnMentees(){
        //Mockito.when(userRepository.findById(userId).isPresent()).thenReturn(Boolean.TRUE);
        Mockito.when(mentorshipService.getMentees(userId)).thenReturn(new ArrayList<>()).getMock();
        assertEquals(userList, mentorshipService.getMentees(userId));
        Mockito.verify(mentorshipService, Mockito.times(1)).getMentees(userId);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionForMentor() {
        Mockito.when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->mentorshipService.getMentees(userId));
        Mockito.verify(mentorshipRepository).findById(userId);
    }



    @Test
    void shouldThrowEntityNotFoundExceptionForMentee() {
        Mockito.when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->mentorshipService.getMentors(userId));
        Mockito.verify(mentorshipRepository).findById(userId);
    }

    @Test
    void shouldReturnMentors(){

    }


}