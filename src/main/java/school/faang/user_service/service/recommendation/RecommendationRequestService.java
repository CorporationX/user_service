package school.faang.user_service.service.recommendation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
//TODO: Доделать тесты!!!
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilter> filters;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator validator;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validator.verifyCanCreate(dto);
        
        RecommendationRequest recommendationRequest = recommendationRequestMapper.fromDto(dto);
        List<SkillRequest> skillRequests = convertSkillsToSkillsRequest(dto.getSkills(), recommendationRequest);
        recommendationRequest.setRequester(findUserById(dto.getRequesterId()));
        recommendationRequest.setReceiver(findUserById(dto.getReceiverId()));
        recommendationRequest.setSkills(skillRequests);
        
        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);
        
        skillRequestRepository.saveAll(skillRequests);
        
        return recommendationRequestMapper.toDto(savedRequest);
    }
    
    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filter) {
        List<RecommendationRequest> requests = StreamSupport.stream(
                recommendationRequestRepository.findAll().spliterator(),
                false
        ).toList();
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(requests.stream(), filter))
                .distinct()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }
    
    public RecommendationRequestDto getRequestById(Long id) {
        RecommendationRequest recommendationRequest = requestById(id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }
    
    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RecommendationRequestRejectionDto rejection) {
        RecommendationRequest recommendationRequest = requestById(id);
        validator.verifyStatusIsPending(recommendationRequest);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(recommendationRequest);
    }
    
    //TODO: Использовать классы-сервисы для получени юзеров, скиллов и т.д
    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            ()->new EntityNotFoundException("User not found")
        );
    }
    
    private RecommendationRequest requestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(
                () -> new RecommendationRequestNotFoundException(String.format(REQUEST_NOT_FOUND.getMessage()))
        );
    }
    
    private List<SkillRequest> convertSkillsToSkillsRequest(
        List<SkillRequestDto> skillRequestDtos,
        RecommendationRequest recommendationRequest
    ) {
        return skillRequestDtos.stream()
            .map(findSkillById())
            .map(skill -> new SkillRequest().builder().skill(skill).request(recommendationRequest).build())
            .toList();
    }
    
    //TODO: Заменить потом на реализацию из сервиса по получению скилл реквестов
    private Function<SkillRequestDto, Skill> findSkillById() {
        return skillRequestDto -> skillRepository.findById(skillRequestDto.getSkillId()).orElseThrow(
            () -> new EntityNotFoundException(String.format("Skill with id %d not found", skillRequestDto.getSkillId()))
        );
    }
}
