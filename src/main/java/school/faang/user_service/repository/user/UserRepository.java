package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

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

    User findByEmailIgnoreCase(String email);

    @Query(nativeQuery = true, value =
            """
            SELECT u.avatar_key FROM users u
            WHERE u.id = :userId
            """
    )
    Optional<String> findAvatarKeyById(Long userId);

    default String getAvatarKeyByIdOrThrow(Long userId) {
        return findAvatarKeyById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Аватар пользователя id=%d не был найден", userId))
                );
    }

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Пользователь id=%d не был найден", userId))
                );
    }

    @Query(nativeQuery = true, value = """
            SELECT id FROM users
            LIMIT :limit
            """)
    List<Long> findAllUser(@Param("limit") int limit);
}