package school.faang.user_service.repository.promotion;

import lombok.RequiredArgsConstructor;
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
public class EventPromotionRepositoryBatch {
    private final JdbcTemplate jdbcTemplate;

    @Retryable(
        retryFor = SQLException.class,
        maxAttemptsExpression = "${app.retryable.event_promotion_repository_batch.update_event_promotion.max_attempts}",
        backoff = @Backoff(
            delayExpression = "${app.retryable.event_promotion_repository_batch.update_event_promotion.delay}",
            multiplierExpression = "${app.retryable.event_promotion_repository_batch.update_event_promotion.multiplier}"
        )
    )
    @Transactional
    public void updateEventPromotions(Map<Long, Integer> eventPromotionViews) {
        String sql = "UPDATE event_promotion SET number_of_views = GREATEST(number_of_views - ?, 0) WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                long promotionId = (long) eventPromotionViews.keySet().toArray()[i];
                int views = eventPromotionViews.get(promotionId);
                ps.setInt(1, views);
                ps.setLong(2, promotionId);
            }

            @Override
            public int getBatchSize() {
                return eventPromotionViews.size();
            }
        });
    }
}
