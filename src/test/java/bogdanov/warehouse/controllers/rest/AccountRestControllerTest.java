package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class AccountRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private UserAccountService service;

    private final String URI = "/api/console/accounts";

    private List<UserAccountDTO> users = new LinkedList<>();

    @PostConstruct
    private void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAll() throws Exception{
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        log.info(result.getResponse().getContentAsString());
        users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserAccountDTO>>() {});

    }



}