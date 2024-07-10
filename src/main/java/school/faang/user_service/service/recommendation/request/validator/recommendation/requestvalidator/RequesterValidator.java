package school.faang.user_service.service.recommendation.request.validator.recommendation.requestvalidator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.recommendation.request.validator.Validator;

@Component
@AllArgsConstructor
public class RequesterValidator implements Validator<RecommendationRequestDto> {

    private final UserRepository mUserRep;

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequestDto) {
        Long requesterId = recommendationRequestDto.getRequesterId();

        boolean requesterExists = mUserRep.existsById(requesterId);

        if (! requesterExists) {
            throw new ValidationException(
                    ExceptionMessage.USER_DOES_NOT_EXIST.getMessage() + "Check the requester id.",
                    requesterId
            );
        }

        return mUserRep.existsById(requesterId);
    }
}
