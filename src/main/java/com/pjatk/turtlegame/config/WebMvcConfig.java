package com.pjatk.turtlegame.config;


import com.pjatk.turtlegame.repositories.*;
import com.pjatk.turtlegame.services.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
    private final TurtleEggService turtleEggService;
    private final ExpeditionService expeditionService;
    private final TurtleTrainingHistoryRepository turtleTrainingHistoryRepository;
    private final AcademyService academyService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor(userRepository,
                        userService,
                        turtleRepository,
                        turtleEggRepository,
                        turtleExpeditionHistoryRepository,
                        privateMessageService,
                        turtleEggService,
                        expeditionService,
                        turtleTrainingHistoryRepository,
                        academyService

                ))
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/error");
    }



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:./avatars/");
    }


}
