package school.faang.user_service.dao.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public void saveUsersUsingBatchUpdate(List<User> users) {
        jdbcTemplate.batchUpdate("INSERT INTO users(password, username, email, phone, about_me, city, country_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, users.get(i).getPassword());
                        ps.setString(2, users.get(i).getUsername());
                        ps.setString(3, users.get(i).getEmail());
                        ps.setString(4, users.get(i).getPhone());
                        ps.setString(5, users.get(i).getAboutMe());
                        ps.setString(6, users.get(i).getCity());
                        ps.setLong(7, users.get(i).getCountry().getId());
                    }
                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                });
    }

}
