package school.faang.user_service.repository.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class EventPromotionRepositoryBatch {
    private final JdbcTemplate jdbcTemplate;

    public void updateEventPromotions(Map<Long, Integer> eventPromotionViews) {
        String sql = "UPDATE event_promotion SET number_of_views = GREATEST(number_of_views - ?, 0) WHERE event_id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                long eventId = (long) eventPromotionViews.keySet().toArray()[i];
                int views = eventPromotionViews.get(eventId);
                ps.setInt(1, views);
                ps.setLong(2, eventId);
            }

            @Override
            public int getBatchSize() {
                return eventPromotionViews.size();
            }
        });
    }
}
