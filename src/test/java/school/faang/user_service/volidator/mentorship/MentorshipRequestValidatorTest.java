package school.faang.user_service.volidator.mentorship;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MentorshipRequestValidatorTest {
    private MentorshipRequestValidator mentorshipRequestValidator = new MentorshipRequestValidator();
    Optional<User> requesterOptional = Optional.ofNullable(new User());
    Optional<User> receiverOptional = Optional.ofNullable(new User());
    User requester = requesterOptional.get();
    User receiver = receiverOptional.get();
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
    void requestValidateEquals() {
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.requestValidate(requesterOptional,requesterOptional));
    }

    @Test
    void requestValidateWithoutPremium_ThreeRequest() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(requesterOptional, receiverOptional);
        });
    }

    @Test
    void requestValidateWithPremium_FourRequest() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.requestValidate(requesterOptional, receiverOptional));
    }

    @Test
    void requestValidateWithPremium_FiveRequest() {
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
            mentorshipRequestValidator.requestValidate(requesterOptional, receiverOptional);
        });
    }

    @Test
    void requestValidateWithPremium_SixRequest() {
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
                ()->mentorshipRequestValidator.requestValidate(requesterOptional, receiverOptional));
    }

    @Test
    void requestValidate4RequestWithTimeoutWithOutPremium() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mentorshipRequests.add(new MentorshipRequest());
            mentorshipRequests.get(i).setCreatedAt(LocalDateTime.now());
        }
        mentorshipRequests.get(0).setCreatedAt(LocalDateTime.now().minusMonths(2));

        requester.setSentMentorshipRequests(mentorshipRequests);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.requestValidate(requesterOptional, receiverOptional);
        });
    }

    @Test
    void acceptRequestValidatorAlreadyACCEPT(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.acceptRequestValidator(Optional.of(mentorshipRequest)));
    }

    @Test
    void acceptRequestValidatorNotACCEPT(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.acceptRequestValidator(Optional.of(mentorshipRequest));
        });
    }

    @Test
    void acceptRequestValidatorAlreadyWorking(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        requesterOptional.get().setMentors(List.of(receiverOptional.get()));
        mentorshipRequest.setRequester(requesterOptional.get());
        mentorshipRequest.setReceiver(receiverOptional.get());

        mentorshipRequest.setStatus(RequestStatus.PENDING);

        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.acceptRequestValidator(Optional.of(mentorshipRequest)));
    }

    @Test
    void acceptRequestValidatorGoodWay(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);

        assertDoesNotThrow(() ->
            mentorshipRequestValidator.acceptRequestValidator(Optional.of(mentorshipRequest))
        );
    }

    @Test
    void rejectRequestValidatorAlreadyREJECTEED(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        assertThrows(DataValidationException.class,
                ()->mentorshipRequestValidator.rejectRequestValidator(Optional.of(mentorshipRequest)));
    }

    @Test
    void rejectRequestValidatorNotREJECTED(){
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        mentorshipRequest.setStatus(RequestStatus.PENDING);
        assertDoesNotThrow(() -> {
            mentorshipRequestValidator.rejectRequestValidator(Optional.of(mentorshipRequest));
        });
    }
}