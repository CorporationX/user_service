//package school.faang.user_service.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.FollowerEvent;
//import school.faang.user_service.dto.SubscriptionDto;
//import school.faang.user_service.publisher.FollowerEventPublisher;
//import school.faang.user_service.repository.SubscriptionRepository;
//import school.faang.user_service.repository.UserRepository;
//
//@ExtendWith(MockitoExtension.class)
//public class SubscriptionServiceTest {
//    @InjectMocks
//    private SubscriptionService subscriptionService;
//    @Mock
//    private SubscriptionRepository subscriptionRepository;
//    @Mock
//    private FollowerEventPublisher followerEventPublisher;
//
//    @Test
//    public void testFollowerExistsIsInvalid() {
//        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, 2L);
//        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);
//
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                () -> subscriptionService.followUser(subscriptionDto));
//        assertEquals("Non-existent user id", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void testFollowerAndFolloweeExists() {
//        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, 1L);
//        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 1L)).thenReturn(true);
//
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                () -> subscriptionService.followUser(subscriptionDto));
//        assertEquals("You can not subscribe to yourself", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void testFollowUser() {
//        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, 2L);
//        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L ,2L)).thenReturn(true);
//
//        subscriptionService.followUser(subscriptionDto);
//        verify(subscriptionRepository, times(1)).followUser(1L ,2L);
//        verify(followerEventPublisher, times(1)).publish(new FollowerEvent(1L, 2L, any()));
//    }
//
//}
