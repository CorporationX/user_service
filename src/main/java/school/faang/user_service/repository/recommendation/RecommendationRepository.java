package school.faang.user_service.repository.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO recommendation (author_id, receiver_id, content)
            VALUES (?1, ?2, ?3) returning id
            """)
    Long create(long authorId, long receiverId, String content);

    @Query(nativeQuery = true, value = """
            WITH inserted_recommendation AS (
            INSERT INTO recommendation (author_id, receiver_id, content)
            VALUES (?1, ?2, ?3)
            RETURNING id
            )
            INSERT INTO skill_offer (skill_id, recommendation_id)
            VALUES (?4, (SELECT id FROM inserted_recommendation))
            RETURNING id;
            """)
    void createRecommendationWithSkillOffer(long authorId, long receiverId, String content, long skillId);

    @Query(nativeQuery = true, value = """
            UPDATE recommendation SET content = :content, updated_at = now()
            WHERE author_id = :authorId AND receiver_id = :receiverId
            """)
    @Modifying
    void update(long authorId, long receiverId, String content);

    @Query(nativeQuery = true, value = """
            WITH updated_recommendation AS (
            UPDATE recommendation
            SET content = :content, updated_at = now()
            WHERE author_id = :authorId AND receiver_id = :receiverId
            RETURNING id
            )
            UPDATE skill_offer
            SET skill_id = :skillId
            WHERE recommendation_id = (SELECT id FROM updated_recommendation)
            RETURNING id
            """)
    void updateRecommendationWithSkillOffer(long authorId, long receiverId, String content, long skillId);

    Page<Recommendation> findAllByReceiverId(long receiverId, Pageable pageable);

    Page<Recommendation> findAllByAuthorId(long authorId, Pageable pageable);

    Optional<Recommendation> findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(long authorId, long receiverId);
}