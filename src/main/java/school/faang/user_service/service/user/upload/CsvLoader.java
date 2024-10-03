package school.faang.user_service.service.user.upload;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.FileUploadedException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.pojo.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvLoader {

    private final ObjectReader objectReader;
    private final UserMapper userMapper;

    @Async("taskExecutor")
    public CompletableFuture<List<User>> parseCsvToUsers(MultipartFile file) {
        List<User> users;
        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<Person> mappingIterator = objectReader.readValues(inputStream);
            users = mappingIterator.readAll().stream()
                    .map(userMapper::toUserFromPerson)
                    .toList();
            log.info("{} : process {} persons", Thread.currentThread().getName(), users.size());
        } catch (IOException e) {
            throw new FileUploadedException("Fail while uploading the file - " + file.getOriginalFilename(), e);
        }
        return CompletableFuture.completedFuture(users);
    }
}
