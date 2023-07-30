package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Repository
public interface SkillOfferRepository extends JpaRepository<SkillOffer, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO skill_offer (skill_id, recommendation_id) VALUES (?1, ?2) returning id")
    Long create(long skillId, long recommendationId);

    void deleteAllByRecommendationId(long recommendationId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(so.id) FROM skill_offer so
            JOIN recommendation r ON r.id = so.recommendation_id AND r.receiver_id = :userId
            WHERE so.skill_id = :skillId
            """)
    int countAllOffersOfSkill(@Param("skillId") long skillId, @Param("userId")long userId);

    @Query(value = """
            SELECT so FROM SkillOffer so
            JOIN so.recommendation r
            WHERE so.skill.id = :skillId AND r.receiver.id = :userId
            """)
    List<SkillOffer> findAllOffersOfSkill(@Param("skillId") long skillId, @Param("userId") long userId);

    @Query(value = """
            SELECT so FROM SkillOffer so
            JOIN so.recommendation r
            WHERE r.receiver.id = :userId
            """)
    List<SkillOffer> findAllOffersToUser(@Param("userId") long userId);

    @Query(value = """
            SELECT r.author FROM SkillOffer so
            JOIN so.recommendation r
            ON r.receiver.id = :receiverId
            WHERE so.skill.id = :skillId
            """)
    List<User> findAllAuthorsBySkillIdAndReceiverId(@Param("skillId") long skillId, @Param("receiverId") long receiverId);
}