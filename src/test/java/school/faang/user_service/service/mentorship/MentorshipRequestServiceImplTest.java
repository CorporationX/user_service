package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    private static final Long USER_ID = 1L;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;
    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDto = new MentorshipRequestDto();
    }

    @Test
    @DisplayName("Если Optional null вернется true")
    public void mentorshipRequestServiceImplTestReturnDTOIfOptionalIsEmpty() {
        mentorshipRequestDto.setUserId(USER_ID);
        mentorshipRequestDto.setMentorId(USER_ID);
        Mockito.when(mentorshipRequestValidator.checkUserAndMentorExists(anyLong(), anyLong()))
                .thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertEquals(mentorshipRequestDto, mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Проверка на то, что можно создать заявку по истечению трех месяцев")
    public void mentorshipRequestServiceImplTestThatCanCreateAnApplicationIfMoreThanThreeMonthsHavePassed() {
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now().minusMonths(4));
        mentorshipRequestDto.setUserId(USER_ID);
        mentorshipRequestDto.setMentorId(USER_ID);
        Mockito.when(mentorshipRequestValidator.checkUserAndMentorExists(anyLong(), anyLong()))
                .thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(new MentorshipRequest()));
        Mockito.when(mentorshipRequestMapper.toDto(any()))
                .thenReturn(mentorshipRequestDto);
        Mockito.when(mentorshipRequestValidator.checkIdAndDates(mentorshipRequestDto))
                .thenReturn(true);
        Assertions.assertEquals(mentorshipRequestDto, mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Если заявка была создана меньше чем три месяца назад бросается исключение")
    public void mentorshipRequestServiceImplTestThrowsRuntimeExceptionIfTheRequestWasCreatedLessThanThreeMonthsAgo() {
        Mockito.when(mentorshipRequestValidator.checkUserAndMentorExists(anyLong(), anyLong()))
                .thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(new MentorshipRequest()));
        Mockito.when(mentorshipRequestValidator.checkIdAndDates(mentorshipRequestDto))
                .thenThrow(RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }
}