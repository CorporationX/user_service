package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.adapter.CareerRepositoryAdapter;
import school.faang.user_service.adapter.UserRepositoryAdapter;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.validator.CareerValidator;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CareerService {

    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final CareerValidator careerValidator;
    private final CareerRepositoryAdapter careerRepositoryAdapter;
    private final UserRepositoryAdapter userRepositoryAdapter;

    public CareerDto addCareer(long userId, CareerDto careerDto) {

        careerValidator.validate(careerDto);
        User user = userRepositoryAdapter.getUserById(userId);
        Career career = Career.builder()
                .dateFrom(careerDto.getFrom())
                .dateTo(careerDto.getTo())
                .company(careerDto.getCompany())
                .position(careerDto.getPosition())
                .user(user)
                .build();
     return careerMapper.toCareerDto(careerRepository.save(career));
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {

        careerValidator.validate(careerDto);
        Career career = careerRepositoryAdapter.getCareerById(careerDto.getId());
        if(!Objects.equals(career.getUser().getId(), userId)) {
            throw new DataValidationException("Users do not match");
        }
        Career updatedCareer = career.toBuilder()
                .dateFrom(careerDto.getFrom())
                .dateTo(careerDto.getTo())
                .company(careerDto.getCompany())
                .position(careerDto.getPosition())
                .build();
        return careerMapper.toCareerDto(careerRepository.save(updatedCareer));
    }

    public CareerDto getById(long careerId) {

        Career career = careerRepositoryAdapter.getCareerById(careerId);
        return careerMapper.toCareerDto(career);
    }
}
