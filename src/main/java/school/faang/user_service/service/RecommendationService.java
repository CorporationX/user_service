package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    public List<RecommendationDto> getAllUserRecommendations(long receiverId){
        return recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged())
                .stream()
                .map(recommendationMapper::toDto)
                .toList());
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationRepository.findAllByAuthorId(authorId)
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }
}