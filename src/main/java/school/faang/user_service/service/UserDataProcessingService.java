package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDataProcessingService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> fetchUsers(List<Long> ids, int offset, int limit) {
        if (ids == null || ids.isEmpty()) {
            log.warn("User IDs list is null or empty. Returning empty list.");
            return Collections.emptyList();
        }
        if (limit <= 0) {
            log.warn("Limit must be positive. Received: {}. Returning empty list.", limit);
            return Collections.emptyList();
        }
        if (offset < 0) {
            log.warn("Offset must be non-negative. Received: {}. Returning empty list.", offset);
            return Collections.emptyList();
        }
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by("id").ascending());
        log.info("Fetching users for {} IDs with pageNumber: {}, pageSize: {}. Total IDs provided: {}",
                ids.size(), pageNumber, limit, ids.size());

        Page<User> userPage = userRepository.findByIdIn(ids, pageable);
        List<UserDto> userDtos = userPage
                .stream()
                .map(userMapper::toDto)
                .toList();
        log.info("Successfully fetched {} user DTOs from a page of {} entities.",
                userDtos.size(), userPage.getNumberOfElements());
        return userDtos;
    }

    @Transactional(readOnly = true)
    public UserDtoNotification fetchUserById(long userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        UserDtoNotification userDtoNotification = userMapper.toDtoNotification(user);
        log.info("Successfully fetched user DTO for ID: {}", userId);
        return userDtoNotification;
    }
}
