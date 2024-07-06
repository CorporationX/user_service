package school.faang.user_service.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.shaded.freemarker.template.utility.NullArgumentException;
import org.springframework.stereotype.Service;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationRequestService {
    private static final long MONTH_FOR_SEARCH_REQUEST = 6L;
    private final RecommendationRequestController requestController;
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;

    public RecommendationRequestDto getRecommendationRequest(@Positive long id) {
        if ()
    }

    public RecommendationRequestDto createRecommendationRequest(RecommendationRequestDto requestDto) {
        validatorData(requestDto);
        RecommendationRequest request = requestRepository.save(requestMapper.toEntity(requestDto));
        return requestMapper.toDto(request);
    }

    private void validatorData(RecommendationRequestDto requestDto) {
        if (requestDto.getRequesterId() == requestDto.getRecieverId()) {
            log.error("Exception requester user can`t be reciever");
            throw new IllegalArgumentException("Exception requester user can`t be reciever");
        }
        if (!userRepository.existsById(requestDto.getRequesterId())) {
            log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NoSuchElementException("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (!userRepository.existsById(requestDto.getRecieverId())) {
            log.error("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
            throw new NoSuchElementException("User with id :" + requestDto.getRequesterId() + " doesn't exist!");
        }
        if (requestRepository.existsById(requestDto.getId())) {
            if (requestDto.getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
                log.error("6 months have passed since the last request was not made");
                throw new DateTimeException("6 months have passed since the last request was not made");
            }
        }
        if (requestDto.getMessage() == null) {
            log.error("Recommendation request does not contain a message");
            throw new NullArgumentException("Recommendation request does not contain a message");
        }
    }
}
