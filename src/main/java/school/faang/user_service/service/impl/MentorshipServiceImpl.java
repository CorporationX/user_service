package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.repository.MentorshipRepository;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.service.MentorshipService;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    @Override
    public void stopMentorship(User mentor) {
        log.info("Остановка наставничества для пользователя с ID: {}", mentor.getId());
        List<User> mentees = mentor.getMentees();
        if (mentees != null) {
            for (User mentee : mentees) {
                log.info("Обработка ученика с ID: {}", mentee.getId());
                if (mentee.getMentors() != null) {
                    mentee.getMentors().remove(mentor);
                    log.info("Ученик с ID: {} больше не имеет наставника с ID: {}", mentee.getId(), mentor.getId());
                }
                transferGoalsToMentee(mentor, mentee);
                mentorshipRepository.save(mentee);
                log.info("Изменения для ученика с ID: {} были сохранены", mentee.getId());
            }
        } else {
            log.warn("У наставника с ID: {} нет подопечных", mentor.getId());
        }
    }

    private void transferGoalsToMentee(User mentor, User mentee) {
        log.info("Перенос целей от наставника с ID: {} к ученику с ID: {}", mentor.getId(), mentee.getId());
        if (mentee.getSetGoals() != null) {
            for (Goal goal : mentee.getSetGoals()) {
                if (goal.getMentor() != null && goal.getMentor().equals(mentor)) {
                    goal.setMentor(mentee);
                    mentee.getGoals().add(goal);
                    log.info("Цель с ID: {} была перенесена от наставника с ID: {} к ученику с ID: {}", goal.getId(), mentor.getId(), mentee.getId());
                }
            }
            if (!mentee.getGoals().isEmpty()) {
                goalRepository.saveAll(mentee.getGoals());
                log.info("Все цели ученика с ID: {} были сохранены", mentee.getId());
            } else {
                log.info("У ученика с ID: {} нет целей для переноса", mentee.getId());
            }
        }
    }
}