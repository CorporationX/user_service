package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("This user does not exist");
        }
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
