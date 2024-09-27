package school.faang.user_service.repository.promotion;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@EnableRetry
@RequiredArgsConstructor
@Repository
public class UserPromotionRepositoryBatch {
    private final JdbcTemplate jdbcTemplate;

    @Retryable(
        retryFor = SQLException.class,
        maxAttemptsExpression = "${app.retryable.user_promotion_repository_batch.update_user_promotion.max_attempts}",
        backoff = @Backoff(
            delayExpression = "${app.retryable.user_promotion_repository_batch.update_user_promotion.delay}",
            multiplierExpression = "${app.retryable.user_promotion_repository_batch.update_user_promotion.multiplier}"
        )
    )
    @Transactional
    public void updateUserPromotions(Map<Long, Integer> userPromotionViewsCopy) {
        String sql = "UPDATE user_promotion SET number_of_views = GREATEST(number_of_views - ?, 0) WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                long promotionId = (long) userPromotionViewsCopy.keySet().toArray()[i];
                int views = userPromotionViewsCopy.get(promotionId);
                ps.setInt(1, views);
                ps.setLong(2, promotionId);
            }

            @Override
            public int getBatchSize() {
                return userPromotionViewsCopy.size();
            }
        });
    }
}
