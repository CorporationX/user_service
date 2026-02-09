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

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerServiceImpl implements CareerService {

    private final UserRepository userRepository;

    private final CareerRepository careerRepository;

    private final CareerMapper careerMapper;

    @Override
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Error");
        }
        Career career = careerMapper.toEntity(careerDto);
        User user = userRepository.getByIdOrThrow(userId);
        career.setUser(user);
        careerRepository.save(career);
        log.info("Career successfully added!");
        return careerMapper.toDto(career);
    }

    @Override
    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Error");
        }
        Career newCareer = careerRepository.getByIdOrThrow(careerId);
        if (!(newCareer.getUser().getId() == userId)) {
            throw new ForbiddenException("Error");
        }
        Career career = careerMapper.toEntity(careerDto);
        career.setUser(newCareer.getUser());
        careerRepository.save(career);
        log.info("Career successfully updated!");
        return careerMapper.toDto(career);
    }

    @Override
    public CareerDto getById(long careerId) {
        return careerMapper.toDto(careerRepository.getByIdOrThrow(careerId));
    }
}
