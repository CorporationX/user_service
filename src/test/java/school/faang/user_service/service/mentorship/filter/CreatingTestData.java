package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class CreatingTestData {
    static Stream<MentorshipRequest> createMentorshipRequestForTest() {
        MentorshipRequest mentorshipRequestFirst;
        MentorshipRequest mentorshipRequestSecond;
        User user = User.builder().id(1L).build();
        User user1 = User.builder().id(2L).build();

        mentorshipRequestFirst = MentorshipRequest.builder().description("test").status(RequestStatus.ACCEPTED).receiver(user).requester(user).build();
        mentorshipRequestSecond = MentorshipRequest.builder().description("no").status(RequestStatus.PENDING).receiver(user1).requester(user1).build();

        return Stream.of(mentorshipRequestFirst, mentorshipRequestSecond);

    }


    static RequestFilterDto createMentorshipRequestDtoForTest(MentorshipFilter mentorshipFilter) {
        switch (mentorshipFilter) {
            case DESCRIPTION -> {
                return RequestFilterDto.builder()
                        .description("test").build();
            }
            case STATUS -> {
                return RequestFilterDto.builder()
                        .status(RequestStatus.ACCEPTED).build();
            }
            case RECEIVER -> {
                return RequestFilterDto.builder()
                        .receiverId(1L).build();
            }
            case REQUESTER -> {
                return RequestFilterDto.builder()
                        .requesterId(1L).build();
            }
        }
        return null;
    }
}
