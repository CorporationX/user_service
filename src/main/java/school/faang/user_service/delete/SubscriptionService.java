//package school.faang.user_service.service;

//
//@Service
//@RequiredArgsConstructor
//public class SubscriptionService {
//    private final SubscriptionRepository subscriptionRepository;
//    private final FollowerEventPublisher followerEventPublisher;
//
//    public void followUser(SubscriptionDto subscriptionDto) {
//        long followerId = subscriptionDto.getFollowerId();
//        long followeeId = subscriptionDto.getFolloweeId();
//
//        validationSubscription(followerId, followeeId);
//        subscriptionRepository.followUser(followerId, followeeId);
//        followerEventPublisher.publish(new FollowerEventDto(followerId, followeeId, LocalDateTime.now()));
//    }
//
//    private void validationSubscription(long followerId, long followeeId) {
//        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
//            throw new IllegalArgumentException("Non-existent user id");
//        }
//
//        if (followerId == followeeId) {
//            throw new IllegalArgumentException("You can not subscribe to yourself");
//        }
//    }
//}
