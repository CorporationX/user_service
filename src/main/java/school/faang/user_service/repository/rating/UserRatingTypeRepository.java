package school.faang.user_service.repository.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;

@Repository
public interface UserRatingTypeRepository extends JpaRepository<UserRatingType, Long> {
    UserRatingType findByName(RatingType type);
}
