package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;
/**
 * This subscription service interface is intended for CRUD operations with user subscriptions,
 * and also for obtaining information about the number of subscribers of each user.
 * There are two validation methods that allow you to fulfill business requirements.
 *
 * @author Dmitry Mokhov
 * @version 1.0
 */

public interface UserSubscriptionService {

    /**
     * The followUser method is designed to create a subscription to a user. It takes into account
     * the business requirement that the user
     * cannot subscribe to itself.
     *
     * @param followerId the number of the user who becomes a subscriber
     * @param followeeId  number of followee
     * @since 1.0
     *
     */

    void followUser(long followerId, long followeeId);
    /**
     * The unfollowUser method is designed to subscribe to a user. It takes into account
     * the business requirement that the user
     * cannot unsubscribe from a user you are not subscribed to.
     *
     * @param followerId  the number of the user who becomes a subscriber
     * @param followeeId  number of followee
     * @since 1.0
     */

    void unfollowUser(long followerId, long followeeId);
    /**
     * The getFollowersCount method is designed to get the number of a user's followers.
     *
     * @param followeeId  user number whose number of subscribers can be found out
     * @since 1.0
     * @return returns a Count Response object containing information about the number
     * of subscribers the user has
     */

    CountResponse getFollowersCount(long followeeId);
    /**
     * The getFolloweesCount method is used to get the number of user subscriptions.
     *
     * @param followerId  the user number whose number of subscriptions you want to find out
     * @since 1.0
     * @return returns a Count Response object containing information about the number
     * of subscriptions the user has
     */

    CountResponse getFolloweesCount(long followerId);
    /**
     * The getFollowers method is used to get a list of the user's followers as a list of UserDto objects.
     *
     * @param followeeId the number of the user whose list of subscribers you want to get
     * @since 1.0
     * @return returns a list of the user's subscribers as a list of UserDto objects
     */

    List<UserDto> getFollowers(long followeeId);
    /**
     * The getFollowees method is designed to get a list of user's subscriptions as a list of UserDto objects.
     *
     * @param followerId the number of the user whose subscription list they want to receive
     * @since 1.0
     * @return returns a list of subscriptions for this user
     */


    List<UserDto> getFollowees(long followerId);
    /**
     *The validateId method is used to validate the user identification number.
     *
     * @param id user number to check
     * @since 1.0
     * @return returns true or false
     */


    boolean validateId(long id);
    /**
     * The findIdInSubscribers method is created to check whether the user is a subscriber or not.
     *
     * @param id user number to check
     * @since 1.0
     * @return returns true or false
     */


    boolean findIdInSubscribers(long id);

}
