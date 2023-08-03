package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserActivity;

import java.util.Optional;

@Repository
public interface UserActivityJpaRepository extends CrudRepository<UserActivity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM user_activity u_a
            WHERE u_a.user_id = :userId
            """)
    Optional<UserActivity> findByUserId(long userId);
}
