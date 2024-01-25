package com.pjatk.turtlegame;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.models.UserItem;
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
public class ItemsControllerTest extends BaseTest{

    protected TurtleUserDetails firstUserDetails;


    @BeforeAll
    public void setup(){
        User user1 = makeUser("ItemsTest", true);

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
        UserItem userItem1 = makeUserItem(firstUserDetails.user(), 30, 5);

        MockHttpServletRequestBuilder requestBuilder = get("/items/" + userItem1.getItem().getId() + "/details").with(user(firstUserDetails));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("Szczegóły przedmiotu");
    }

    @Test
    void canSellItem() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = post("/items/3/details").with(user(firstUserDetails))
                .content("Gold=1000&Quantity=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
    }

}
