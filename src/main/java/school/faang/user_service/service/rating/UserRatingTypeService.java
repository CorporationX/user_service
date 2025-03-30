package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.repository.rating.UserRatingTypeRepository;

@RequiredArgsConstructor
@Service
public class UserRatingTypeService {
    private final UserRatingTypeRepository userRatingTypeRepository;

    public UserRatingType findByName(RatingType type) {
        return userRatingTypeRepository.findByName(type);
    }

    public UserRatingType save(UserRatingType userRatingType) {
        return userRatingTypeRepository.save(userRatingType);
    }

    public UserRatingType findById(Long id) {
        return userRatingTypeRepository.findById(id).orElse(null);
    }

    public void delete(UserRatingType userRatingType) {
        userRatingTypeRepository.delete(userRatingType);
    }

}
