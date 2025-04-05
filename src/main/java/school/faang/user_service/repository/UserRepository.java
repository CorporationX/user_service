package school.faang.user_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE users
            SET banned = :banned
            WHERE id = :userId
            """)
    void setBannedField(long userId, boolean banned);

    List<User> findByIdIn(List<Long> ids, Pageable pageable);

    List<User> findAllByActiveTrue();

    long countByIdIn(List<Long> ids);

    Optional<User> findByTelegramUsername(String username);
}