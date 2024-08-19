package school.faang.user_service.validator.requestvalidator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateRequestDtoValidator implements Validator<RecommendationRequestDto> {
    private final List<Validator<RecommendationRequestDto>> createRequestValidators;


    @Override
    public boolean validate(RecommendationRequestDto data) {
        if (data.getRequesterId().equals(data.getReceiverId())) {
            throw new ValidationException("Author and receiver of a recommendation request cannot be the same person");
        }
        return createRequestValidators.stream().allMatch(v -> v.validate(data));
    }
}
