package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.Turtle;
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
public class NestControllerTest extends BaseTest {

    protected TurtleUserDetails firstUserDetails;

    protected TurtleUserDetails secondUserDetails;

    @BeforeAll
    public void setup() {
        User user1 = makeUser("1UserTurtle", true);
        User user2 = makeUser("2UserTurtle", true);

        firstUserDetails = new TurtleUserDetails(user1);
        secondUserDetails = new TurtleUserDetails(user2);
    }

    @Test
    void canSeeNestPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/nest").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Legowisko");
    }

    @Test
    void canSellEgg() throws Exception {

        MockHttpServletRequestBuilder request = post("/nest/1/sell").with(user(firstUserDetails))
                .content("Gold=100&Quantity=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);

    }

    @Test
    void canAdoptEgg() throws Exception {

        MockHttpServletRequestBuilder request = post("/nest/2/adopt").with(user(firstUserDetails))
                .content("Name=Basia&Gender=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        ;

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canWarmEgg() throws Exception {
        addTurtleEgg(firstUserDetails.user());
        MockHttpServletRequestBuilder request = post("/nest/1/warm").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(302);

    }


}
