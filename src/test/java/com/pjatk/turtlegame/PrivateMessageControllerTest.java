package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
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
public class PrivateMessageControllerTest extends BaseTest {

    protected TurtleUserDetails firstUserDetails;
    protected TurtleUserDetails secondUserDetails;



    @BeforeAll
    public void setup() {
        User user1 = makeAdmin("1UserPriv", true);
        User user2 = makeUser("2UserPriv", true);

        firstUserDetails = new TurtleUserDetails(user1);
        secondUserDetails = new TurtleUserDetails(user2);
    }

    @Test
    void canSeePrivateMessagePage() throws Exception {
        MockHttpServletRequestBuilder request = get("/private-message").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Prywatne wiadomości");
    }

    @Test
    void canSendPrivateMessage() throws Exception {
        MockHttpServletRequestBuilder request = post("/private-message/create").with(user(firstUserDetails))
                .content("recipient="+secondUserDetails.getUsername()+"&title=test&content=hej&gold=100")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage")).isEqualTo("Wysłałeś wiadomość!");

    }

    @Test
    void cantSendPrivateMessageWithTooMuchGold() throws Exception {


        MockHttpServletRequestBuilder firstRequest = post("/private-message/create").with(user(firstUserDetails))
                .content("recipient="+secondUserDetails.getUsername()+"&title=test&content=hej&gold=10000")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult firstResult = mockMvc.perform(firstRequest).andReturn();

        assertThat(firstResult.getResponse().getStatus()).isEqualTo(302);
        assertThat(firstResult.getFlashMap().get("failedMessage")).isEqualTo("Posiadasz za mało złota!");
    }

}
