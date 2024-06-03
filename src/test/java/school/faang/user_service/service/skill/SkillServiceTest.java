package school.faang.user_service.service.skill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private UserRepository userRepository;

    @Nested
    class PositiveTests {
        @DisplayName("should return user skills when such user exists")
        @Test
        void shouldReturnUserSkillsWhenSuchUserExists() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
            when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of());
            when(skillMapper.toDtoList(anyList())).thenReturn(List.of());

            assertDoesNotThrow(() -> skillService.getUserSkills(anyLong()));
            verify(skillMapper).toDtoList(anyList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when such user doesn't exist")
        @Test
        void shouldThrowExceptionWhenSuchUserDoesntExist() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> skillService.getUserSkills(anyLong()));

            verifyNoInteractions(skillRepository);
            assertEquals(NO_SUCH_USER_EXCEPTION.getMessage(), exception.getMessage());
        }
    }

}