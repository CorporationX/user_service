package school.faang.user_service.listener;

public interface Activity {

    Long getUserId(Object object);

    Long getRating(Object object);

    Class getEntityClass();
}