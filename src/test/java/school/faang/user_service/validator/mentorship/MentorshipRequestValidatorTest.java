package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exeption.DataValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MentorshipRequestValidatorTest {
    private MentorshipRequestValidator mentorshipRequestValidator = new MentorshipRequestValidator();
    User requester = new User();
    User receiver = new User();
    @BeforeEach
    void setUp() {
        long requesterId = 1L;
        long receiverId = 2L;

        requester.setId(requesterId);
        requester.setMentors(new ArrayList<>());
        receiver.setId(receiverId);
        receiver.setMentees(new ArrayList<>());
    }

    @Test
    void testRequestValidateEquals() {
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.requestValidate(requester,requester));
    }

    @Test
    void testRequestValidateWithoutPremium_ThreeRequest_True() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(requester, receiver);
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
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.requestValidate(requester, receiver));
    }

    @Test
    void testRequestValidateWithPremium_FiveRequest_True() {
        LocalDateTime now = LocalDateTime.now();
        Premium premium = new Premium(0,requester,now.minusDays(1),now.plusDays(1));
        requester.setPremium(premium);

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(requester, receiver);
        });
    }

    @Test
    void testRequestValidateWithPremium_SixRequest_Throw() {
        LocalDateTime now = LocalDateTime.now();
        Premium premium = new Premium(0,requester,now.minusDays(1),now.plusDays(1));
        requester.setPremium(premium);

        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.requestValidate(requester, receiver));
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
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(requester, receiver);
        });
    }

    @Test
    void testAcceptRequestValidatorAlreadyACCEPT_Throw(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.acceptRequestValidator(mentorshipRequest));
    }

    @Test
    void testAcceptRequestValidatorNotACCEPT_true(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.acceptRequestValidator(mentorshipRequest);
        });
    }

    @Test
    void testAcceptRequestValidatorAlreadyWorking_Throw(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        requester.setMentors(List.of(receiver));
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);

        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.acceptRequestValidator(mentorshipRequest));
    }

    @Test
    void testAcceptRequestValidatorGoodWay_True(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);

        assertDoesNotThrow(() ->
            mentorshipRequestValidator.acceptRequestValidator(mentorshipRequest)
        );
    }

    @Test
    void testRejectRequestValidatorAlreadyREJECTEED_Throw(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.rejectRequestValidator(mentorshipRequest));
    }

    @Test
    void testRejectRequestValidatorNotREJECTED_True(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.rejectRequestValidator(mentorshipRequest);
        });
    }
}