package school.faang.user_service.converter.component;

import com.json.student.PreviousEducation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetPreviousEducation {
    public List<PreviousEducation> setPreviousEducation(String degree, String institution, int completionYear) {
        List<PreviousEducation> previousEducations = new ArrayList<>();
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setDegree(degree);
        previousEducation.setInstitution(institution);
        previousEducation.setCompletionYear(completionYear);
        previousEducations.add(previousEducation);

        return previousEducations;
    }
}
