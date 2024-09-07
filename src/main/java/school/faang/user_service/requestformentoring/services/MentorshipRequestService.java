package school.faang.user_service.requestformentoring.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.requestformentoring.helper.exeptions.ErrorUserAlreadyExists;
import school.faang.user_service.requestformentoring.helper.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.requestformentoring.helper.filters.FiltersRequest;
import school.faang.user_service.requestformentoring.helper.validation.ValidationDb;
import school.faang.user_service.requestformentoring.helper.validation.ValidationUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final int MONTHS_REQUEST_MENTORSHIP_REQUEST = 3;
    private final MentorshipRequestRepository menReqRepository;
    private final ValidationDb validationDb;
    private final ValidationUser validationUser;
    private final MentorshipRequestMapper menReqMapper;
    private final FiltersRequest filtersRequest;

    public void requestMentorship(MentorshipRequestDto menReqDto) {
        validRequestMentorship(menReqDto);
        menReqRepository.create(menReqDto.getRequesterId(),
                menReqDto.getReceiverId(),
                menReqDto.getDescription());
    }


    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        validationUser.checkOneTypeToNull(filter);
        return filtersRequest.filters(filter, menReqRepository.findAll()).stream()
                .map(menReqMapper::toDto)
                .toList();
    }

    public void acceptRequest(long id) {
        validationUser.checkOneTypeToNull(id);
        validationDb.checkAvailabilityMentorshipRequestDb(id);
        Optional<MentorshipRequest> menReq = menReqRepository.findMentorshipRequestById(id);

        checkRequesterToReceiver(menReq.get().getRequester(), menReq.get().getReceiver());

        menReq.get().getReceiver().getMentors().add(menReq.get().getRequester());
        menReq.get().setStatus(RequestStatus.ACCEPTED);

        menReqRepository.save(menReq.get());
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        validationUser.checkOneTypeToNull(id);
        validationDb.checkAvailabilityMentorshipRequestDb(id);
        Optional<MentorshipRequest> menReq = menReqRepository.findMentorshipRequestById(id);

        menReq.get().setStatus(RequestStatus.REJECTED);
        menReq.get().setRejectionReason(rejection.getReason());
        menReqRepository.save(menReq.get());
    }


    private void checkRequesterToReceiver(User receiver, User requester) {
        if (receiver.getMentors().contains(requester)) {
            throw new ErrorUserAlreadyExists(requester.getUsername());
        }
    }

    private void validRequestMentorship(MentorshipRequestDto menReqDto) {
        validationUser.checkOneTypeToNull(menReqDto);
        validationDb.checkingAvailabilityUsersDb(menReqDto.getRequesterId());
        validationDb.checkingAvailabilityUsersDb(menReqDto.getReceiverId());
        validationUser.checkUserEqualsUser(menReqDto.getRequesterId(), menReqDto.getReceiverId());

        validationDb.checksLastRequestMentoring(menReqDto.getRequesterId(),
                menReqDto.getReceiverId(),
                MONTHS_REQUEST_MENTORSHIP_REQUEST);
    }

}
