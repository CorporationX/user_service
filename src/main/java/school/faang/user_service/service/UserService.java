package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Autowired
    private ExecutorService pull;

    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User with id " + id + " not found");
        });
        log.info("Return user with id: {}", foundUser.getId());
        return userMapper.toUserDto(foundUser);
    }

    public List<UserDto> getUsersByIds(List<Long> usersIds) {
        List<User> users = userRepository.findAllById(usersIds);
        log.info("Return list of users: {}", users);
        return userMapper.toUserListDto(users);
    }

    public void registerAnArrayOfUser(InputStream stream) throws IOException {



        //CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> );
      //  pull = Executors.newFixedThreadPool(100);

//
//        byte[] bytes = new byte[1024 * 10];
//        while (stream.read(bytes) != -1) {
//
//        }

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String finalLine = line;
//               Future<String> future = pull.submit(() -> get(finalLine));
//
//            }
//        } catch (IOException e) {
//
//        }
    }

    private String get(String line) {
        line.length();
        userRepository.saveAll(new ArrayList<>());
        return "";
    }
}
