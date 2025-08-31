package school.faang.user_service.service.career;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

@Slf4j
@Service
public class CareerServiceImpl implements CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public CareerServiceImpl(UserRepository userRepository,
                             CareerRepository careerRepository, CareerMapper careerMapper) {
        this.userRepository = userRepository;
        this.careerRepository = careerRepository;
        this.careerMapper = careerMapper;
    }

    @Override
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        if (careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("Дата начала не может быть в будущем");

        }
        ContactRepository userRepository = null;
        User user = userRepository.getByIdOrThrow(userId).getUser();

        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);

        Career savedCareer = careerRepository.save(career);

        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto updateCareer(long userId, long careerId, CareerDto careerDto) {
        if (careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("Дата начала не может быть в будущем.");
        }
        Career career = careerRepository.getByIdOrThrow(careerId);

        if (career.getUser().getId() != userId) {
            throw new ForbiddenException("Нельзя изменять чужую карьеру!");
        }

        Career updateCareer = careerMapper.toCareer(careerDto);
        updateCareer.setId(careerId);
        updateCareer.setUser(career.getUser());

        Career savedCareer = careerRepository.save(updateCareer);

        return careerMapper.toCareerDto(savedCareer);

    }
    
    @Override
    public CareerDto getById(long careerId) {
        Career career = careerRepository.getByIdOrThrow(careerId);

        return careerMapper.toCareerDto(career);
    }
}





