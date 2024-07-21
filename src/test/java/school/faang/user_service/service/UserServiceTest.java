package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Set;

import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
        @Mock
        private UserRepository repository;

        @Mock
        private UserMapper mapper;

        @InjectMocks
        private UserService service;

        @Test
        void getUserTest_whenException(){
            Long userId = null;
                assertThrows(RuntimeException.class ,() -> service.getUser(userId));
        }

        @Test
        void getUserTest(){
                Long userId = 1l;
                service.getUser(userId);
                Mockito.verify(repository, times(1))
                        .findById(userId);
        }

        @Test
        void getUsersByIdsTest(){
                User firstUser = new User();
                User secondUser = new User();
                User thirdUser = new User();
                firstUser.setId(1L);
                secondUser.setId(2L);
                thirdUser.setId(3L);
                List<Long> userList = List.of(firstUser.getId(), secondUser.getId(),thirdUser.getId());
                Iterable<Long> iterable = userList;
                List<UserDto> dtoList = userList.stream()
                        .map(userLong -> service.getUser(userLong))
                        .toList();
                assertEquals(dtoList , service.getUsersByIds(iterable));
        }
}
