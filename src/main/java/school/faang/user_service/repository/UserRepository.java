package school.faang.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.Collection;
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

    @Query("""
            SELECT u 
              FROM User u
              JOIN ProfilePromotion pp ON pp.profile = u
              JOIN FETCH u.country
              LEFT JOIN FETCH u.contactPreference
              LEFT JOIN FETCH u.premium
              LEFT JOIN FETCH u.workSchedule
             WHERE pp.active = true
               AND pp.plan   = :plan
            """)
    Slice<User> findAllActivePromotionByPlan(@Param("plan") Plan plan, Pageable pageable);

    @Query("""
            SELECT u
              FROM User u
              JOIN FETCH u.country
              LEFT JOIN FETCH u.contactPreference
              LEFT JOIN FETCH u.premium
              LEFT JOIN FETCH u.workSchedule
             WHERE NOT EXISTS (
               SELECT pp 
                 FROM ProfilePromotion pp
                WHERE pp.profile = u
                  AND pp.active  = true
             )
            """)
    Slice<User> findAllWithoutPromotion(Pageable pageable);

    Page<User> findByIdIn(Collection<Long> ids, Pageable pageable);
}
