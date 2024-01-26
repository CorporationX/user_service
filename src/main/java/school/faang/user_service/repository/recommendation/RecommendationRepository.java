package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO recommendation (author_id, receiver_id, content)
            VALUES (?1, ?2, ?3) returning id
            """)
    Long create(long authorId, long receiverId, String content);

    @Query(nativeQuery = true, value = """
            UPDATE recommendation SET content = :content, updated_at = now()
            WHERE author_id = :authorId AND receiverId = :receiverId
            """)
    @Modifying
    Recommendation update(long authorId, long receiverId, String content);

    List<Recommendation> findAllByReceiverId(long receiverId);

    List<Recommendation> findAllByAuthorId(long authorId);

    Optional<Recommendation> findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(long authorId, long receiverId);
}