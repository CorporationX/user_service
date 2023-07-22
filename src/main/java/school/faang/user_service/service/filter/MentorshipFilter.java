package school.faang.user_service.service.filter;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MentorshipFilter {
    private MentorshipRequestMapper mentorshipRequestMapper;
    public List<MentorshipRequestDto> filterRequests(List<MentorshipRequest> mentorshipRequests, RequestFilterDto filter){
        Stream<MentorshipRequest> filteredStream = mentorshipRequests.stream();

        if (filter.getDescription() != null) {
            filteredStream = filteredStream.filter(request -> request.getDescription().contains(filter.getDescription()));
        }

        if (filter.getRequesterId() != 0) {
            filteredStream = filteredStream.filter(request -> request.getRequester().getId() == filter.getRequesterId());
        }

        if (filter.getReceiverId() != 0) {
            filteredStream = filteredStream.filter(request -> request.getReceiver().getId() == filter.getReceiverId());
        }

        if (filter.getStatus() != null) {
            filteredStream = filteredStream.filter(request -> request.getStatus() == filter.getStatus());
        }

        return filteredStream.map((x)->mentorshipRequestMapper.toDto(x)).collect(Collectors.toList());
    }
}
