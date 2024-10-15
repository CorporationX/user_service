package school.faang.user_service.repository.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${user.constants.batch-size}")
    private int batchSize;


    @Async("taskExecutor")
    @Transactional
    @Override
    public void batchInsertUsers(List<User> users) {
        String sql = """
                INSERT INTO users (username, password, email, phone, about_me, active, city, country_id)
                VALUES (:username, :password, :email, :phone, :aboutMe, :active, :city, :countryId)
                ON CONFLICT DO NOTHING RETURNING id""";

        List<Long> savedUsersIds = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
            List<?> resultList = entityManager.createNativeQuery(sql)
                    .setParameter("username", user.getUsername())
                    .setParameter("password", user.getPassword())
                    .setParameter("email", user.getEmail())
                    .setParameter("phone", user.getPhone())
                    .setParameter("aboutMe", user.getAboutMe())
                    .setParameter("active", user.isActive())
                    .setParameter("city", user.getCity())
                    .setParameter("countryId", user.getCountry().getId())
                    .getResultList();

            if (!resultList.isEmpty()) {
                savedUsersIds.add(user.getId());
            }
        }
        log.info("{} users inserted", savedUsersIds.size());
    }
}
