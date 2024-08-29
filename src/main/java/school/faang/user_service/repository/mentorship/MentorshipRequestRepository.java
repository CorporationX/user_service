package school.faang.user_service.repository.mentorship;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.MentorshipRequest;

@Repository
public interface MentorshipRequestRepository extends CrudRepository<MentorshipRequest, Long> {

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

  @Modifying
  @Transactional
  @Query(value = """
      delete
      from mentorship_request mr
      where mr.requester_id = :id or mr.receiver_id = :id
      """, nativeQuery = true)
  int deleteAllMentorshipRequestById(@Param("id") long id);
}