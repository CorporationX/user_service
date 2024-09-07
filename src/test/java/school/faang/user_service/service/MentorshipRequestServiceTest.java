package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    MentorshipRequestService mentorshipRequestService;

    @Mock
    MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    MentorshipRequestRepository repository;

    @InjectMocks
    MentorshipRequestService service;

    @Test
    void test_service_method_called_() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(dto)).thenReturn(new Validated());
        service.requestMentorship(dto);
        verify(repository).create(dto.getRequesterId(),dto.getReceiverId(),dto.getDescription());
    }

    @Test
    void test_service_method_not_called(){
        MentorshipRequestDto dto = MentorshipRequestDto.builder().requesterId(1L).receiverId(2L).build();
        when(mentorshipRequestValidator.validate(dto)).thenReturn(new NotValidated(""));
        service.requestMentorship(dto);
        verifyNoInteractions(repository);
    }
}