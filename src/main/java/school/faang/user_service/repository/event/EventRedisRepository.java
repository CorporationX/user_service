package school.faang.user_service.repository.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.event.EventRedisModel;

@Repository
public interface EventRedisRepository extends CrudRepository<EventRedisModel, String> {
}
