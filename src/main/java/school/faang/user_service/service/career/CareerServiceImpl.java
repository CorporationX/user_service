package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
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
    public CareerDto addCareer(Long userId, CreateCareerDto careerDto) {
        validateCareerDates(careerDto.from(), careerDto.to());
        validateCompany(careerDto.company());
        validatePosition(careerDto.position());

        User user = userRepository.getByIdOrThrow(userId);

        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto updateCareer(Long userId, Long careerId, UpdateCareerDto careerDto) {

        validateCareerDates(careerDto.from(), careerDto.to());
        validateCompany(careerDto.company());
        validatePosition(careerDto.position());

        Career newCareer = careerRepository.getByIdOrThrow(careerId);

        if (!newCareer.getUser().getId().equals(userId)) {
            log.warn("User {} tried to update career {} that belongs to user {}",
                    userId, careerId, newCareer.getUser().getId());
            throw new ForbiddenException("You can only update your own career data");
        }

        Career careerToUpdate = careerMapper.toCareer(careerDto);
        careerToUpdate.setUser(newCareer.getUser());
        careerToUpdate.setId(careerId);
        Career savedCareer = careerRepository.save(careerToUpdate);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto getById(Long careerId) {
        Career career = careerRepository.getByIdOrThrow(careerId);
        log.info("Retrieved career with id: {}", careerId);

        return careerMapper.toCareerDto(career);
    }

    private void validateCareerDates(LocalDate from, LocalDate to) {
        validateStartDate(from);
        validateDateRange(from, to);
    }

    private void validateStartDate(LocalDate from) {
        if (from == null) {
            log.warn("Start date cannot be null");
            throw new DataValidationException("Start date is required");
        }

        if (from.isAfter(LocalDate.now())
                || from.isEqual(LocalDate.now())) {
            log.warn("Start date cannot be in the future. Provided date: {}", from);
            throw new DataValidationException("Start date cannot be in the future");
        }
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (to != null && to.isBefore(from)) {
            log.warn("End date cannot be before start date. Start: {}, End: {}",
                    from, to);
            throw new DataValidationException("End date cannot be before start date");
        }
    }

    private void validateCompany(String company) {
        if (company == null || company.isBlank()) {
            log.warn("Company cannot be null or empty");
            throw new DataValidationException("Company is required");
        }
    }

    private void validatePosition(String position) {
        if (position == null || position.isBlank()) {
            log.warn("Position cannot be null or empty");
            throw new DataValidationException("Position is required");
        }
    }
}
