package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestMapperTest {

    private final RecommendationRequestMapperImpl mapper = new RecommendationRequestMapperImpl();

    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final String MESSAGE = "Test message";
    private static final RequestStatus REQUEST_STATUS = RequestStatus.ACCEPTED;
    private static final long REQUESTER_ID = 10L;
    private static final long RECEIVER_ID = 5L;
    private static final LocalDateTime CREATED_AT = LocalDateTime
            .of(2024, 5, 15, 8, 30);
    private static final LocalDateTime UPDATED_AT = LocalDateTime
            .of(2024, 6, 26, 10, 40);
    private static final long SKILL_REQUEST_ID_ONE = 1L;
    private static final long SKILL_REQUEST_ID_TWO = 2L;
    private static final long SKILL_ID_ONE = 1L;
    private static final long SKILL_ID_TWO = 2L;
    private RecommendationRequest rq;
    private RecommendationRequestDto rqd;

    @BeforeEach
    void setUp() {
        rq = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .message(MESSAGE)
                .status(REQUEST_STATUS)
                .skills(List.of(SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_ONE)
                                .request(rq)
                                .skill(Skill.builder()
                                        .id(SKILL_ID_ONE)
                                        .build())
                                .build(),
                        SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_TWO)
                                .request(rq)
                                .skill(Skill.builder()
                                        .id(SKILL_ID_TWO)
                                        .build())
                                .build()))
                .requester(User.builder()
                        .id(REQUESTER_ID)
                        .build())
                .receiver(User.builder()
                        .id(RECEIVER_ID)
                        .build())
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();

        rqd = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .message(MESSAGE)
                .status(REQUEST_STATUS)
                .skillIds(List.of(SKILL_ID_ONE, SKILL_ID_TWO))
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    @Nested
    class ToEntity {

        @Test
        @DisplayName("If RecommendationRequestDto is null then pass it further")
        public void whenDtoNullParameterMappedThenPassItToEntity() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("On enter RecommendationRequestDto, on exit RecommendationRequest")
        public void whenDtoNotNullThenReturnEntity() {
            RecommendationRequest requestResult = mapper.toEntity(rqd);

            assertEquals(rqd.getId(), requestResult.getId());
            assertEquals(rqd.getMessage(), requestResult.getMessage());
            assertEquals(rqd.getStatus(), requestResult.getStatus());
            assertNull(requestResult.getRequester().getId());
            assertNull(requestResult.getReceiver().getId());
            assertEquals(rqd.getCreatedAt(), requestResult.getCreatedAt());
            assertEquals(rqd.getUpdatedAt(), requestResult.getUpdatedAt());
        }
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("If RecommendationRequest is null then pass it further")
        public void whenEntityNullParameterMappedThenPassItToDto() {
            assertNull(mapper.toDto(null));
        }

        @Test
        @DisplayName("On enter RecommendationRequest, on exit RecommendationRequestDto")
        public void whenEntityNotNullThenReturnDto() {
            RecommendationRequestDto dtoResult = mapper.toDto(rq);

            assertEquals(rq.getId(), dtoResult.getId());
            assertEquals(rq.getMessage(), dtoResult.getMessage());
            assertEquals(rq.getStatus(), dtoResult.getStatus());
            assertEquals(rq.getSkills().size(), dtoResult.getSkillIds().size());
            assertEquals(rq.getRequester().getId(), dtoResult.getRequesterId());
            assertEquals(rq.getReceiver().getId(), dtoResult.getReceiverId());
            assertEquals(rq.getCreatedAt(), dtoResult.getCreatedAt());
            assertEquals(rq.getUpdatedAt(), dtoResult.getUpdatedAt());
        }
    }
}
