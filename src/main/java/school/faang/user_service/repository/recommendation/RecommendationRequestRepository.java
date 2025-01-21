package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long>,
        JpaSpecificationExecutor<RecommendationRequest> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @Query(value = """
                SELECT * 
                FROM recommendation_request rr
                WHERE rr.requester_id = :requesterId 
                AND rr.created_at >= NOW() - INTERVAL '6 months'
                ORDER BY rr.created_at DESC
                LIMIT 1
            """, nativeQuery = true)
    Optional<RecommendationRequest> findLatestRecommendationInLast6Months(@Param("requesterId") Long requesterId);

    Optional<RecommendationRequest> findRecommendationRequestByIdAndStatus(Long recommendationId, RequestStatus status);

}