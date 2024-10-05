package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
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

    @Query(nativeQuery = true, value = """
            SELECT u.*
            FROM users u
            LEFT JOIN user_promotion up ON u.id = up.user_id AND up.number_of_views > 0
            ORDER BY
                up.coefficient DESC NULLS LAST,
                up.creation_date ASC,
                u.created_at DESC
            OFFSET :offset
            LIMIT :limit
            """)
    List<User> findAllSortedByPromotedUsersPerPage(@Param("offset") int offset, @Param("limit") int limit);

    @Query("""
            SELECT COUNT(f) FROM User u 
            JOIN u.followers f 
            WHERE u.id = :userId
            """)
    Integer countFollowersByUserId(@Param("userId") Long userId);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT id FROM User WHERE id IN :ids AND active = true")
    List<Long> findActiveUserIdsByIds(@Param("ids") List<Long> ids);
}