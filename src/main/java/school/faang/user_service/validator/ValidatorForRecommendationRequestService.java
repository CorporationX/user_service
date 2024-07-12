package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.exeptions.NotFoundElement;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorForRecommendationRequestService {
    private static final long MONTH_FOR_SEARCH_REQUEST = 6L;
    private final UserRepository userRepository;
    private final RecommendationRequestRepository requestRepository;

    public void validatorData(RecommendationRequestDto requestDto) {
        if (requestDto.getRequesterId() == requestDto.getRecieverId()) {
            log.error("Exception requester user can`t be reciever");
            throw new DataValidationException("Exception requester user can`t be reciever");
        }
        if (!userRepository.existsById(requestDto.getRequesterId())) {
            ValidatorForRecommendationRequestService.log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NotFoundElement("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (!userRepository.existsById(requestDto.getRecieverId())) {
            ValidatorForRecommendationRequestService.log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NotFoundElement("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (requestRepository.existsById(requestDto.getId())) {
            if (requestDto.getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
                ValidatorForRecommendationRequestService.log.error("6 months have passed since the last request was not made");
                throw new DataValidationException("6 months have passed since the last request was not made");
            }
        }
    }
}
