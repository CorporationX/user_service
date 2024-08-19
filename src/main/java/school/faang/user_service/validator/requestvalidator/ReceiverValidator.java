package school.faang.user_service.validator.requestvalidator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.Validator;

@Component
@AllArgsConstructor
public class ReceiverValidator implements Validator<RecommendationRequestDto> {

    private final UserRepository userRep;

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequest) {
        Long receiverId = recommendationRequest.getReceiverId();

        boolean receiverExists = userRep.existsById(receiverId);

        if (!receiverExists) {
            throw new ValidationException(
                    ExceptionMessage.USER_DOES_NOT_EXIST.getMessage() + "Check the receiver id.",
                    receiverId
            );
        }

        return receiverExists;
    }
}
