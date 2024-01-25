package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.News;
import com.pjatk.turtlegame.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewsControllerTest extends BaseTest {

    protected TurtleUserDetails userDetails;
    protected TurtleUserDetails adminUserDetails;

    @BeforeAll
    public void setup() {
        User admin = makeAdmin("AdminNews", true);
        User user = makeUser("UserNews", true);

        adminUserDetails = new TurtleUserDetails(admin);
        userDetails = new TurtleUserDetails(user);

    }

    @Test
    void adminCanCreateNews() throws Exception {
        MockHttpServletRequestBuilder request = post("/news/create").with(user(adminUserDetails))
                .content("title=test&content=test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage").toString()).isEqualTo("Dodałeś ogłoszenie!");

    }

    @Test
    void userCantCreateNews() throws Exception {
        MockHttpServletRequestBuilder request = post("/news/create").with(user(userDetails))
                .content("title=test&content=test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);

    }

    @Test
    void adminCanEditNews() throws Exception {
        News news = makeNews(adminUserDetails.user());

        MockHttpServletRequestBuilder request = post("/news/edit").with(user(adminUserDetails))
                .content("edit-title=test&edit-content=test&news-id=" + news.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage").toString()).isEqualTo("Wprowadziłeś zmiany!");

    }

    @Test
    void userCantEditNews() throws Exception {
        News news = makeNews(userDetails.user());

        MockHttpServletRequestBuilder request = post("/news/edit").with(user(userDetails))
                .content("title=test&content=test&news-id=" + news.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);

    }


}
