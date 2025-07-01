package school.faang.user_service.repository.promotion;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.promotion.PromotionRedisModel;

import java.util.Optional;

@Repository
@EnableRedisRepositories(repositoryImplementationPostfix = "NativeImpl")
public interface PromotionRedisRepository extends CrudRepository<PromotionRedisModel, String>,
        PromotionRedisRepositoryNative {
    Optional<PromotionRedisModel> findByEventId(Long eventId);

    Optional<PromotionRedisModel> findByUserId(Long userId);
}
