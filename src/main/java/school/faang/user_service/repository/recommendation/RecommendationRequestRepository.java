package school.faang.user_service.repository.recommendation;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @Query("""
            SELECT r FROM RecommendationRequest r
            WHERE (:#{#f.requesterId} IS NULL OR r.requester.id = :#{#f.requesterId})
              AND (:#{#f.receiverId} IS NULL OR r.receiver.id = :#{#f.receiverId})
              AND (:#{#f.messageContains} IS NULL OR LOWER(r.message)
               LIKE LOWER(CONCAT('%', :#{#f.messageContains}, '%')))
              AND (:#{#f.status} IS NULL OR r.status = :#{#f.status})
            ORDER BY r.createdAt DESC
            """)
    List<RecommendationRequest> getByFilters(@Param("f") RecommendationRequestFilterDto f);

    default RecommendationRequest getByIdOrThrow(long requestId) {
        return findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Recommendation request %d not found", requestId))
        );
    }
}