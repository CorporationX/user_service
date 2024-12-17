package school.faang.user_service.scheduled;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.document.UserDocument;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.elasticsearch.UserElasticsearchRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchDataSyncService {
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final UserMapper userMapper;
    private final JedisPool jedisPool;
    private final EntityManager entityManager;

    @Value("${spring.data.redis.elastic-search-key}")
    private String elasticsearchRedisKey;

    @Scheduled(cron = "${spring.task.scheduling.cron-elastic-search-data-sync}")
    @Transactional
    public void syncData() {
        log.info("Starting data synchronization from PostgreSQL to Elasticsearch...");

        EntityGraph<?> entityGraph = entityManager.getEntityGraph("User.skills");
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class)
                .setHint("jakarta.persistence.loadgraph", entityGraph)
                .getResultList();

        List<UserDocument> userDocuments = users.parallelStream()
                .map(user -> {
                    synchronized (user) {
                        try (Jedis jedis = jedisPool.getResource()) {
                            UserDocument userDocument = userMapper.userToUserDocument(user);
                            int premiumScore = 0;

                            if (user.getPremium() != null && user.getPremium().isActive()) {
                                premiumScore += user.getPremium().getPremiumPeriod().getSearchScore();
                            }
                            userDocument.setSearchScore(jedisGetOrDefault(jedis, user.getId(), 0) + premiumScore);
                            return userDocument;
                        }
                    }
                }).toList();

        userElasticsearchRepository.saveAll(userDocuments);

        log.info("Data synchronization completed from PostgreSQL to Elasticsearch");
    }

    private int jedisGetOrDefault(Jedis jedis, long key, int defaultValue) {
        String value = jedis.hget(elasticsearchRedisKey, String.valueOf(key));
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
