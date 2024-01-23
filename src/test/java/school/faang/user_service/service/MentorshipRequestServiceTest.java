package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.time.LocalDateTime;
import java.util.Optional;

public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    MentorshipRequestService mentorshipRequestService;


    @Test
    public void testIsReceiverExistsIsInvalid() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(Mockito.anyLong(), Mockito.anyLong(), "String")
                ));

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());
    }

    @Test
    public void testIsRequesterExistsIsInvalid() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(Mockito.anyLong(), Mockito.anyLong(), "String")
                ));

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(Mockito.anyLong());
    }

    @Test
    public void testIsNotRequestToYourselfIsInvalid() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(1L, 1L, "description"))
        );

        Mockito.verify(userRepository, Mockito.times(1))
                .existsById(1L);
    }

    @Test
    public void testIsMoreThanThreeMonthsIsInvalid() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        MentorshipRequest request = new MentorshipRequest();
        request.setUpdatedAt(LocalDateTime.now().minusMonths(2));
        Mockito.when(mentorshipRequestRepository.findLatestRequest(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(request));

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(new MentorshipRequestDto(Mockito.anyLong(), Mockito.anyLong(), "description"))
        );
    }

    @Test
    public void testRequestMentorship() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        mentorshipRequestService.requestMentorship(new MentorshipRequestDto(Mockito.anyLong(), Mockito.anyLong(), "description"));
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyLong(), "description");
    }
}
