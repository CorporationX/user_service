package school.faang.user_service.service;


import school.faang.user_service.dto.RecommendationDto;

public interface RecommendationService {
    RecommendationDto createOrUpdate(RecommendationDto dto);
    void delete(long id);
}
