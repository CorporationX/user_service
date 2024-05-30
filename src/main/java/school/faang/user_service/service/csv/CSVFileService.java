package school.faang.user_service.service.csv;

import com.json.student.Person;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface CSVFileService {

    void convertCsvFile(List<Person> persons);
    CompletableFuture<Void> processPersonAsync(Person person, ExecutorService executor);
    void setUpAndSaveToDB(User user);
    void assignCountryToUser(User user);
}
