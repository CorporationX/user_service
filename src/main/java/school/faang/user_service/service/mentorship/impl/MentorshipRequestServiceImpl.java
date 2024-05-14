package school.faang.user_service.service.mentorship.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestFilterService mentorshipRequestFilterService;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final UserValidator userValidator;


    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(Long requesterId, Long receiverId, MentorshipRequestDto dto) {
        var userList = userValidator.validateUsersExistence(List.of(requesterId, receiverId));
        mentorshipRequestValidator.validateMentorshipRequest(dto);

        MentorshipRequest entity = mentorshipRequestMapper.toEntity(dto);
        User requesterEntity = getUser(userList, requesterId);
        User receiverEntity = getUser(userList, receiverId);
        entity.setRequester(requesterEntity);
        entity.setReceiver(receiverEntity);
        MentorshipRequest entityFromDB = mentorshipRequestRepository.save(entity);

        return mentorshipRequestMapper.toDto(entityFromDB);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        var entitiesStream = mentorshipRequestRepository.findAll().stream();

        entitiesStream = mentorshipRequestFilterService.apply(entitiesStream, requestFilterDto);

        return mentorshipRequestMapper.toDtoList(entitiesStream.toList());
    }

    @Override
    @Transactional
    public MentorshipRequestDto acceptRequest(Long id) {
        var entity = mentorshipRequestValidator.validateMentorshipRequestExistence(id);

        addMentor(entity);
        entity.setStatus(RequestStatus.ACCEPTED);
        entity = mentorshipRequestRepository.save(entity);

        return mentorshipRequestMapper.toDto(entity);
    }

    @Override
    @Transactional
    public MentorshipRequestDto rejectRequest(Long id, RejectionDto rejection) {
        var entity = mentorshipRequestValidator.validateMentorshipRequestExistence(id);

        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.getRejectionReason());
        entity = mentorshipRequestRepository.save(entity);

        return mentorshipRequestMapper.toDto(entity);
    }

    private User getUser(List<User> list, Long userId) {
        return list.stream()
                .filter(user -> user.getId() == userId)
                .findAny()
                .orElseThrow();
    }

    private void addMentor(MentorshipRequest entity) {
        mentorshipRequestValidator.validateMentor(entity);
        var requester = entity.getRequester();
        var receiver = entity.getReceiver();
        requester.getMentors().add(receiver);

        userRepository.save(requester);
    }
}
