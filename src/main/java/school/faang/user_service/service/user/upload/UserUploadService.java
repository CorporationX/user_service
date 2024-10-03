package school.faang.user_service.service.user.upload;

import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserUploadService {

    CompletableFuture<Map<String, Country>> getAllCountries();

    CompletableFuture<List<User>> setCountriesToUsers(List<User> users, Map<String, Country> countries);

    void saveUsers(List<User> users);
}
