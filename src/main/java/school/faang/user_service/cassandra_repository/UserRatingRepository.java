package school.faang.user_service.cassandra_repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserRating;

import java.util.UUID;

@Repository
public interface UserRatingRepository extends CassandraRepository<UserRating, UUID> {
}
