package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE receiver_id = ?1 AND status = 1
            ORDER BY created_at DESC
            """)
    List<RecommendationRequest> findAllRecommendationRequestForReceiver(long receiverId);
}