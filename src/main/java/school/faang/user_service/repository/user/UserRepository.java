package school.faang.user_service.repository.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

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

    User findByEmailIgnoreCase(String email);

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }

    @Modifying
    @Query("""
            update User u
            set u.banned = true
            where u.id in :bannedIds
            """)
    void bannedByIds(@Param("bannedIds") List<Long> bannedIds);

    boolean existsByIdIn(Collection<Long> ids);

    @Query("SELECT f.id FROM User u JOIN u.followers f WHERE u.id = :authorId")
    List<Long> findFollowerIdsPaged(@Param("authorId") Long authorId, Pageable pageable);
}