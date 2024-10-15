package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.UserProfilePic;
import school.faang.user_service.model.entity.Goal;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

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
    @Query("update User u set u.goals = ?1 where u.id = ?2")
    int updateGoalsById(@NonNull Goal goals, @NonNull Long id);

    @Modifying
    @Query("UPDATE User u SET u.userProfilePic = ?2 WHERE u.id = ?1")
    void saveUserProfilePic(Long userId, UserProfilePic userProfilePic);

    @Query("SELECT u.userProfilePic FROM User u WHERE u.id = ?1")
    UserProfilePic findUserProfilePicByUserId(Long userId);

    @Modifying
    @Query("UPDATE User u SET u.userProfilePic = null WHERE u.id = ?1")
    void deleteUserProfilePicByUserId(Long userId);
}