package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/monitoring/beans")
public class BeansController {
    @Autowired
    private ApplicationContext appContext;

    @GetMapping
    public @ResponseBody List<String> getBeanList() {
        return Arrays.asList(appContext.getBeanDefinitionNames());
    }
}