package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.ProjectSubscription;

@Repository
public interface ProjectSubscriptionRepository extends JpaRepository<ProjectSubscription, Long> {

    @Query(nativeQuery = true, value = "insert into project_subscription (project_id, follower_id) values (:projectId, :followerId)")
    @Modifying
    void followProject(long followerId, long projectId);

    @Query(nativeQuery = true, value = "select exists(select 1 from project_subscription where follower_id = :followerId and project_id = :projectId)")
    boolean existsByFollowerIdAndProjectId(long followerId, long projectId);
}
