package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;
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

    boolean existsByUsername(String username);

    @Query(nativeQuery = true, value = """
            SELECT profile_pic_file_id FROM users
            WHERE id = :userId
            """)
    Optional<String> getUserProfileFileId(Long userId);

    @Query(value = "SELECT u FROM User u ORDER BY u.ratingPoints DESC LIMIT :limit")
    List<User> findTopByOrderByRatingPointsDesc(int limit);

    @Query(value = "SELECT u FROM User u WHERE u.ratingPoints < :minRating ORDER BY u.ratingPoints DESC LIMIT :limit")
    List<User> findTopByRatingBelowLimit(@Param("minRating") int minRating, @Param("limit") int limit);

    @Query(value = "SELECT u.followers FROM User u WHERE u.id = :authorId")
    List<User> findFollowersByUserId(@Param("authorId") Long authorId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.isBanned = true WHERE u.id = :userId")
    void banUserById(@Param("userId") Long userId);

}