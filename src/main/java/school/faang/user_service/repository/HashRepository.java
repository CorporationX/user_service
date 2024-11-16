package school.faang.user_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${unique.numbers.seq}")
    private String uniqueNumbersSeq;

    @Value("${hash.batch.size}")
    private int hashBatchSize;

    public List<Long> getUniqueNumbers(int n) {
        String sql = String.format("SELECT nextval('%s') FROM generate_series(1, ?)", uniqueNumbersSeq);
        return jdbcTemplate.query(sql, new Object[]{n}, (rs, rowNum) -> rs.getLong(1));
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash_value) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, argument) -> {
            ps.setString(1, argument);
        });
    }

    public List<String> getHashBatch() {
        String sql = "WITH deleted AS (DELETE FROM hash ORDER BY RANDOM() LIMIT ?) RETURNING hash_value";
        return jdbcTemplate.query(sql, new Object[]{hashBatchSize}, (rs, rowNum) -> rs.getString("hash_value"));
    }
}
