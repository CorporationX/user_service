package school.faang.user_service.listener;

public interface Activity {

    Long getUserId(Object object);

    Long getRating(Object object);

    <T extends Object> T getEntity();
}