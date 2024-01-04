package com.pjatk.turtlegame.config;


import com.pjatk.turtlegame.repositories.*;
import com.pjatk.turtlegame.services.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/src/main/resources/media/avatars/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // If the requested resource is not found locally, try to load it from S3

                        String s3BucketUrl = "s3://turtleblastavatars/avatars/"; // Replace with your S3 bucket URL
                        String s3Key = resourcePath.replace("/media/avatars/", ""); // Adjust as per your S3 key structure

                        try {
                            URL s3Url = new URL(s3BucketUrl + s3Key);
                            InputStream s3InputStream = s3Url.openStream();
                            return new InputStreamResource(s3InputStream);
                        } catch (IOException e) {
                            // Handle the exception or log it accordingly
                            return super.getResource(resourcePath, location);
                        }
                    }
                });
    }
}
