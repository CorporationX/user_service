package school.faang.user_service.config.redis.user_ban;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.util.Pair;
import school.faang.user_service.listener.user_ban.UserBanEventListener;

@Configuration
public class UserBanEventConfig {

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanEventListenerTopic;

    @Bean
    public MessageListenerAdapter userBanEventListenerAdapter(UserBanEventListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public Pair<Topic, MessageListenerAdapter> userBanEventListenerAdapterPair(
            @Qualifier("userBanEventListenerAdapter") MessageListenerAdapter messageListenerAdapter
    ) {
        return Pair.of(new ChannelTopic(userBanEventListenerTopic), messageListenerAdapter);
    }
}
