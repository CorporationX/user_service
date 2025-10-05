package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;

public interface CareerService {

    CareerDto addCareer(long userId, CreateCareerDto careerDto);

    CareerDto updateCareer(long userId, long careerId, UpdateCareerDto careerDto);

    CareerDto getById(long careerId);
}
