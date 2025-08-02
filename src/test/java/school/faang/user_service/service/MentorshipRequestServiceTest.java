package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;

    @Test
    @DisplayName("Should delete mentor from his mentees")
    public void deleteMentorFromMentees() {
        User mentor = User.builder()
                .id(1L)
                .build();
        User mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>() {
                    {
                        add(mentor);
                    }
                })
                .build();
        mentor.setMentees(List.of(mentee));
        when(userRepository.getByIdOrThrow(1L)).thenReturn(mentor);

        mentorshipRequestService.deactivateMentor(mentor.getId());
        assertEquals(new ArrayList<>(), mentee.getMentors());
    }
}