package school.faang.user_service.service.user;

import com.json.student.Person;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.csvconverter.ConverterCsvToPerson;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ConverterCsvToPerson csvToPerson;

    public void saveUsersFromFile(MultipartFile file) {
        if (file.isEmpty()) {
            String msg = "File name:%s is empty";
            log.error(String.format(msg, file.getOriginalFilename()));
            throw new DataValidationException(String.format(msg, file.getOriginalFilename()));
        }
        List<Person> persons = csvToPerson.convertorToPerson(file);

    }

}
