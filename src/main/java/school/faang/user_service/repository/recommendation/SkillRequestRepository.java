package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.recommendation.SkillRequest;

public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO skill_request (request_id, skill_id)
            VALUES (:requestId, :skillId)
            RETURNING *
            """)
    SkillRequest create(long requestId, long skillId);
}