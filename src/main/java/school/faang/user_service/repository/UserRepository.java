package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Transactional
    @Modifying
    @Query("""
            UPDATE User u SET u.rankScore = u.rankScore + :rank WHERE u.id = :userId
            """)
    void updateUserRankByUserId(@Param("userId") Long userId, @Param("rank") double rank);

    @Transactional
    @Modifying
    @Query("""
            UPDATE User u SET u.rankScore = :minValue WHERE u.id = :userId
            """)
    void updateUserRankByUserIdToMin(@Param("userId") Long userId, @Param("minValue") BigDecimal minValue);

    @Transactional
    @Modifying
    @Query("""
        UPDATE User u SET u.rankScore = :maxValue WHERE u.id = :userId
        """)
    void updateUserRankByUserIdToMax(@Param("userId") Long userId, @Param("maxValue") BigDecimal maxValue);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u SET u.rankScore = u.rankScore - :rank 
            WHERE u.rankScore >= :rank
            AND u.id NOT IN(:activeUsersIds)
            """)
    void updatePassiveUsersRatingWhichRatingMoreThanRating(@Param("rank") double rank,
                                                           @Param("activeUsersIds") Set<Long> activeUsersIds);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u SET u.rankScore = 0 
            WHERE u.rankScore < :rank
            AND u.id NOT IN(:activeUsersIds)
            """)
    void updatePassiveUsersRatingWhichRatingLessThanRating(@Param("rank") double rank,
                                                           @Param("activeUsersIds") Set<Long> activeUsersIds);

    @Query("""
            SELECT u FROM User u
            WHERE u.id IN(:ids)
            """)
    Optional<List<User>> findAllByIds(@Param("ids") List<Long> ids);
}