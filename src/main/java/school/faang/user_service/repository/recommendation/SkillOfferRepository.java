package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Repository
public interface SkillOfferRepository extends JpaRepository<SkillOffer, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO skill_offer (skill_id, recommendation_id) VALUES (?1, ?2) returning id")
    Long create(long skillId, long recommendationId);

    @Query(nativeQuery = true, value = "delete from skill_offer where recommendation_id = :recommendationId")
    void deleteAllByRecommendationId(long recommendationId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(so.id) FROM skill_offer so
            JOIN recommendation r ON r.id = so.recommendation_id AND r.receiver_id = :userId
            WHERE so.skill_id = :skillId
            """)
    int countAllOffersOfSkill(long skillId, long userId);

    @Query(value = """
            SELECT so FROM SkillOffer so
            JOIN so.recommendation r
            WHERE so.skill.id = :skillId AND r.receiver.id = :userId
            """)
    List<SkillOffer> findAllOffersOfSkill(long skillId, long userId);
}