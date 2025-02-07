package school.faang.user_service.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.redis.UserCache;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, Long> {
}
