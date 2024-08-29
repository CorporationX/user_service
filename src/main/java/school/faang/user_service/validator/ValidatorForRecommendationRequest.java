package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorForRecommendationRequest {
    @Value("${number-months.new.request}")
    private long MONTH_FOR_SEARCH_REQUEST;
    private final RecommendationRequestRepository requestRepository;
    private final UserService userService;

    public void validate(RecommendationRequestDto requestDto) {
        if (requestDto.getRequesterId().equals(requestDto.getRecieverId())) {
            log.info("requester user can`t be reciever");
            throw new DataValidationException("requester user can`t be reciever");
        }
        userService.existUserById(requestDto.getRequesterId());
        userService.existUserById(requestDto.getRecieverId());
    }

    public void validaRecommendationRequestByIdAndUpdateAt(RecommendationRequestDto requestDto) {
        if (requestRepository.existsById(requestDto.getId())) {
            if (requestDto.getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
                String messageError = "Not enough time has passed to make another request";
                log.error(messageError);
                throw new DataValidationException(messageError);
            }
        }
    }
}
