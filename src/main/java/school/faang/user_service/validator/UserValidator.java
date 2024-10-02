package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository userRepository;

    public boolean validateUserBeforeSave(User studentToUser) {

        //ToDo В таблице user поля Username, Email, Phone имеют тип unique key.
        //ToDo Поэтому если у сохраняемого юзера есть совпадение по имени, эл.адресу или номеру телефона с юзерами,
        //ToDo которые содержатся в базе данных, то новый юзер не будет сохранен, а в лог выйдет сообщение.
        //ToDo Но ошибку я не бросаю, так как операция прервется и следующие юзеры не сохранятся

        List<User> resultUsers = userRepository.findByUsernameOrEmailOrPhone(
                studentToUser.getUsername(),
                studentToUser.getEmail(),
                studentToUser.getPhone());

        boolean usersIsExists = !resultUsers.isEmpty();

        if (usersIsExists) {
            for (User resultUser : resultUsers) {
                if (studentToUser.getUsername().equals(resultUser.getUsername())) {
                    log.warn("User with username {} already exists", studentToUser.getUsername());
                } else if (studentToUser.getEmail().equals(resultUser.getEmail())) {
                    log.warn("User with email {} already exists", studentToUser.getEmail());
                } else if (studentToUser.getPhone().equals(resultUser.getPhone())) {
                    log.warn("User with phone number {} already exists", studentToUser.getPhone());
                }
            }
        }
        return usersIsExists;
    }
}
