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
public class TurtleControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;


    @BeforeAll
    public void setup() {
        User user1 = makeUser("TurtleTest", true);

        firstUserDetails = new TurtleUserDetails(user1);
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
        Turtle turtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder request = get("/turtles/" + turtle.getId() + "/details").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Szczegóły żółwia");
    }

    @Test
    void canSeeTurtleFightHistoryPage() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder request = get("/turtles/" + turtle.getId() + "/fight-history").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Historia walk żółwia");
    }

    @Test
    void canTurtleWearArmor() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder request = post("/turtles/" + turtle.getId() + "/details/wear").with(user(firstUserDetails))
                .content("helmetId=19")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage").toString()).isEqualTo("Założyłeś ekwipunek!");
    }

    @Test
    void canAbandonTurtle() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder request = post("/turtles/" + turtle.getId() + "/delete").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canSellTurtle() throws Exception {
        Turtle turtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder request = post("/turtles/" + turtle.getId() + "/sell").with(user(firstUserDetails))
                .content("shells=100")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);;

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

}
