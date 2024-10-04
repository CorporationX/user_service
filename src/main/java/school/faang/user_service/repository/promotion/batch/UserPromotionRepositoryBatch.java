package school.faang.user_service.repository.promotion.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.promotion.batch.util.PromotionBatchPreparedStatementSetter;

import java.sql.SQLException;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UserPromotionRepositoryBatch {
    private static final String UPDATE_USER_PROMOTION_QUERY =
            "UPDATE user_promotion SET number_of_views = GREATEST(number_of_views - ?, 0) WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateUserPromotions(Map<Long, Integer> userPromotionViews) throws SQLException {
        jdbcTemplate.batchUpdate(UPDATE_USER_PROMOTION_QUERY,
                new PromotionBatchPreparedStatementSetter(userPromotionViews));
    }
}
