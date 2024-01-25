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
public class ItemControllerTest extends BaseTest {

    protected TurtleUserDetails firstUserDetails;


    @BeforeAll
    public void setup() {
        User user1 = makeUser("1ItemTest", true);

        firstUserDetails = new TurtleUserDetails(user1);
    }

    @Test
    void canSeeItemsPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/items").with(user(firstUserDetails));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Ekwipunek");
    }

    @Test
    void canSeeItemDetailsPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/items/10/details").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Sałata");

    }

    @Test
    void canSellItem() throws Exception {

        MockHttpServletRequestBuilder request = post("/items/10/details").with(user(firstUserDetails))
                .content("Gold=100&Quantity=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }


    @Test
    void cantSellItemIfWeDontHaveEnough() throws Exception {

        MockHttpServletRequestBuilder request = post("/items/10/details").with(user(firstUserDetails))
                .content("Gold=100&Quantity=1000")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getFlashMap().get("failedMessage").toString()).isEqualTo("Brak wystarczającej ilości");

    }



}
