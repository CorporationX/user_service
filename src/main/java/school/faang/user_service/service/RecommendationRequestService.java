package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filer.RequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RequestFilter> requestFilter;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (userRepository.findById(recommendationRequestDto.getRequesterId()).isEmpty()
                || userRepository.findById(recommendationRequestDto.getRecieverId()).isEmpty()) {
            throw new IllegalArgumentException("Requester or Reciever does not exist in the database");
        }

        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getRecieverId());
        if (recommendationRequest.isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);
            if (recommendationRequest.get().getUpdatedAt().isAfter(localDateTime)) {
                throw new IllegalArgumentException("Recommendation request can only be sent once every 6 months");
            }
        }

        List<Long> skillsId = recommendationRequestDto.getSkillsId();
        List<Skill> skills = skillRepository.findAllById(skillsId);
        boolean result = skills.stream().map(Skill::getId).allMatch(skillsId::contains);
        if (!result) {
            throw new IllegalArgumentException("One or more requested skills do not exist in the database");
        }

        RecommendationRequest newRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequestRepository.save(newRequest);

        skills.forEach(skill -> skillRequestRepository.create(newRequest.getId(), skill.getId()));

        return recommendationRequestMapper.toDto(newRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RequestFilter> requestFilterTrue = requestFilter.stream()
                .filter(filterOne -> filterOne.isApplication(filter))
                .toList();
        List<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll();
        for (RequestFilter requestFilterItem : requestFilterTrue) {
            recommendationRequestsAll = requestFilterItem
                    .apply(recommendationRequestsAll.stream(), filter)
                    .toList();
        }
        return recommendationRequestsAll.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();

//        Stream<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll().stream();
//        requestFilter.stream()
//                .filter(requestFilter -> requestFilter.isApplication(filter))
//                .forEach(requestFilter -> requestFilter.apply(recommendationRequestsAll, filter));
//        return recommendationRequestsAll.map(recommendationRequestMapper::toDto).toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request with ID " + id + " not found"));
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        return recommendationRequestRepository.findById(id)
                .map(request -> {
                    if (request.getStatus() == RequestStatus.PENDING) {
                        request.setStatus(RequestStatus.REJECTED);
                        request.setRejectionReason(rejectionDto.getReason());
                        recommendationRequestRepository.save(request);
                        return recommendationRequestMapper.toDto(request);
                    } else {
                        throw new IllegalArgumentException("The status is not PENDING");
                    }
                }).orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }
}