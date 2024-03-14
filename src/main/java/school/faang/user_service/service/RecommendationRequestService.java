package school.faang.user_service.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.StringToLongMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@Service
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final StringToLongMapper stringToLongMapper;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository, SkillRequestRepository skillRequestRepository, StringToLongMapper stringToLongMapper) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.skillRequestRepository = skillRequestRepository;
        this.stringToLongMapper = stringToLongMapper;
    }

    @Transactional
    public void create(Long requesterId, Long receiverId, RecommendationRequestDto requestDto) {
        boolean requestExists = recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId);

        if (requestExists) {
            throw new IllegalArgumentException("Запрос на рекомендацию уже отправлен и еще не закрыт");
        }

        recommendationRequestRepository.createRequest(requesterId, receiverId);

        List<Long> skillIds = stringToLongMapper.stringToLong(requestDto.getSkills());
        for (Long skillId : skillIds) {
            skillRequestRepository.create(requesterId, skillId);
        }
    }

    @Transactional
    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        Long requesterId = filter.getRequesterId();
        Long receiverId = filter.getReceiverId();

        if (requesterId != null && receiverId != null) {
            return recommendationRequestRepository.findAllByRequesterIdAndReceiverId(requesterId, receiverId);
        } else if (requesterId != null) {
            return recommendationRequestRepository.findAllByRequesterId(requesterId);
        } else if (receiverId != null) {
            return recommendationRequestRepository.findAllByReceiverId(receiverId);
        } else {
            List<RecommendationRequest> allRequests = new ArrayList<>();
            recommendationRequestRepository.findAll().forEach(allRequests::add);
            return allRequests;
        }
    }

    @Transactional
    public RecommendationRequest getRequest(long id) {
        Optional<RecommendationRequest> recommendationRequestOptional = recommendationRequestRepository.findById(id);

        return recommendationRequestOptional.orElseThrow(() -> new NotFoundException("Рекомендация с id " + id + " не найдена"));
    }

    @Transactional
    public RecommendationRequest rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рекомендация с id " + id + " не найдена"));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Нельзя отклонить запрос, который уже принят или отклонен");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        return recommendationRequestRepository.save(recommendationRequest);
    }

}