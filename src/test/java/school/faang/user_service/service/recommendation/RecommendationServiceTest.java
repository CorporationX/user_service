package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    RecommendationService service;
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    RecommendationValidator recommendationValidator;
    @Mock
    UserService userService;
    @Mock
    SkillOfferRepository skillOfferRepository;
    @Mock
    RecommendationMapper recommendationMapper;

    @Test
    public void testCreate() {
        long recommendationId = 1L;
        RecommendationDto dto = getRecommendationDto();
        User author = getUser();
        User receiver = getUser();
        when(recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()
        )).thenReturn(recommendationId);
        when(userService.findById(dto.getAuthorId())).thenReturn(author);
        when(userService.findById(dto.getReceiverId())).thenReturn(receiver);

        RecommendationDto result = service.create(dto);
        verify(recommendationValidator, times(1)).validateData(dto);
        verify(recommendationValidator, times(1)).checkDate(dto);
        verify(recommendationRepository, times(1)).create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()
        );
        dto.setId(recommendationId);
        Assertions.assertSame(dto, result);
    }

    @Test
    public void testUpdate() {
        RecommendationDto dto = getRecommendationDto();
        User author = getUser();
        User receiver = getUser();
        when(userService.findById(dto.getAuthorId())).thenReturn(author);
        when(userService.findById(dto.getReceiverId())).thenReturn(receiver);
        RecommendationDto result = service.update(dto);

        verify(recommendationValidator, times(1))
                .validateData(dto);
        verify(skillOfferRepository, times(1))
                .deleteAllByRecommendationId(dto.getId());
        verify(recommendationRepository, times(1))
                .update(dto.getAuthorId(), dto.getReceiverId(), dto.getContent());
        Assertions.assertSame(result, dto);
    }

    @Test
    public void testDelete() {
        long recommendationId = 1;
        service.delete(recommendationId);
        verify(recommendationRepository, times(1))
                .deleteById(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long receiverId = 1;
        int page = 1;
        int size = 1;

        when(recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        )).thenReturn(Page.empty());

        service.getAllUserRecommendations(receiverId, page, size);
        verify(recommendationRepository, times(1))
                .findAllByReceiverId(
                        receiverId,
                        PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
                );
        verify(recommendationMapper, times(1)).toListDto(Collections.emptyList());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        long authorId = 1;
        int page = 1;
        int size = 1;

        when(recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        )).thenReturn(Page.empty());

        service.getAllGivenRecommendations(authorId, page, size);
        verify(recommendationRepository, times(1))
                .findAllByAuthorId(
                        authorId,
                        PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
                );
        verify(recommendationMapper, times(1)).toListDto(Collections.emptyList());
    }

    private RecommendationDto getRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .authorId(2L)
                .receiverId(3L)
                .content("content")
                .skillOffers(Collections.emptyList())
                .build();
    }

    private User getUser() {
        return User.builder()
                .skills(Collections.emptyList())
                .build();
    }
}