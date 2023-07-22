package school.faang.user_service.volidator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.PairMentorshipDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class MentorshipRequestValidator {
    public PairMentorshipDto requestValidate(Optional<User> requesterOptional, Optional<User> receiverOptional) {
        User requester = requesterOptional.orElseThrow(() -> new DataValidationException("Requester was not found"));
        User receiver = receiverOptional.orElseThrow(() -> new DataValidationException("Receiver was not found"));

        if (requester.getId() == receiver.getId()) {
            throw new DataValidationException("request was not create, your mentor is you");
        }
        //было условие, что запросы можно отправлять только раз в 3 месяца, сделал так, что с премиум можно делать 5 раз
        //сделал через try, потому что если поля не объявлены(то есть никогда не было премиума, вылетает ошибка)
        int numberOfRequestsInMonth = 0;
        try {
            numberOfRequestsInMonth = requester.getPremium().getEndDate().isAfter(LocalDateTime.now()) ? 5 : 3;
        } catch (NullPointerException e) {
            numberOfRequestsInMonth = 3;
        }

        List<MentorshipRequest> sentRequests = requester.getSentMentorshipRequests();
        if (sentRequests.size() >= numberOfRequestsInMonth) {

            MentorshipRequest thirdLatestRequest = sentRequests.get(sentRequests.size() - numberOfRequestsInMonth);
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

            if (!thirdLatestRequest.getCreatedAt().isBefore(oneMonthAgo)) {
                throw new DataValidationException("request was not created, too many requests in this month");
            }
        }

        return new PairMentorshipDto(requester,receiver);
    }

    public MentorshipRequest acceptRequestValidator(Optional<MentorshipRequest> requestOptional) {
        MentorshipRequest request = requestOptional.orElseThrow(() -> new DataValidationException("Request is not exist"));
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new DataValidationException("Already ACCEPTED");
        }
        if (requester.getMentors().contains(receiver)) {
            throw new DataValidationException("Already working");
        }
        return request;
    }

    public MentorshipRequest rejectRequestValidator(Optional<MentorshipRequest> requestOptional) {
        MentorshipRequest request = requestOptional.orElseThrow(() -> new DataValidationException("Request is not exist"));
        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new DataValidationException("Already REJECTED");
        }
        return request;
    }
}
