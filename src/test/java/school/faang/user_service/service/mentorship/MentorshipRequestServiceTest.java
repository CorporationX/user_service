package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Captor
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;

    @Test
    public void testRequestMentorshipValidatorExecution() {
        MentorshipRequestDto mentorshipRequestDto = prepareMentorshipRequestDto();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestReceiverAndRequesterExistence(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestValidator, times(1))
                .validateReflection(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestFrequency(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getCreatedAt());
    }

    @Test
    public void testRequestMentorshipRepositoryCreateExecution() {
        MentorshipRequestDto mentorshipRequestDto = prepareMentorshipRequestDto();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository, times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription()
                );
    }

    @Test
    public void testRequestMentorshipMapperToDtoExecution() {
        MentorshipRequestDto mentorshipRequestDto = prepareMentorshipRequestDto();

        MentorshipRequest mentorshipRequestAfterCreation = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        when(mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription()))
                .thenReturn(mentorshipRequestAfterCreation);

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestMapper, times(1))
                .toDto(mentorshipRequestCaptor.capture());
        MentorshipRequest mentorshipRequest = mentorshipRequestCaptor.getValue();

        assertEquals(mentorshipRequestDto.getRequesterId(), mentorshipRequest.getRequester().getId());
        assertEquals(mentorshipRequestDto.getReceiverId(), mentorshipRequest.getReceiver().getId());
        assertEquals(mentorshipRequestDto.getDescription(), mentorshipRequest.getDescription());
        assertEquals(mentorshipRequestDto.getCreatedAt(), mentorshipRequest.getCreatedAt());
    }

    private MentorshipRequestDto prepareMentorshipRequestDto() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(2L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        mentorshipRequestDto.setDescription("description");
        return mentorshipRequestDto;
    }
}