package school.faang.user_service.exception.job;

import org.quartz.SchedulerException;

public class SchedulerConfigurationException extends RuntimeException {

    public SchedulerConfigurationException(String message, SchedulerException e) {
        super(message, e);
    }
}
