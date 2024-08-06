package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.TestDataFactory;

import static java.lang.Long.MAX_VALUE;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private static final Long USER_ID = 1L;
    private static final Long INVALID_USER_ID = MAX_VALUE;

    @Test
    void givenUserIdWhenStopUserMentorshipThenAllMenteeMentorshipsAreRemoved() {
        // given - precondition
        var user = TestDataFactory.createUser();
        var userDto = TestDataFactory.createUserDto();

        when(userRepository.findById(USER_ID))
                .thenReturn(of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        // when - action
        var actualResult = mentorshipService.stopUserMentorship(USER_ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();

        verify(userRepository, times(1))
                .findById(USER_ID);
        verify(userMapper, times(1))
                .toDto(user);
    }

    @Test
    void givenInvalidUserIdWhenStopUserMentorshipThenThrowException() {
        // when - action
        var user = TestDataFactory.createUser();

        when(userRepository.findById(USER_ID))
                .thenReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> mentorshipService.stopUserMentorship(USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}