package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerDto;

public interface CareerService {
    CareerDto addCareer(long userId, CareerDto careerDto);

    CareerDto updateCareer(long userId, long careerId, CareerDto careerDto);

    CareerDto getById(long careerId);
}
