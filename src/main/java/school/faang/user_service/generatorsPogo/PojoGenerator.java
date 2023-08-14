package school.faang.user_service.generatorsPogo;

import com.helger.jcodemodel.JCodeModel;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PojoGenerator {

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        JCodeModel codeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig();

    }
}
