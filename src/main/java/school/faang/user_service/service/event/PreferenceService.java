package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserPreferenceRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    public List<Long> getPreference(long id) {
        List<Long> preferences = userPreferenceRepository.getUserPreference(id);
        preferences.forEach(System.out::println);
        return preferences;
    }
}
