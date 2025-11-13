package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;
import school.faang.user_service.dto.user.education.UpdateEducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.EducationRepository;

import java.time.Year;

@RequiredArgsConstructor
@Service
@Slf4j

public class EducationServiceImpl implements EducationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;


    @Override
    public EducationDto addEducation(long userId, CreateEducationDto educationDto ) {
        log.info("Adding education for userId={} with institution={}", userId, educationDto);
        int currentYear = Year.now().getValue();

        if (educationDto.yearFrom() > currentYear) {
            throw new DataValidationException("YearFrom cannot be greater than the current year");
        }
        validateEducationDto(educationDto);
        User user = userRepository.getByIdOrThrow(userId);
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        education = educationRepository.save(education);

        return educationMapper.toEducationDto(education);
    }

    @Override
    public EducationDto updateEducation(long userId, long educationId, UpdateEducationDto educationDto) {
        int currentYear = Year.now().getValue();
        if (educationDto.yearFrom() != null && educationDto.yearFrom() > currentYear) {
            throw new DataValidationException("YearFrom cannot be greater than the current year");
        }

        //validateEducationDto(educationDto);

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id " + educationId));

        if (!education.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to update this education");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto);

        updatedEducation.setUser(education.getUser());

        updatedEducation.setId(education.getId());

        Education savedEducation = educationRepository.save(updatedEducation);

        return educationMapper.toEducationDto(savedEducation);

    }

    @Override
    public EducationDto getById(long educationId) {
        Education education = educationRepository.getByIdOrThrow(educationId);

        return educationMapper.toEducationDto(education);
    }

    public void validateEducationDto(CreateEducationDto educationDto) {
        if (educationDto.educationLevel() == null || educationDto.institution().isBlank()) {
            throw new DataValidationException("Institution cannot be null or empty");
        }

        if (educationDto.yearFrom() == null) {
            throw new DataValidationException("YearFrom is required");
        }

        int currentYear = Year.now().getValue();

        if (educationDto.yearFrom() > currentYear) {
            throw new DataValidationException("YearFrom cannot be greater than the current year");
        }
    }
}

