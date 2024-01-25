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

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;
    protected TurtleUserDetails secondUserDetails;

    @BeforeAll
    public void setup() {
        User user1 = makeUser("1UserTest", true);
        User user2 = makeUser("2UserTest", true);

        firstUserDetails = new TurtleUserDetails(user1);
        secondUserDetails = new TurtleUserDetails(user2);

    }

    @Test
    void canVisitUserPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/user/" + firstUserDetails.getId()).with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Profil gracza");

    }

    @Test
    void canVisitProfileFromFriendsPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/user/profile").with(user(firstUserDetails))
                        .content("friendUsername=" + secondUserDetails.getUsername())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void canVisitEditPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/user/edit").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Edytuj profil");
    }

    @Test
    void canChangePassword() throws Exception {
        MockHttpServletRequestBuilder request =
                post("/user/edit-password")
                        .with(user(firstUserDetails))
                        .content("oldPassword=test123&newPassword=test1234")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage").toString()).isEqualTo("Zmiana hasła udana");
    }

    @Test
    void cantChangePasswordWhenOldIsNotCorrect() throws Exception {
        MockHttpServletRequestBuilder request = post("/user/edit-password").with(user(firstUserDetails))
                        .content("oldPassword=test12356&newPassword=test1234")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(Objects.requireNonNull(result.getModelAndView()).getModel().get("failedMessage")).isEqualTo("Stare hasło nie pasuje");
    }

}
