package school.faang.user_service.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Career;
import school.faang.user_service.repository.CareerRepository;

@RequiredArgsConstructor
@Component
public class CareerRepositoryAdapter {

    private final CareerRepository careerRepository;

    public Career getCareerById (long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Career with id " + careerId + " not found"));
    }
}
