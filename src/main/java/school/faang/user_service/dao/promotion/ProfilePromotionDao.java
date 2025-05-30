package school.faang.user_service.dao.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.EventType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfilePromotionDao implements PromotionUpdator {
    private final JdbcTemplate jdbcTemplate;
    private static final String UPDATE_PROFILE_SQL = """
            UPDATE profile_promotion 
            SET current_views = ?, updated_at = now() 
            WHERE profile_id = ?
            """;
    private static final String DEACTIVATE_PROFILE_SQL = """
            UPDATE profile_promotion 
            SET active = false, updated_at = now()  
            WHERE active = true 
            AND current_views >= num_promoted_views            
            """;

    @Override
    public void batchUpdatePromotions(Map<Long, Long> idsScoresMap) {
        updateProfilePromotionViews(idsScoresMap);
        deactivateProfilePromotionViews();
    }

    private void updateProfilePromotionViews(Map<Long, Long> idsScoresMap) {
        log.info("executing updateProfilePromotionViews");
        if (idsScoresMap.isEmpty()) {
            log.info("No views to update, skipping batch.");
            return;
        }
        List<Map.Entry<Long, Long>> entries = idsScoresMap.entrySet().stream().toList();

        int[] updateCounts = jdbcTemplate.batchUpdate(UPDATE_PROFILE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map.Entry<Long, Long> args = entries.get(i);
                ps.setLong(1, args.getValue());
                ps.setLong(2, args.getKey());
            }

            @Override
            public int getBatchSize() {
                return entries.size();
            }
        });
        log.info("Updated {} rows in profile_promotion", Arrays.stream(updateCounts).sum());
    }

    private void deactivateProfilePromotionViews() {
        log.info("Deactivating profile promotion views");
        jdbcTemplate.update(DEACTIVATE_PROFILE_SQL);
    }

    @Override
    public EventType getEventType() {
        return EventType.PROFILE_VIEW;
    }
}
