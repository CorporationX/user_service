package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipReqDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipReqServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestedEventPublisher eventPublisher;

    @InjectMocks
    private MentorshipReqServiceImpl mentorshipService;

    @Test
    void testShouldCreateMentorshipRequestWhenValidDto() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "Хочу учиться у вас";

        MentorshipReqDto dto = new MentorshipReqDto();
        dto.setRequesterId(requesterId);
        dto.setReceiverId(receiverId);
        dto.setDescription(description);

        User requester = new User();
        requester.setId(requesterId);

        User receiver = new User();
        receiver.setId(receiverId);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)).thenReturn(Optional.empty());

        mentorshipService.requestMentorship(dto);

        verify(mentorshipRequestRepository).create(requesterId, receiverId, description);
        verify(eventPublisher).publish(any());
    }

    @Test
    void testShouldThrowExceptionWhenNegativeUserIds() {
        long negativeSenderId = -1L;
        long negativeReceiverId = -5L;

        MentorshipReqDto dto = new MentorshipReqDto();
        dto.setRequesterId(negativeSenderId);
        dto.setReceiverId(negativeReceiverId);
        dto.setDescription("Хочу учиться");

        assertThatThrownBy(() -> mentorshipService.requestMentorship(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("должен быть больше 0");

        verify(mentorshipRequestRepository, never()).create(anyLong(), anyLong(), anyString());
        verify(eventPublisher, never()).publish(any());
    }
}