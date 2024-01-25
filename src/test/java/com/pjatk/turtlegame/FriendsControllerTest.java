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
public class FriendsControllerTest extends BaseTest {

    protected TurtleUserDetails firstUserDetails;
    protected TurtleUserDetails secondUserDetails;
    protected TurtleUserDetails thirdUserDetails;

    @Autowired
    FriendRequestService friendRequestService;


    @BeforeAll
    public void setup() {
        User user1 = makeAdmin("1UserFri", true);
        User user2 = makeUser("2UserFri", true);
        User user3 = makeUser("3UserFri", true);

        firstUserDetails = new TurtleUserDetails(user1);
        secondUserDetails = new TurtleUserDetails(user2);
        thirdUserDetails = new TurtleUserDetails(user3);

    }

    @Test
    void canSeeFriendPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/friends").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Znajomi");
    }

    @Test
    void canSendFriendRequest() throws Exception {
        MockHttpServletRequestBuilder request = post("/friends/add").with(user(firstUserDetails))
                .content("friendUsername=" + secondUserDetails.getUsername())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage")).isEqualTo("Zaproszenie wysłano pomyślnie!");

    }

    @Test
    void cantSendFriendsRequestWhenRequestExist() throws Exception {


        MockHttpServletRequestBuilder firstRequest = post("/friends/add").with(user(secondUserDetails))
                .content("friendUsername=" + thirdUserDetails.getUsername())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult firstResult = mockMvc.perform(firstRequest).andReturn();


        MockHttpServletRequestBuilder secondRequest = post("/friends/add").with(user(secondUserDetails))
                .content("friendUsername=" + thirdUserDetails.getUsername())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult secondResult = mockMvc.perform(secondRequest).andReturn();


        assertThat(secondResult.getResponse().getStatus()).isEqualTo(302);
        assertThat(secondResult.getFlashMap().get("failedMessage")).isEqualTo("Zaproszenie do znajomych zostało już wysłane wcześniej!");
    }

}
