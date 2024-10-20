package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;
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

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE users SET active = :active
            WHERE id = :id
            """)
    void updateUserActive(long id, boolean active);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1 FROM users
                WHERE username = :username AND email = :email AND phone = :phone
            )
            """)
    boolean existsByUsernameAndEmailAndPhone(String username, String email, String phone);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            UPDATE users SET banned = true
            WHERE id IN (:ids)
            """)
    void banUsersById(List<Long> ids);

    @Query("""
            SELECT u, c.localeCode, cp.preference FROM User u
            LEFT JOIN FETCH u.country c
            LEFT JOIN FETCH u.contactPreference cp
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithCountryAndContactPreference(long id);
}