package school.faang.user_service.service.dao.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public void saveUsersUsingBatchUpdate(List<User> users) {
        String sql = "INSERT INTO users(password, username, email, phone, about_me, city, country_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getAboutMe());
                ps.setString(6, user.getCity());
                ps.setLong(7, user.getCountry().getId());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
    }
}
