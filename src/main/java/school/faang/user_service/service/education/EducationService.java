package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    private void checkData(EducationDto educationDto) {
        if (educationDto == null) {
            throw new DataValidationException("Данные об образовании не могут быть пустыми.");
        }

        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("Год должен быть меньше текущего.");
        }
    }

    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        checkData(educationDto);
        User user = userRepository.findById(userId).
                orElseThrow(() -> new DataValidationException("Пользователь не найден."));

        Education education = educationMapper.toEducationWithUser(educationDto, user);
        education = educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    @Transactional
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        checkData(educationDto);
        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new DataValidationException("Образование не найдено."));

        if (!education.getUser().getId().equals(userId)) {
            throw new DataValidationException("Нельзя обновлять данные другого пользователя");
        }

        educationMapper.updateEducationFromDto(educationDto, education);

        education = educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    public EducationDto getById(long educationId) {
        if (educationId <= 0) {
            throw new DataValidationException("Некорректный идентификатор образования.");
        }
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Образование не найдено."));
        return educationMapper.toEducationDto(education);
    }
}
