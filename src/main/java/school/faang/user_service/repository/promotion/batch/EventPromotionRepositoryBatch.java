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
public class EventPromotionRepositoryBatch {
    private final static String UPDATE_EVENT_PROMOTION_QUERY =
            "UPDATE event_promotion SET number_of_views = GREATEST(number_of_views - ?, 0) WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateEventPromotions(Map<Long, Integer> eventPromotionViews) throws SQLException {
        jdbcTemplate.batchUpdate(UPDATE_EVENT_PROMOTION_QUERY,
                new PromotionBatchPreparedStatementSetter(eventPromotionViews));
    }
}
