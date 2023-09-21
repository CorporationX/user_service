package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship_request (requester_id, receiver_id, description, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW())
            """)
    MentorshipRequest create(long requesterId, long receiverId, String description);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship_request
            WHERE requester_id = :requesterId AND receiver_id = :receiverId
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship_request
            WHERE requester_id = :requesterId
            ORDER BY created_at DESC
            """)
    List<MentorshipRequest> getAllRequestsForRequester(long requesterId);

    //TODO не работает
    @Query(nativeQuery = true, value = "SELECT mr.status FROM mentorship_request mr WHERE mr.id = :id")
    RequestStatus findStatusById(@Param("id") Long id);

}