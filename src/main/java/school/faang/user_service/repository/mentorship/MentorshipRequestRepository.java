package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship_request (requester_id, receiver_id, description, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW())
            RETURNING *
            """)
    MentorshipRequest create(long requesterId, long receiverId, String description);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship_request
            WHERE requester_id = :requesterId AND receiver_id = :receiverId
            AND created_at > :threeMonthsAgo
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<MentorshipRequest> findLatestRequestWithinPeriod(@Param("requesterId") Long requesterId,
                                                               @Param("receiverId") Long receiverId,
                                                               @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);

    @Query("""
        SELECT mr FROM MentorshipRequest mr 
        JOIN FETCH mr.requester 
        JOIN FETCH mr.receiver
        WHERE (:requesterId IS NULL OR mr.requester.id = :requesterId)
        AND (:receiverId IS NULL OR mr.receiver.id = :receiverId)
        AND (:status IS NULL OR mr.status = :status)
        ORDER BY mr.createdAt DESC
        """)
    List<MentorshipRequest> findByFilters(@Param("requesterId") Long requesterId,
                                         @Param("receiverId") Long receiverId,
                                         @Param("status") RequestStatus status);
}