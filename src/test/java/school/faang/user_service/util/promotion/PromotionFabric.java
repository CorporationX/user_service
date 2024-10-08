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
    public static final int ACTIVE_NUMBER_OF_VIEWS = 1;
    public static final int NON_ACTIVE_NUMBER_OF_VIEWS = 0;

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

    public static Event getEvent(long id, String title, User owner, List<EventPromotion> promotions) {
        return Event
                .builder()
                .id(id)
                .title(title)
                .owner(owner)
                .promotions(promotions)
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

    public static Event getEvent(long id, List<EventPromotion> eventPromotions) {
        return Event
                .builder()
                .id(id)
                .promotions(eventPromotions)
                .build();
    }

    public static Event getEvent(User owner, List<EventPromotion> eventPromotions) {
        return Event
                .builder()
                .owner(owner)
                .promotions(eventPromotions)
                .build();
    }

    public static List<Event> getEvents(int number, List<EventPromotion> eventPromotions) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getEvent(i, eventPromotions))
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

    public static User getUser(long id, String username, List<UserPromotion> promotions) {
        return User
                .builder()
                .id(id)
                .username(username)
                .promotions(promotions)
                .build();
    }

    public static User getUser(long id, String username, List<UserPromotion> promotions, LocalDateTime createdAt) {
        return User
                .builder()
                .id(id)
                .username(username)
                .promotions(promotions)
                .createdAt(createdAt)
                .build();
    }

    public static User getUser(long id, List<UserPromotion> promotions) {
        return User
                .builder()
                .id(id)
                .promotions(promotions)
                .build();
    }

    public static User getUser(long id) {
        return User
                .builder()
                .id(id)
                .build();
    }

    public static List<User> getUsers(int number, List<UserPromotion> userPromotions) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> getUser(i, userPromotions))
                .toList();
    }

    public static List<User> getUsers(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::getUser)
                .toList();
    }

    public static List<User> buildUsersWithActivePromotion(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildUserWithActivePromotion)
                .toList();
    }

    public static User buildUserWithActivePromotion(Long id) {
        return User
                .builder()
                .id(id)
                .promotions(List.of(buildActiveUserPromotion(id)))
                .build();
    }

    public static List<UserPromotion> buildActiveUserPromotions(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildActiveUserPromotion)
                .toList();
    }

    public static UserPromotion buildActiveUserPromotion(Long id) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }

    public static UserPromotion buildNonActiveUserPromotion(Long id) {
        return UserPromotion
                .builder()
                .id(id)
                .numberOfViews(NON_ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }

    public static List<Event> buildEventsWithActivePromotion(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildEventWithActivePromotion)
                .toList();
    }

    public static Event buildEventWithActivePromotion(Long id) {
        return Event
                .builder()
                .id(id)
                .promotions(List.of(buildActiveEventPromotion(id)))
                .build();
    }

    public static List<EventPromotion> buildActiveEventPromotions(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(PromotionFabric::buildActiveEventPromotion)
                .toList();
    }

    public static EventPromotion buildActiveEventPromotion(Long id) {
        return EventPromotion
                .builder()
                .id(id)
                .numberOfViews(ACTIVE_NUMBER_OF_VIEWS)
                .build();
    }
}
