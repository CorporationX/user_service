package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int REQUESTS_PERIOD_RESTRICTION = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    public void create(RecommendationRequestDto requestDto) {
        validateReceiverExistence(requestDto);
        validateRequesterExistence(requestDto);
        validateRequestsPeriod(requestDto);
        validateSkillsExistence(requestDto);
    }

    private void validateReceiverExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getReceiverId())) {
            throw new DataValidationException("We haven't managed to find receiver in DataBase");
        }
    }

    private void validateRequesterExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getRequesterId())) {
            throw new DataValidationException("We haven't managed to find requester in DataBase");
        }
    }

    private void validateRequestsPeriod(RecommendationRequestDto requestDto) {
        LocalDateTime createdDate = requestDto.getCreatedAt();
        LocalDateTime updatedDate = requestDto.getUpdatedAt();
        Duration periodBetween = Duration.between(updatedDate, createdDate);
        int monthsBetweenRequests = Period.ofDays((int) periodBetween.toDays()).getMonths();
        if (monthsBetweenRequests <= REQUESTS_PERIOD_RESTRICTION) {
            String message = ("You can't send request for " + Period.ofMonths(REQUESTS_PERIOD_RESTRICTION - monthsBetweenRequests).getDays() + " days");
            throw new DataValidationException(message);
        }
    }

    private void validateSkillsExistence(RecommendationRequestDto requestDto){
         List<Skill> skillList = skillRepository.findAllById(requestDto.getSkillsIds());
        if (!(skillList.size() ==requestDto.getSkillsIds().size())){
            throw new DataValidationException("One of skills have not been found in DataBase");
        }
    }

}
