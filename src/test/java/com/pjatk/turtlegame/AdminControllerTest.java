package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerTest extends BaseTest{

    protected TurtleUserDetails userDetails;
    protected TurtleUserDetails adminUserDetails;

    @BeforeAll
    public void setup(){
        User admin = makeAdmin("AdminTester", true);
        User user = makeUser("UserToBan", true);

        adminUserDetails = new TurtleUserDetails(admin);
        userDetails = new TurtleUserDetails(user);

    }

    @Test
    void canSeeAdminPageAsAdmin() throws Exception{
        MockHttpServletRequestBuilder request = get("/admin-panel").with(user(adminUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Panel administracyjny");
    }

    @Test
    void cantSeeAdminPageAsUser() throws Exception{
        MockHttpServletRequestBuilder request = get("/admin-panel").with(user(userDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);

    }

    @Test
    void adminCanBanUser() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/admin-panel/ban-user").with(user(adminUserDetails))
                .content("playerUsername=UserToBan&reason=Test&banExpiredAt=2026-06-06")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

    @Test
    void userCantBanUser() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post("/admin-panel/ban-user").with(user(userDetails))
                .content("playerUsername=AdminTester&reason=Test&banExpiredAt=2026-06-06")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);
    }
}
