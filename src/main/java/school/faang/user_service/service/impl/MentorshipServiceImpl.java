package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    @Override
    public List<UserDto> getMentees(long userId) {
        final User userById = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с userID %s не найден", userId)));
        return mentorshipMapper.toUserDto(userById.getMentees());
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        final User userById = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с userID %s не найден", userId)));
        return mentorshipMapper.toUserDto(userById.getMentors());
    }

    @Override
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ментор с ID %s не найден", mentorId)));

        User userMentee = mentor.getMentees()
                .stream()
                .filter(mentee -> mentee.getId() == menteeId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Менти с ID %s не найден у ментора с ID %s", menteeId, mentorId)
                ));

        mentor.getMentees().remove(userMentee);

        mentorshipRepository.save(mentor);
    }

    @Override
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Менти с ID %s не найден", menteeId)));

        User userMentor = mentee.getMentors()
                .stream()
                .filter(mentor -> mentor.getId() == mentorId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Ментор с ID %s не найден у менти с ID %s", mentorId, menteeId)
                ));

        mentee.getMentors().remove(userMentor);

        mentorshipRepository.save(mentee);
    }
}
