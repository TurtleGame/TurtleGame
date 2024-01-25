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
public class ExpeditionControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;

    @BeforeAll
    public void setup(){
        User user1 = makeUser("ExpeditionTest", true);

        firstUserDetails = new TurtleUserDetails(user1);

    }

    @Test
    void canSeeExpeditionPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/expeditions").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Wyprawy");
    }

    @Test
    void canSendTurtleOnExpeditionWhenHaveEnoughLevel() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder requestBuilder = post("/expeditions").with(user(firstUserDetails))
                .content("turtleId="+firstTurtle.getId()+"&durationTime=30&expeditionId=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void cantSendTurtleOnExpeditionWhenDontHaveEnoughLevel() throws Exception {
        Turtle firstTurtle = makeTurtle(firstUserDetails.user(), 50, 1);

        MockHttpServletRequestBuilder requestBuilder = post("/expeditions").with(user(firstUserDetails))
                .content("turtleId="+firstTurtle.getId()+"&durationTime=30&expeditionId=3")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("failedMessage").toString()).contains("Wymagany level, aby wyruszyć na tą wyprawę to ");
    }

}
