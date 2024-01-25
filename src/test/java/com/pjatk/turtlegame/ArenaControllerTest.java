package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.models.Turtle;
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
public class ArenaControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;
    protected TurtleUserDetails secondUserDetails;

    @BeforeAll
    public void setup(){
        User user1 = makeUser("1ArenaTester", true);
        User user2 = makeUser("2ArenaTester", true);

        firstUserDetails = new TurtleUserDetails(user1);
        secondUserDetails = new TurtleUserDetails(user2);


    }

    @Test
    void canSeeArenaPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/arena").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Arena");
    }

    @Test
    void turtlesCanFightWhenTheyHaveMoreThan0Energy() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 50, 1);
        Turtle secondTurtle = makeTurtle(secondUserDetails.user(), 70, 2);

        MockHttpServletRequestBuilder requestBuilder = post("/arena/attack").with(user(firstUserDetails))
                .content("ourTurtleId=1&opponentTurtleId=2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void turtlesCantFightWhenOurTurtleHave0Energy() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 0, 1);
        Turtle secondTurtle = makeTurtle(secondUserDetails.user(), 2, 2);

        MockHttpServletRequestBuilder requestBuilder = post("/arena/attack").with(user(firstUserDetails))
                .content("ourTurtleId="+firstTurtle.getId()+"&opponentTurtleId="+secondTurtle.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("failedMessage").toString()).isEqualTo("Żółw ma za mało energii");
    }

}
