package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;
    long requesterId = 1L;
    long receiverId = 2L;
    MentorshipRequestDto mentorshipRequestDto;
    User requester = new User();
    User receiver = new User();

    @BeforeEach
    void setUp() {
        requester.setId(requesterId);
        requester.setMentors(new ArrayList<>());
        receiver.setId(receiverId);
        receiver.setMentees(new ArrayList<>());

        mentorshipRequestDto = MentorshipRequestDto.builder()
                .id(0L)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
    }

    @Test
    void testRequestValidateNotEquals() {
        requester.setSentMentorshipRequests(new ArrayList<>());
        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);
        assertDoesNotThrow(() -> mentorshipRequestValidator.requestValidate(mentorshipRequestDto));
    }

    @Test
    void testRequestValidateEquals() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(requesterId)
                .build();
        assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requestValidate(mentorshipRequestDto));
    }

    @Test
    void testRequestValidateWithoutPremium_ThreeRequest_True() {

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        requester.setSentMentorshipRequests(mentorshipRequests);

        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(mentorshipRequestDto);
        });
    }

    @Test
    void testRequestValidateWithPremium_FourRequest_Throw() {

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        requester.setSentMentorshipRequests(mentorshipRequests);

        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);
        assertThrows(DataValidationException.class,
                ()-> mentorshipRequestValidator.requestValidate(mentorshipRequestDto));
    }

    @Test
    void testRequestValidateWithPremium_FiveRequest_True() {

        LocalDateTime now = LocalDateTime.now();
        Premium premium = new Premium(0, requester, now.minusDays(1), now.plusDays(1));
        requester.setPremium(premium);

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        requester.setSentMentorshipRequests(mentorshipRequests);
        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);

        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(mentorshipRequestDto);
        });
    }

    @Test
    void testRequestValidateWithPremium_SixRequest_Throw() {

        LocalDateTime now = LocalDateTime.now();
        Premium premium = new Premium(0, requester, now.minusDays(1), now.plusDays(1));
        requester.setPremium(premium);

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.requestValidate(mentorshipRequestDto));
    }

    @Test
    void testRequestValidate_FourRequestWithTimeoutWithoutPremium_true() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        mentorshipRequests.get(0).setCreatedAt(LocalDateTime.now().minusMonths(2));

        requester.setSentMentorshipRequests(mentorshipRequests);
        Mockito.when(userService.findUserById(requesterId))
                .thenReturn(requester);

        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(mentorshipRequestDto);
        });
    }

    @Test
    void testAcceptRequestValidatorAlreadyACCEPT_Throw() {

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.acceptRequestValidator(mentorshipRequestDto, RequestStatus.ACCEPTED));
    }

    @Test
    void testAcceptRequestValidatorAlreadyWorking_Throw() {
        UserDto requester = UserDto.builder()
                .build();

        Mockito.when(userService.getUser(requesterId))
                .thenReturn(requester);

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.acceptRequestValidator(mentorshipRequestDto, RequestStatus.PENDING));
    }

    @Test
    void testAcceptRequestValidatorGoodWay_True() {
        UserDto requester = UserDto.builder()
                .build();

        Mockito.when(userService.getUser(requesterId))
                .thenReturn(requester);


        assertDoesNotThrow(() ->
                mentorshipRequestValidator.acceptRequestValidator(mentorshipRequestDto, RequestStatus.PENDING)
        );
    }

    @Test
    void testRejectRequestValidatorAlreadyREJECTEED_Throw() {

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.rejectRequestValidator(mentorshipRequestDto, RequestStatus.REJECTED));
    }

    @Test
    void testRejectRequestValidatorNotREJECTED_True() {
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.rejectRequestValidator(mentorshipRequestDto, RequestStatus.PENDING);
        });
    }
}