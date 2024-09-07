package school.faang.user_service.requestformentoring.helper.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FiltersRequest {

    public List<MentorshipRequest> filters(RequestFilterDto requestFilterDto, List<MentorshipRequest> menReq) {
        return addRequestFilter()
                .flatMap(filterRequest -> filterRequest.filterMenReq(requestFilterDto, menReq).stream())
                .toList();
    }

    private Stream<FilterRequest> addRequestFilter() {
        return Stream.of(
                new FilterByAuthorRequest(),
                new FilterByDescription(),
                new FilterByRecipientRequest(),
                new FilterByRequestStatus()
        );
    }


}
