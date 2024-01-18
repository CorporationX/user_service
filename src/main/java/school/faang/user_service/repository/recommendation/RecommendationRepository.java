package school.faang.user_service.repository.recommendation;

import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.recommendation.RecommendationDto;
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

    Page<Recommendation> findAllByReceiverId(long receiverId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT * FROM Recommendation WHERE receiverId = :userId
            """)
    List<Recommendation> findAllByReceiverId(@Param("userId") long userId);
//    @Query(nativeQuery = true, value = """
//            SELECT * FROM recommendation WHERE receiverId = :userId
//            """)
//    List<Recommendation> findAllByReceiverId(long userId);

    Page<Recommendation> findAllByAuthorId(long authorId, Pageable pageable);

    Optional<Recommendation> findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(long authorId, long receiverId);
}