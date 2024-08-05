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
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDto = new MentorshipRequestDto();
    }

    @Test
    @DisplayName("Проверка на то, что описание не содержит null, пустую строку или строку с пробелами")
    public void validateRequestReturnTrueIfTheDescriptionIsCorrect() {
        mentorshipRequestDto.setDescription("jgk");
        Assertions.assertTrue(mentorshipRequestValidator.validateRequest(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Если в описании null бросается RuntimeException")
    public void validateRequestThrowRuntimeExceptionIfDescriptionIsNull() {
        Assertions.assertFalse(mentorshipRequestValidator.validateRequest(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Если в описании строка пустая или содержит пробелы бросается RuntimeException")
    public void validateRequestThrowRuntimeExceptionIfTheDescriptionIsEmptyOrContainsSpaces() {
        mentorshipRequestDto.setDescription("");
        Assertions.assertFalse(mentorshipRequestValidator.validateRequest(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Если пользователь пытается стать ментором для себя бросается RuntimeException")
    public void validateRequestThrowRuntimeExceptionIfTheUserWantsToBeAMentorForHimself() {
        Long userId = 1L;
        mentorshipRequestDto.setUserId(userId);
        mentorshipRequestDto.setMentorId(userId);
        Assertions.assertThrows(RuntimeException.class, () -> mentorshipRequestValidator.checkIdAndDates(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Если заявка была оформлена меньше чем три месяца назад бросается RuntimeException")
    public void checkRequestDateReturnFalseIfTheApplicationWasSubmittedLessThanThreeMonthsAgo() {
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        Assertions.assertThrows(RuntimeException.class, () -> mentorshipRequestValidator.checkIdAndDates(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Проверка на то, что пользователи существуют")
    public void checkUserAndMentorExistsReturnTrueIfUsersExist() {
        Mockito.when(userService.existsById(anyLong()))
                .thenReturn(true);
        Assertions.assertTrue(mentorshipRequestValidator.checkUserAndMentorExists(1L, 2L));
    }

    @Test
    @DisplayName("Проверка на то, что пользователи не существуют")
    public void checkUserAndMentorExistsReturnFalseIfUsersDontExist() {
        Mockito.when(userService.existsById(anyLong()))
                .thenReturn(false);
        Assertions.assertFalse(mentorshipRequestValidator.checkUserAndMentorExists(1L, 2L));
    }
}