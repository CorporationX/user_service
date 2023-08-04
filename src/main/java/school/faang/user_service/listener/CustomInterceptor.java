package school.faang.user_service.listener;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserActivity;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserActivityJpaRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class CustomInterceptor implements Interceptor {

    private UserActivityJpaRepository userActivityJpaRepository;

    private final List<Activity> activities;

    @Lazy
    @Autowired
    public CustomInterceptor(UserActivityJpaRepository userActivityJpaRepository, List<Activity> activities) {
        this.userActivityJpaRepository = userActivityJpaRepository;
        this.activities = activities;
    }

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (Arrays.asList(types).contains("rating")) {
            return false;
        }

        Long userId = null;
        long rating = 0;
        for (Activity activity : activities) {
            if (entity.getClass() == activity.getEntityClass()) {
                userId = activity.getUserId(entity);
                rating = activity.getRating(entity);
            }
        }
        if (userId == null) {
            return false;
        }

        userActivityJpaRepository.save(UserActivity
                .builder()
                .user(User
                        .builder()
                        .id(userId)
                        .build())
                .rating(rating)
                .build());

//        userActivityJpaRepository.updateRating(rating, userId);

        return Interceptor.super.onSave(entity, id, state, propertyNames, types);
    }
}
