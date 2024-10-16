package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.ProjectSubscription;

import java.util.Optional;

@Repository
public interface ProjectSubscriptionRepository extends CrudRepository<ProjectSubscription, Long> {
    @Query(nativeQuery = true,
            value = "select exists (select 1 from project_subscription where follower_id = :followerId and project_id = :projectId)")
    boolean existsByFollowerIdAndProjectId(long followerId, long projectId);

    @Modifying
    @Query(nativeQuery = true,
            value = "insert into project_subscription  (follower_id, project_id) values (:followerId, :projectId)")
    void followProject(long followerId, long projectId);

    @Query("SELECT ps FROM ProjectSubscription ps WHERE ps.follower.id = :userId AND ps.projectId = :projectId")
    Optional<ProjectSubscription> findByUserIdAndProjectId(long userId, long projectId);
}