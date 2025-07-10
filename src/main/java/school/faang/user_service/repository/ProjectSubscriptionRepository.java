package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.ProjectSubscription;

import java.util.List;

public interface ProjectSubscriptionRepository extends JpaRepository<ProjectSubscription, Long> {
    @Query(nativeQuery = true, value = """
            select exists (
                select 1 from project_subscription 
                where follower_id = :followerId 
                and project_id = :projectId
            )
            """)
    boolean existsByFollowerIdAndProjectId(long followerId, long projectId);

    @Query(nativeQuery = true, value = """
            insert into project_subscription  (follower_id, project_id) values (:followerId, :projectId)
            """)
    @Modifying
    void followProject(long followerId, long projectId);

    @Query(nativeQuery = true, value = """
            delete from project_subscription where follower_id = :followerId and project_id = :projectId
            """)
    @Modifying
    void unfollowProject(long followerId, long projectId);

    @Query(nativeQuery = true, value = "select count(id) from project_subscription where project_id = :projectId")
    int findFollowersAmountByProjectId(long projectId);

    @Query(nativeQuery = true, value = "select project_id from project_subscription where follower_id = :followerId")
    List<Long> getByFollowerId(long followerId);
}