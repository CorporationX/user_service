package school.faang.user_service.service.recommendation.validator;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;


import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class RecommendationRequestValidator {

    public static final Long MIN_INTERVAL_MONTHS = 6L;

    public static void validateCreatedOrUpdatedRecommendationRequests(RecommendationRequest latestPendingRequest,
                                                                      Long requesterId, Long receiverId) {
        StringBuilder messageError = new StringBuilder();
        if (latestPendingRequest != null
                && LocalDateTime.now().minusMonths(MIN_INTERVAL_MONTHS).isBefore(latestPendingRequest.getCreatedAt())) {
            messageError.append(String.format("The last recommendation was sent less than %d months",
                    MIN_INTERVAL_MONTHS));
        }
        if (requesterId.equals(receiverId)) {
            messageError.append("Requester and receiver are equal");
        }
        throwMessageError(messageError);
    }

    public static void validateAcceptingAndRejecting(Long currentUserId, Long receiverId,
                                                     RequestStatus currentStatus) {
        StringBuilder messageError = new StringBuilder();
        if (!currentStatus.equals(RequestStatus.PENDING)) {
            messageError.append(String.format("RequestStatus must be %s", RequestStatus.PENDING));
        }
        if (!currentUserId.equals(receiverId)) {
            messageError.append("RequestStatus must be changed only by receiver");
        }
        throwMessageError(messageError);
    }

    public static void handleValidationError(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            throw new DataValidationException(message);
        }
    }

    private static void throwMessageError(StringBuilder messageError) {
        if (!messageError.toString().isBlank()) {
            throw new DataValidationException(messageError.toString());
        }
    }


}
