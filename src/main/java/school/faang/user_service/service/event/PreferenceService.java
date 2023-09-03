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
        return userPreferenceRepository.getUserPreference(id);
    }
}
