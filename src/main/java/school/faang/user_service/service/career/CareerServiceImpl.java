package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    @Override
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        if (careerDto.from().isAfter(LocalDate.now())) {
            throw new DataValidationException("From date should not be later than today!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        career = careerRepository.save(career);
        log.info("Career {} has been created", career.getId());
        return careerMapper.toCareerDto(career);
    }

    @Override
    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto) {
        if (careerDto.from().isAfter(LocalDate.now())) {
            throw new DataValidationException("From date should not be later than today!");
        }
        Career career = careerRepository.getByIdOrThrow(careerId);
        User user = career.getUser();
        if (userId != user.getId()) {
            throw new ForbiddenException("ID mismatch: updating this career's details is not allowed for this user.");
        }
        career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        careerRepository.save(career);
        log.info("Career {} has been updated", career.getId());
        return careerMapper.toCareerDto(career);
    }

    @Override
    public CareerDto getById(long careerId) {
        Career career = careerRepository.getByIdOrThrow(careerId);
        return careerMapper.toCareerDto(career);
    }
}