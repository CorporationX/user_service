package school.faang.user_service.util.promotion;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

public class PromotionFabric {

    public static EventPromotion getEventPromotion(long id, Event event, int numberOfViews, int audienceReach,
                                                   LocalDateTime creationDate) {
        return EventPromotion
                .builder()
                .id(id)
                .event(event)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static EventPromotion getEventPromotion(PromotionTariff tariff, int numberOfViews) {
        return EventPromotion
                .builder()
                .promotionTariff(tariff)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static EventPromotion getEventPromotion(long id, int numberOfViews) {
        return EventPromotion
                .builder()
                .id(id)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static Event getEvent(long id, String title, User owner, EventPromotion promotion) {
        return Event
                .builder()
                .id(id)
                .title(title)
                .owner(owner)
                .promotion(promotion)
                .build();
    }

    public static Event getEvent(long id) {
        return Event
                .builder()
                .id(id)
                .build();
    }

    public static Event getEvent(User owner) {
        return Event
                .builder()
                .owner(owner)
                .build();
    }

    public static Event getEvent(long id, EventPromotion eventPromotion) {
        return Event
                .builder()
                .id(id)
                .promotion(eventPromotion)
                .build();
    }

    public static Event getEvent(User owner, EventPromotion eventPromotion) {
        return Event
                .builder()
                .owner(owner)
                .promotion(eventPromotion)
                .build();
    }

    public static List<Event> getEvents(int number, EventPromotion eventPromotion) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getEvent(i, eventPromotion))
                .toList();
    }

    public static List<Event> getEvents(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::getEvent)
                .toList();
    }

    public static UserPromotion getUserPromotion(long id, User user, int numberOfViews, int audienceReach,
                                                 LocalDateTime creationDate) {
        return UserPromotion
                .builder()
                .id(id)
                .user(user)
                .numberOfViews(numberOfViews)
                .audienceReach(audienceReach)
                .creationDate(creationDate)
                .build();
    }

    public static UserPromotion getUserPromotion(PromotionTariff tariff, int numberOfViews) {
        return UserPromotion
                .builder()
                .promotionTariff(tariff)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static UserPromotion getUserPromotion(long id) {
        return UserPromotion
                .builder()
                .id(id)
                .build();
    }

    public static UserPromotion getUserPromotion(long id, int numberOfViews) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(numberOfViews)
                .build();
    }

    public static User getUser(long id, String username, UserPromotion promotion) {
        return User
                .builder()
                .id(id)
                .username(username)
                .promotion(promotion)
                .build();
    }

    public static User getUser(long id, UserPromotion promotion) {
        return User
                .builder()
                .id(id)
                .promotion(promotion)
                .build();
    }

    public static User getUser(long id) {
        return User
                .builder()
                .id(id)
                .build();
    }

    public static List<User> getUsers(int number, UserPromotion userPromotion) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getUser(i, userPromotion))
                .toList();
    }

    public static List<User> getUsers(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::getUser)
                .toList();
    }
}
