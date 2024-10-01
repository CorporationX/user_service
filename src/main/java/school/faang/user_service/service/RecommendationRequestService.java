package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.*;
import school.faang.user_service.filter.*;
import school.faang.user_service.repository.RequestFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.filter.RequesterIdFilter;
import school.faang.user_service.repository.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public interface RecommendationRequestService {
    RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto);
    RecommendationRequestDto getRequest(long id);
    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);
    RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);
}