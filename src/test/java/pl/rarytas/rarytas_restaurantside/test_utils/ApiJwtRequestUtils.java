package pl.rarytas.rarytas_restaurantside.test_utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ApiJwtRequestUtils extends ApiRequestUtils {

    /**
     * Constructor to initialize the ApiRequestUtils with a MockMvc instance.
     *
     * @param mockMvc The MockMvc instance to be used for making requests.
     */
    public ApiJwtRequestUtils(MockMvc mockMvc) {
        super(mockMvc);
    }

    public <T> void postAndExpect(String endpointUrl, T object, String jwt, ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    public <T> void patchAndExpect(String endpointUrl, T object, String jwt, ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(patch(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    public <T> void postAndExpect200(String url, T t, String jwt) throws Exception {
        postAndExpect(url, t, jwt, status().isOk());
    }

    public <T> void patchAndExpect200(String url, T t, String jwt) throws Exception {
        patchAndExpect(url, t, jwt, status().isOk());
    }
}
