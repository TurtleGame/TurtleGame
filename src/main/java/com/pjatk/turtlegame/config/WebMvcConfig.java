package com.pjatk.turtlegame.config;

import com.pjatk.turtlegame.repositories.TurtleEggRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ExpeditionService;
import com.pjatk.turtlegame.services.PrivateMessageService;
import com.pjatk.turtlegame.services.TurtleStatisticService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TurtleRepository turtleRepository;
    private final TurtleEggRepository turtleEggRepository;
    private final TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    private final PrivateMessageService privateMessageService;
    private final TurtleStatisticService turtleStatisticService;
    private final ExpeditionService expeditionService;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor(userRepository,
                        userService,
                        turtleRepository,
                        turtleEggRepository,
                        turtleExpeditionHistoryRepository,
                        privateMessageService,
                        turtleStatisticService,
                        expeditionService

                ))
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/error");
    }
}
