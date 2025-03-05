package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.NotFoundRequestException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static int SIX_MONTHS = 6;
    private static String NOT_FOUND_REQUEST_MESSAGE = "Запрос не найден c id:";
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Optional<User> requester = userRepository.findById(recommendationRequestDto.getRequesterId());
        Optional<User> receiver = userRepository.findById(recommendationRequestDto.getReceiverId());

        if (
                requester.isPresent() &&
                receiver.isPresent() &&
                canRequestRecommendation(requester.get(), receiver.get()) &&
                allSkillsExist(recommendationRequestDto.getSkills())
        ) {
            RecommendationRequest toCreateRequest =
                    recommendationRequestMapper.toRecommendationRequest(recommendationRequestDto);
            recommendationRequestRepository.save(toCreateRequest);
            toCreateRequest
                    .getSkills()
                    .forEach(skill ->
                            skillRequestRepository.create(toCreateRequest.getId(), skill.getId()));
            return recommendationRequestMapper.toRecommendationRequestDto(toCreateRequest);
        }

        return null;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> requests = StreamSupport
                .stream(recommendationRequestRepository.findAll().spliterator(), false)
                .toList();
        return requests
                .stream()
                .filter(request -> filterByCondition(request, filterDto))
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long requestId) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(requestId);

        if (request.isPresent()) {
            return recommendationRequestMapper.toRecommendationRequestDto(request.get());
        } else {
            throw new NotFoundRequestException(String.format(NOT_FOUND_REQUEST_MESSAGE, requestId));
        }
    }

    private boolean canRequestRecommendation(User requester, User receiver) {
        Optional<RecommendationRequest> request =
                recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId());
        if (request.isPresent()) {
            LocalDate lastRequestDate = request.get().getCreatedAt().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long months = ChronoUnit.MONTHS.between(lastRequestDate, currentDate);
            return months >= SIX_MONTHS;
        } else {
            return true;
        }
    }

    private boolean allSkillsExist(List<Skill> skills) {
        return skills
                .stream()
                .allMatch(skill -> skillRepository.existsById(skill.getId()));
    }

    private boolean filterByCondition(RecommendationRequest request, RequestFilterDto filterDto) {
        if (validateRequestFilter(filterDto) ||
                        !request.getRequester().equals(filterDto.getRequester()) ||
                        !request.getReceiver().equals(filterDto.getReceiver()) ||
                        !request.getStatus().equals(filterDto.getStatus()) ||
                        !request.getSkills().containsAll(filterDto.getSkills()) ||
                        !request.getCreatedAt().isBefore(filterDto.getCreatedAt()) ||
                        !request.getUpdatedAt().isAfter(filterDto.getUpdatedAt())
        ) {
            return false;
        }

        return true;
    }

    private boolean validateRequestFilter(RequestFilterDto filter) {
        if (
                filter.getRequester() == null ||
                filter.getReceiver() == null ||
                filter.getStatus() == null ||
                filter.getCreatedAt() == null ||
                filter.getUpdatedAt() == null ||
                filter.getSkills() == null ||
                filter.getSkills().isEmpty()
        ) {
            return false;
        }
        return true;
    }
}
