package pl.rarytas.rarytas_restaurantside.testSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@Component
public class ApiRequestUtils {

    private final MockMvc mockMvc;

    public ApiRequestUtils(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public <T> List<T> fetchItemListFromEndpoint(String endpointUrl, Class<T> itemType) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, itemType));
    }
}
