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

    //    @Query(nativeQuery = true, value = """
//            SELECT u.*
//            FROM users u
//            LEFT JOIN user_promotion up ON u.id = up.user_id
//            ORDER BY
//                CASE WHEN up.active IS NULL THEN 1 ELSE 0 END,
//                up.coefficient DESC,
//                up.creation_date ASC
//            OFFSET :offset
//            LIMIT :limit
//            """)
    @Query(nativeQuery = true, value = """
            SELECT u.*
            FROM users u
            LEFT JOIN user_promotion up ON u.id = up.user_id AND up.number_of_views > 0
            ORDER BY
                CASE WHEN up.coefficient IS NULL THEN 1 ELSE 0 END,
                up.coefficient DESC,
                up.creation_date ASC
            OFFSET :offset
            LIMIT :limit
            """)
    List<User> findAllSortedByPromotedUsersPerPage(@Param("offset") int offset, @Param("limit") int limit);

//    @Query(nativeQuery = true, value = """
//    SELECT u.*
//    FROM users u
//    LEFT JOIN user_promotion up ON u.id = up.user_id
//    ORDER BY
//        CASE WHEN up.coefficient IS NULL OR up.coefficient = 0 THEN 1 ELSE 0 END ASC,
//        up.coefficient DESC,
//        CASE WHEN up.coefficient IS NULL OR up.coefficient = 0 THEN up.creation_date END DESC,
//        up.creation_date ASC
//    OFFSET :offset
//    LIMIT :limit
//    """)
//    List<User> findAllSortedByPromotedUsersPerPage(@Param("limit") int limit, @Param("offset") int offset);

    @Query("""
            SELECT COUNT(f) FROM User u 
            JOIN u.followers f 
            WHERE u.id = :userId
            """)
    Integer countFollowersByUserId(@Param("userId") Long userId);
}