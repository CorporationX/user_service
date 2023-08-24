package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class RecommendationRequestMapperTest {
    @Spy
    RecommendationRequestMapperImpl mapper;
    RecommendationRequestDto requestDto;

    RecommendationRequest request;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .rejectionReason("reason")
                .recommendationId(1L)
                .skillsId(List.of(1L))
                .build();

        request = RecommendationRequest.builder()
                .id(1L)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(1L).build())
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .rejectionReason("reason")
                .recommendation(Recommendation.builder().id(1L).build())
                .build();
    }

    @Test
    void testToDto() {
        request.setSkills(List.of(SkillRequest.builder().skill(Skill.builder().id(1L).build()).build()));
        RecommendationRequestDto actual = mapper.toDto(request);
        assertEquals(requestDto, actual);
    }
    @Test
    void testToEntity() {
        RecommendationRequest actual = mapper.toEntity(requestDto);
        assertEquals(request, actual);
    }
}