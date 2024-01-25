package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MarketControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;

    @BeforeAll
    public void setup(){
        User user1 = makeUser("MarketTest", true);

        firstUserDetails = new TurtleUserDetails(user1);

    }

    @Test
    void canSeeMarketPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/market").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Rynek");
    }

    @Test
    void canBuyTurtle() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder requestBuilder = post("/market/" + firstTurtle.getId() + "/buyTurtle").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canUndoTurtle() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder requestBuilder = post("/market/" + firstTurtle.getId() + "/undoTurtle").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canBuyItem() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/market/1/buyItem").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

}
