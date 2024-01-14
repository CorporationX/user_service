package school.faang.user_service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.CoreMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestFilterTest {


    private MentorshipRequestFilter mentorshipRequestFilter;

    private List<MentorshipRequestFilter> mentorshipRequestFilters = new ArrayList<>();

    private MentorshipRequest mentorshipRequest;

    private RequestFilterDto requestFilterDto;


    @BeforeEach
    public void init() {
        mentorshipRequest = new MentorshipRequest();
        mentorshipRequestFilter = new MentorshipDescriptionFilter();
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescriptionFilter("Description 111");
        mentorshipRequestFilters.add(mentorshipRequestFilter);
    }

    @Test
    public void testCorrectDescription() {
        mentorshipRequest.setDescription("One TWO Description 111 THREE FOUR");
        Stream<MentorshipRequest> streams = Stream.of(mentorshipRequest);
        List<MentorshipRequestFilter> collect = mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .peek(filter -> filter.apply(streams, requestFilterDto))
                .collect(Collectors.toList());
        assertThat(collect.size(), is(1));
    }

    @Test
    public void testIncorrectDescription() {
        mentorshipRequest.setDescription("One TWO filter THREE FOUR");
        Stream<MentorshipRequest> streams = Stream.of(mentorshipRequest);
        List<MentorshipRequestFilter> collect = mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .peek(filter -> filter.apply(streams, requestFilterDto))
                .collect(Collectors.toList());
        assertThat(collect.size(), is(0));
    }
}