package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        validateFromDateBeforeNow(careerDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found.", userId)));
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        validateFromDateBeforeNow(careerDto);
        Career existingCareer = careerRepository.findById(careerDto.id()).orElseThrow(() ->
                new EntityNotFoundException("Career not found."));
        if (existingCareer.getUser().getId() != userId) {
            throw new DataValidationException("User cannot update someone else's career.");
        }
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(existingCareer.getUser());
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId).orElseThrow(() ->
                new EntityNotFoundException("Career not found."));
        return careerMapper.toCareerDto(career);
    }

    private void validateFromDateBeforeNow(CareerDto careerDto) {
        if (!careerDto.from().isBefore(LocalDate.now())) {
            throw new DataValidationException("Career start date must be in the past. Current value: %s"
                    .formatted(careerDto.from()));
        }
    }
}

