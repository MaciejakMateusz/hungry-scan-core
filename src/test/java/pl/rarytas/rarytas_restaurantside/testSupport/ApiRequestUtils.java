package pl.rarytas.rarytas_restaurantside.testSupport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ApiRequestUtils {

    private final MockMvc mockMvc;

    public ApiRequestUtils(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public <T> List<T> fetchObjects(String endpointUrl, Class<T> itemType) throws Exception {
        MvcResult result = mockMvc.perform(get(endpointUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);

        ObjectMapper objectMapper = prepObjMapper();
        return objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, itemType));
    }

    public <T> T fetchObject(String endpointUrl, Class<T> itemType) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);

        return prepObjMapper().readValue(jsonResponse, itemType);
    }

    public void fetchAndExpectUnauthorized(String endpointUrl, ResultMatcher matcher) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    public <T> void postObject(String endpointUrl, T object, ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    public <T> Map<String, Object> postAndReturnResponseBody(String endpointUrl,
                                                             T object,
                                                             ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        ResultActions resultActions = mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
        };
        return objectMapper.readValue(responseBody, typeReference);
    }

    public <T> void patchAndReturnResponseBody(String endpointUrl,
                                                             T object,
                                                             ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(patch(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print());
    }

    public void postMultipartRequest(String endpointUrl,
                                     MenuItem menuItem,
                                     MockMultipartFile file,
                                     ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(menuItem);

        mockMvc.perform(multipart(endpointUrl)
                        .file(file)
                        .param("file", file.getOriginalFilename())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(matcher)
                .andDo(print());
    }

    private ObjectMapper prepObjMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public <T> T getObjectExpect200(String endpoint, Integer id, Class<T> classType) throws Exception {
        Map<String, Object> responseParams =
                postAndReturnResponseBody(endpoint, id, status().isOk());
        String paramName = classType.getSimpleName().toLowerCase();
        return deserializeObject(responseParams.get(paramName), classType);
    }

    public <T> T deserializeObject(Object object, Class<T> classType) {
        return prepObjMapper().convertValue(object, classType);
    }

    public <T> Map<?, ?> postAndExpectErrors(String url, T t) throws Exception {
        Map<String, Object> responseParams =
                postAndReturnResponseBody(url, t, status().isBadRequest());
        return (Map<?, ?>) responseParams.get("errors");
    }

    public <T> void postAndExpect200(String url, T t) throws Exception {
        postAndReturnResponseBody(url, t, status().isOk());
    }

    public <T> void patchAndExpect200(String url, T t) throws Exception {
        patchAndReturnResponseBody(url, t, status().isOk());
    }

    public void postAndExpect200(String url, MenuItem menuItem, MockMultipartFile file) throws Exception {
        postMultipartRequest(url, menuItem, file, status().isOk());
    }
}