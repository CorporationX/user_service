package school.faang.user_service.mapper.recomendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class RecommendationRequestMapperTest {
//    private RecommendationRequestDto recommendationRequestDto = createDto();
//    private RecommendationRequest recommendationRequestEntity = createEntity();
//
//    private RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();


    @Test
    @DisplayName("Test successful mapToEntity")
    public void testSuccessfulMapToEntity() {

//        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
//        System.out.println(recommendationRequestEntity);
//        assertAll(
//                () -> assertEquals(recommendationRequestEntity.getId(), recommendationRequestDto.getId()),
//                () -> assertEquals(recommendationRequestEntity.getCreatedAt(), recommendationRequestEntity.getCreatedAt()),
//                () -> assertEquals(recommendationRequestEntity.getRequester().getId(), recommendationRequestDto.getRequesterId()),
//                () -> assertEquals(recommendationRequestEntity.getReceiver().getId(), recommendationRequestDto.getReceiverId()),
//                () -> assertEquals(recommendationRequestEntity.getStatus(), recommendationRequestDto.getStatus()),
//                () -> assertEquals(recommendationRequestEntity.getMessage(), recommendationRequestDto.getMessage())
//        );
//
//    }
//
//    @Test
//    @DisplayName("Test sussessful mapToDto")
//    public void testSussessfulMapToDto() {
//        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.mapToDto(recommendationRequestEntity);
//        assertAll(
//                ()->assertEquals(recommendationRequestDto.getMessage(),recommendationRequestEntity.getMessage())
//        );
//    }
//
//    private RecommendationRequestDto createDto() {
//        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
//        recommendationRequestDto.setId(1L);
//        recommendationRequestDto.setRequesterId(2L);
//        recommendationRequestDto.setReceiverId(5L);
//        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
//        recommendationRequestDto.setMessage("testMessage");
//        recommendationRequestDto.setStatus(RequestStatus.PENDING);
//        recommendationRequestDto.setSkills(List.of(new SkillRequest(), new SkillRequest()));
//        return recommendationRequestDto;
//    }
//
//    private RecommendationRequest createEntity() {
//        RecommendationRequest recommendationRequest = new RecommendationRequest();
//        recommendationRequest.setId(1L);
//        recommendationRequest.setCreatedAt(LocalDateTime.now());
//        recommendationRequest.setRequester(new User());
//        recommendationRequest.getRequester().setId(2L);
//        recommendationRequest.setReceiver(new User());
//        recommendationRequest.getReceiver().setId(5L);
//        recommendationRequest.setMessage("testMessage");
//        recommendationRequest.setStatus(RequestStatus.PENDING);
//        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
//        return recommendationRequest;
//    }
    }
}