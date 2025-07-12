package school.faang.user_service.education_addition;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Education;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class  EducationValidationServiceImpl implements EducationValidationService {

    @Override
    public void validateOnAdd(long userId, EducationDto educationDto) throws DataValidationException {
        if (userId <= 0) {
            throw new DataValidationException("User ID must be positive");
        }

        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("YearFrom must be less than current year");
        }
    }

    @Override
    public void validateOnUpdate(long userId, Education education, EducationDto educationDto) throws DataValidationException {
        if (educationDto.getYearFrom() == null || educationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("YearFrom must be less than current year");
        }

        if (education == null) {
            throw new EntityNotFoundException("Education with id=%d not found");
        }


        if (!education.getUser().getId().equals(userId)) {
            throw new DataValidationException("Can't update someone else's data");
        }
    }

    @Override
    public void validateOnGetById(long educationId) throws DataValidationException {
        if (educationId <= 0) {
            throw new DataValidationException("Education ID must be positive");
        }
    }

}