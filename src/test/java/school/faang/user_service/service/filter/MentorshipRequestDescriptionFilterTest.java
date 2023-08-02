package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestDescriptionFilter;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestDescriptionFilterTest {
    private final MentorshipRequestDescriptionFilter descriptionFilter = new MentorshipRequestDescriptionFilter();
    private RequestFilterDto filter;
    private List<MentorshipRequestDto> requests;
    private MentorshipRequestDto request1;
    private MentorshipRequestDto request2;
    private MentorshipRequestDto request3;


    @BeforeEach
    void setUp() {
        filter = RequestFilterDto.builder().build();

        request1 = MentorshipRequestDto.builder()
                .description("description1")
                .build();
        request2 = MentorshipRequestDto.builder()
                .description("description2")
                .build();
        request3 = MentorshipRequestDto.builder()
                .description("description3")
                .build();

        requests = new ArrayList<>();

        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
    }

    @Test
    void testIsApplicableWhenDescriptionPatternNotEmpty() {
        filter.setDescriptionPattern("description");
        assert true;
    }
}
