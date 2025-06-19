package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataProcessingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserDataProcessingService userDataProcessingService;

    private List<Long> userIds;
    private List<User> users;
    private List<UserDto> userDtos;

    @BeforeEach
    void setUp() {
        userIds = List.of(1L, 2L, 3L);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        users = List.of(user1, user2);

        UserDto userDto1 = new UserDto(1L, "user1", "user1@example.com");
        UserDto userDto2 = new UserDto(2L, "user2", "user2@example.com");
        userDtos = List.of(userDto1, userDto2);
    }

    @Test
    void fetchUsers_shouldReturnEmptyList_whenIdsIsNull() {
        List<UserDto> result = userDataProcessingService.fetchUsers(null, 0, 10);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void fetchUsers_shouldReturnEmptyList_whenIdsIsEmpty() {
        List<UserDto> result = userDataProcessingService.fetchUsers(Collections.emptyList(), 0, 10);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void fetchUsers_shouldReturnEmptyList_whenLimitIsZero() {
        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, 0, 0);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void fetchUsers_shouldReturnEmptyList_whenLimitIsNegative() {
        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, 0, -1);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void fetchUsers_shouldReturnEmptyList_whenOffsetIsNegative() {
        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, -1, 10);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void fetchUsers_shouldReturnUserDtos_whenUsersFound() {
        int offset = 0;
        int limit = 2;
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("id").ascending());
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findByIdIn(userIds, pageable)).thenReturn(userPage);
        when(userMapper.toDto(users.get(0))).thenReturn(userDtos.get(0));
        when(userMapper.toDto(users.get(1))).thenReturn(userDtos.get(1));

        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, offset, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDtos.get(1), result.get(1));
        assertEquals(userDtos.get(1), result.get(1));
        verify(userRepository).findByIdIn(userIds, pageable);
        verify(userMapper, times(users.size())).toDto(any(User.class));
    }

    @Test
    void fetchUsers_shouldHandlePaginationCorrectly_whenOffsetIsNotZero() {
        int offset = 2;
        int limit = 2;
        int expectedPageNumber = offset / limit;
        Pageable expectedPageable = PageRequest.of(expectedPageNumber, limit, Sort.by("id").ascending());

        List<User> pagedUsers = List.of(new User(), new User());
        Page<User> userPage = new PageImpl<>(pagedUsers, expectedPageable, userIds.size());

        UserDto dto3 = new UserDto(3L, "user3", "user3@example.com");
        UserDto dto4 = new UserDto(4L, "user4", "user4@example.com");
        List<UserDto> expectedDtos = List.of(dto3, dto4);

        when(userRepository.findByIdIn(userIds, expectedPageable)).thenReturn(userPage);
        when(userMapper.toDto(pagedUsers.get(0)))
                .thenReturn(dto3)
                .thenReturn(dto4);

        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, offset, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);

        verify(userRepository).findByIdIn(userIds, expectedPageable);
        verify(userMapper, times(pagedUsers.size())).toDto(any(User.class));
    }


    @Test
    void fetchUsers_shouldReturnEmptyList_whenRepositoryReturnsEmptyPage() {
        int offset = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(0, limit, Sort.by("id").ascending());
        Page<User> emptyPage = Page.empty(pageable);

        when(userRepository.findByIdIn(userIds, pageable)).thenReturn(emptyPage);

        List<UserDto> result = userDataProcessingService.fetchUsers(userIds, offset, limit);

        assertTrue(result.isEmpty());
        verify(userRepository).findByIdIn(userIds, pageable);
        verifyNoInteractions(userMapper);
    }

    @Test
    void fetchUsers_shouldCorrectlyCalculatePageNumber() {
        int offset = 5;
        int limit = 2;
        Pageable expectedPageable = PageRequest.of(2, limit, Sort.by("id").ascending());
        Page<User> userPage = new PageImpl<>(users, expectedPageable, users.size());

        when(userRepository.findByIdIn(userIds, expectedPageable)).thenReturn(userPage);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(new UserDto(1L,"",""));

        userDataProcessingService.fetchUsers(userIds, offset, limit);

        verify(userRepository).findByIdIn(userIds, expectedPageable);
    }
}