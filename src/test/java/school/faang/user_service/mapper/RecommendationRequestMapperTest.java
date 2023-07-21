package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestMapperTest {

    @Spy
    RecommendationRequestMapperImpl mapper;

    RecommendationRequestDto requestDto;

    RecommendationRequest request;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillsId(List.of(1L))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(now.minusMonths(7))
                .updatedAt(now)
                .build();

        request = RecommendationRequest.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(SkillRequest.builder().id(1L).build()))
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(1L).build())
                .createdAt(now.minusMonths(7))
                .updatedAt(now)
                .build();
    }

    @Test
    void testToDto() {
        RecommendationRequestDto actual = mapper.toDto(request);
        assertEquals(requestDto, actual);
    }

    @Test
    void testToEntity() {
        RecommendationRequest actual = mapper.toEntity(requestDto);
        assertEquals(request, actual);
    }
}