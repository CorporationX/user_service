package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserActivity;

import java.util.stream.Stream;

@Repository
public interface UserActivityJpaRepository extends CrudRepository<UserActivity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT ua.rating FROM user_activity ua
            WHERE ua.user_id = ?1
            """)
    Stream<Long> findUserActivityByUserId(long userId);

    @Query(nativeQuery = true, value = """
            UPDATE user_activity 
            SET rating = rating + ?1
            WHERTE user_id = ?2
            """)
    void updateRating(long rating, long userId);
}
