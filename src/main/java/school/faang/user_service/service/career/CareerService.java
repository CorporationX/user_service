package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository сareerRepository;
    private final CareerMapper сareerMapper;

    public boolean addCareer(Long userId, CareerDto careerDto) {
        LocalDateTime now = LocalDateTime.now();
        if (careerDto.getFrom().isAfter(now)) {
            throw new DataValidationException("incorrect date");
        }
        Optional<User> userOptional = userRepository.findById(careerDto.getId());
        if (userOptional.isEmpty()) {
            throw new DataValidationException("user not found");
            }
        Career career = сareerMapper.toCareer(careerDto);
        User user = userOptional.get();
        career.setUser(user);
        сareerRepository.save(career);
        return сareerMapper.toCareerDto(career);
    }
}
