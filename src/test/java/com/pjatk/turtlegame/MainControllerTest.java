package com.pjatk.turtlegame;

import com.pjatk.turtlegame.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest extends BaseTest {

    @Test
    void canSeeTitlePage() throws Exception {
        MockHttpServletRequestBuilder request = get("/");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Zaloguj");
    }

    @Test
    void canRegister() throws Exception {
        MockHttpServletRequestBuilder request = post("/registration")
                .content("email=RegisterTester@test.pl&username=RegisterTester&password=test123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage")).isEqualTo("Rejestracja zakończona pomyślnie. Na mailu czeka na Ciebie link aktywacyjny.");
    }

    @Test
    void canConfirmEmail() throws Exception {
        User user = makeUser("ConfirmTester", false);
        String token = user.getActivationToken();

        MockHttpServletRequestBuilder request = get("/registration/confirm?token=" + token);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("successMessage")).isEqualTo("Konto aktywowane. Możesz się zalogować.");
    }

    @Test
    void canLogin() throws Exception {
        User user = makeUser("LoginTester", true);

        MockHttpServletRequestBuilder request = post("/login")
                .content("username=" + user.getEmail() + "&password=test123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MvcResult result = mockMvc.perform(request).andDo(print()).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/main");
    }
}
