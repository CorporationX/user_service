package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public Page<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("This user does not exist");
        }
        return subscriptionRepository.findByFolloweeId(followeeId,
                        PageRequest.of(filter.getPage(), filter.getPageSize()))
                .map(userMapper::toDto);
    }
}
