package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @Mock

    @BeforeEach
    }

    @Test

        when(userFilter.apply(any(Stream.class), eq(filterDto))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(userMapper.toDto(user)).thenReturn(userDto);
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, ()-> userService.deactivateUser(userId));

        verify(goalRepository, never()).findGoalsByUserId(userId);
        verify(eventRepository, never()).findAllByUserId(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeactivateUser_Success() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);



        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);
        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(userRepository, times(1)).save(user);

        assertFalse(user.isActive(), "User should be deactivated");
    }
}