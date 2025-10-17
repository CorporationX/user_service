package school.faang.user_service.service.education;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import static java.time.Year.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Transactional
    @Override
    public EducationDto addEducation(long userId, CreateEducationDto dto) {
        log.debug("addEducation userId={}, dto={}", userId, dto);
        int currentYear = now().getValue();
        if (dto.yearFrom() == null || dto.yearFrom() > currentYear) {
            throw new DataValidationException("yearFrom=" + dto.yearFrom()
                    + ", currentYear=" + currentYear);
        }
        if (dto.yearTo() != null && dto.yearFrom() != null && dto.yearTo() < dto.yearFrom()) {
            throw new DataValidationException("Год окончания не может быть раньше начала");
        }
        if (dto.institution() == null || dto.institution().isBlank()) {
            throw new DataValidationException("Не может быть пустым");
        }
        User user = userRepository.getByIdOrThrow(userId);
        Education entity = educationMapper.toEducation(dto);
        entity.setUser(user);
        Education saved = educationRepository.save(entity);
        return educationMapper.toEducationDto(saved);
    }

    @Transactional
    @Override
    public EducationDto updateEducation(long userId, long educationId, UpdateEducationDto dto) {
        log.debug("updateEducation userId={}, educationId={}, dto={}", userId, educationId, dto);
        int currentYear = now().getValue();
        if (dto.yearFrom() != null && dto.yearFrom() > currentYear) {
            throw new DataValidationException("Дата поступления не может быть больше текущего года");
        }
        Education existing = educationRepository.findById(educationId)
                .orElseThrow(() -> new DataValidationException("Данных об образовании не найдено"));
        if (existing.getUser().getId() != userId) {
            throw new ForbiddenException("Нельзя редактировать чужие данные");
        }
        if (dto.yearFrom() != null && dto.yearTo() != null && dto.yearTo() < dto.yearFrom()) {
            throw new DataValidationException("Введите корректные года учебы");
        }
        educationMapper.update(existing, dto);
        Education saved = educationRepository.save(existing);
        return educationMapper.toEducationDto(saved);
    }

    @Override
    public EducationDto getById(long educationId) {
        log.debug("getById id={}", educationId);
        Education entity = educationRepository.getByIdOrThrow(educationId);
        return educationMapper.toEducationDto(entity);
    }
}
