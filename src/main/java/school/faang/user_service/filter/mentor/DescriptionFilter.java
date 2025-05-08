package school.faang.user_service.filter.mentor;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getDescription() != null && !requestFilterDto.getDescription().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> request, RequestFilterDto filterRequestDto) {
        return request.filter(reg -> reg.getDescription().contains(filterRequestDto.getDescription()));
    }
}
