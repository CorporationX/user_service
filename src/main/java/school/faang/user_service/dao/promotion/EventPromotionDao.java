package school.faang.user_service.dao.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.events.AnalyticsEventType;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPromotionDao extends AbstractPromotionDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String UPDATE_PROMOTIONS_SQL = """
            UPDATE event_promotion 
            SET current_views = ?, updated_at = now() 
            WHERE event_id = ?
            """;
    private static final String DEACTIVATE_PROMOTIONS_SQL = """
            UPDATE event_promotion 
            SET active = false, updated_at = now()  
            WHERE active = true 
            AND current_views >= num_promoted_views            
            """;
    @Value("${analytics.promotion.update-batch-size}")
    private Integer batchSize;

    @Override
    public List<Long> batchUpdatePromotions(Map<Long, Long> idsScoresMap) {
        List<Long> successUpdates = updateEventPromotionViews(idsScoresMap);
        deactivateEventPromotionViews();
        return successUpdates;
    }

    private List<Long> updateEventPromotionViews(Map<Long, Long> idsScoresMap) {
        log.info("Executing updateProfilePromotionViews");
        if (idsScoresMap.isEmpty()) {
            log.info("No views to update, skipping batch.");
            return Collections.emptyList();
        }
        List<Map.Entry<Long, Long>> entries = idsScoresMap.entrySet().stream().toList();

        List<Long> successUpdates;
        try {
            int[] updatedEvents = jdbcTemplate.batchUpdate(UPDATE_PROMOTIONS_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Map.Entry<Long, Long> args = entries.get(i);
                    ps.setLong(1, args.getValue());
                    ps.setLong(2, args.getKey());
                }

                @Override
                public int getBatchSize() {
                    return Math.min(entries.size(), batchSize);
                }
            });
            successUpdates = entries.stream().map(Map.Entry::getKey).toList();
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof BatchUpdateException bue) {
                int[] counts = bue.getUpdateCounts();
                successUpdates = processUpdateResults(entries, counts);
            } else {
                log.error("Unexpected DataAccessException in batchUpdate", dae);
                throw dae;
            }
        }
        return successUpdates;
    }

    private void deactivateEventPromotionViews() {
        log.info("Deactivating ended events promotions");
        jdbcTemplate.update(DEACTIVATE_PROMOTIONS_SQL);
    }

    @Override
    public AnalyticsEventType getEventType() {
        return AnalyticsEventType.EVENT_VIEW;
    }
}
