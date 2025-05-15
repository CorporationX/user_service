package school.faang.user_service.utils;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Utils {
    public String format(final String messagePattern, final Object arg1) {
        return new FormattedMessage(messagePattern, arg1).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object arg1, final Object arg2) {
        return new FormattedMessage(messagePattern, arg1, arg2).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object... arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }
}
