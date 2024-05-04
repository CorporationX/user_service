package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validation.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    public final SubscriptionValidator subscriptionValidator;

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followersUsers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followersUsers, filter);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        //ToDo Сделать фильтрацию. На данный момент возвращается заглушка
        return List.of(new UserDto(1, "1", "11111"));
    }
}
