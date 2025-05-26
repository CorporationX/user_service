package school.faang.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.List;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM users u
            JOIN user_skill us ON us.user_id = u.id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = ?1 AND s.id IN (?2)
            """)
    int countOwnedSkills(long userId, List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_premium up ON up.user_id = u.id
            WHERE up.end_date > NOW()
            """)
    Stream<User> findPremiumUsers();

    List<User> findByUsernameLike(String username);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users AS u
            JOIN profile_promotion AS pp ON pp.profile_id = u.id
            WHERE pp.active = true 
            AND pp.plan = :plan
            """)
    Slice<User> findAllActivePromotedByPlan(@Param("plan") Plan plan, Pageable pageable);
}