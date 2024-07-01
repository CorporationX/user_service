package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

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
            select exists (select 1 from users where username = ?1)
            """)
    boolean existsUserByUsername(String username);

    @Query(nativeQuery = true, value = """
            select exists (select 1 from users where phone = ?1)
            """)
    boolean existsUserByPhone(String phone);

    @Query(nativeQuery = true, value = """
            select exists (select 1 from users where email = ?1)
            """)
    boolean existsUserByEmail(String email);
}