package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorForRecommendationRequestService {
    private static final long MONTH_FOR_SEARCH_REQUEST = 6L;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository requestRepository;

    public void validatorData(RecommendationRequestDto requestDto) {
        if (requestDto.getRequesterId() == requestDto.getRecieverId()) {
            log.error("Exception requester user can`t be reciever");
            throw new DataValidationException("Exception requester user can`t be reciever");
        }
        if (!userRepository.existsById(requestDto.getRequesterId())) {
            ValidatorForRecommendationRequestService.log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NoSuchElementException("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (!userRepository.existsById(requestDto.getRecieverId())) {
            ValidatorForRecommendationRequestService.log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NoSuchElementException("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (requestRepository.existsById(requestDto.getId())) {
            if (requestDto.getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
                ValidatorForRecommendationRequestService.log.error("6 months have passed since the last request was not made");
                throw new DataValidationException("6 months have passed since the last request was not made");
            }
        }
        saveSkillRequest(requestDto);
    }

    private void saveSkillRequest(RecommendationRequestDto requestDto) {
        requestDto.getSkills().forEach(
                skill -> skillRequestRepository.create(requestDto.getId(), skill.getSkillId()));
    }
}
