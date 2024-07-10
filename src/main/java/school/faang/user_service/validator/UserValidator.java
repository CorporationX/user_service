package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private static final long MAX_PIC_SIZE = 5_242_880L;
    private final UserRepository userRepository;

    public void checkUserInDB(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("The user with this id was not found: {}", userId);
            throw new DataValidationException("The user with this id was not found: " + userId);
        }
    }

    public void validateUserNotExists(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            log.error("The user with this username already exists id: {}", user.getUsername());
            throw new DataValidationException("The user with this username already exists id: " + user.getUsername());
        }
        if (userRepository.existsUserByEmail(user.getEmail())) {
            log.error("The user with this email already exists id: {}", user.getEmail());
            throw new DataValidationException("The user with this email already exists id: " + user.getEmail());
        }
        if (userRepository.existsUserByPhone(user.getPhone())) {
            log.error("The user with this phone already exists id: {}", user.getPhone());
            throw new DataValidationException("The user with this phone already exists id: " + user.getPhone());
        }
    }

    public void validateCsvFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("Received empty csv file");
            throw new DataValidationException("The file can't be empty");
        }
    }

    public void checkMaxSizePic(MultipartFile file) {
        if (file.getSize() > MAX_PIC_SIZE) {
            log.error("Размер файла (" + file.getSize() + ") превышает максимальный");
            throw new DataValidationException("Превышен максимальный размер файла");
        }
    }

    public void checkExistPicId(User user) {
        if (user.getUserProfilePic() == null || user.getUserProfilePic().getFileId() == null) {
            log.error("У пользователя c id: " + user.getId() + " отсутствует аватар");

            throw new DataValidationException("У пользователя c id: " + user.getId() + " отсутствует аватар");
        }
    }
}