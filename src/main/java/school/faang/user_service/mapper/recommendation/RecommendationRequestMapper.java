package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "receiverIdToReceiver")
    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "requesterIdToRequester")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto, @Context UserRepository userRepository);

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest, @Context UserRepository userRepository);

    @Named("receiverIdToReceiver")
    default User receiverIdToReceiver(Long receiverId, @Context UserRepository userRepository) {
        return userRepository.findById(receiverId).get();
    }

    @Named("requesterIdToRequester")
    default User requesterIdToRequester(Long requesterId, @Context UserRepository userRepository) {
        return userRepository.findById(requesterId).get();
    }
}
