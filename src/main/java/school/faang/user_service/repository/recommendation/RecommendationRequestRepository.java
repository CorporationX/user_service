package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Optional;

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestAcceptedRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
            select * from recommendation_request
            where requester_id = ?1 and receiver_id = ?2
            order by created_at desc
            limit 1
            """)
    Optional<RecommendationRequest> findLatestRecommendationRequestFromRequesterToReceiver(
            long requesterId,
            long receiverId
    );

    @Query(nativeQuery = true, value = """
                insert into recommendation_request (requester_id, receiver_id, message, status, created_at, updated_at)
                values (:requesterId, :receiverId, :message, 0, now(), now())
            """)
    @Modifying
    RecommendationRequest create(long requesterId, long receiverId, String message);
}