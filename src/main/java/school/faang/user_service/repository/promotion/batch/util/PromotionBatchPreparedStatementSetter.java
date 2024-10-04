package school.faang.user_service.repository.promotion.batch.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@RequiredArgsConstructor
public class PromotionBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private final Map<Long, Integer> promotionViews;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        long promotionId = (long) promotionViews.keySet().toArray()[i];
        int views = promotionViews.get(promotionId);
        ps.setInt(1, views);
        ps.setLong(2, promotionId);
    }

    @Override
    public int getBatchSize() {
        return promotionViews.size();
    }
}
