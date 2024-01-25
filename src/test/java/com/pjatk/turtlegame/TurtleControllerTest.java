package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.services.FriendRequestService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TurtleControllerTest extends BaseTest {

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
    void canSeeTurtlePage() throws Exception {
        MockHttpServletRequestBuilder request = get("/turtles").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Twoje żółwie");
    }

    @Test
    void canSeeTurtleDetailsPage() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 100, 20);

        MockHttpServletRequestBuilder request = get("/turtles/"+turtle.getId()+"/details").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains(turtle.getName());

    }

    @Test
    void cantSeeTurtleDetailsPageIfUserIsNotOwner() throws Exception {
        Turtle turtle1 = makeTurtle(secondUserDetails.user(), 100, 20);

        MockHttpServletRequestBuilder request = get("/turtles/"+turtle1.getId()+"/details").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Twoje żółwie");
    }

    @Test
    void canAbandonTurtle() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 100, 20);

        MockHttpServletRequestBuilder request = post("/turtles/"+turtle.getId()+"/delete").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canSellTurtle() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 100, 20);

        MockHttpServletRequestBuilder request = post("/turtles/"+turtle.getId()+"/sell").with(user(firstUserDetails))
                .content("shells=100")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);;

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void cantSellTurtleIfHeIsNotOurs() throws Exception {
        Turtle turtle = makeTurtle(secondUserDetails.user(), 100, 20);

        MockHttpServletRequestBuilder request = post("/turtles/"+turtle.getId()+"/sell").with(user(firstUserDetails))
                .content("shells=100")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);;

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getFlashMap().get("failedMessage").toString()).contains("Nie można");
    }

}
