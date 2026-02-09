package school.faang.user_service.service.career;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;

@Service
public interface CareerService {
    public CareerDto addCareer(long userId, CareerDto careerDto);

    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto);

    public CareerDto getById(long careerId);
}
