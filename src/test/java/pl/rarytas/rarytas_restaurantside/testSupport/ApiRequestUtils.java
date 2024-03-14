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

/**
 * Utility class designed to simplify making HTTP requests to REST APIs in unit tests.
 * It provides methods for fetching objects, posting objects, patching objects, and handling multipart requests.
 */
@Component
public class ApiRequestUtils {

    private final MockMvc mockMvc;

    /**
     * Constructor to initialize the ApiRequestUtils with a MockMvc instance.
     * @param mockMvc The MockMvc instance to be used for making requests.
     */
    public ApiRequestUtils(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    /**
     * Fetches a list of objects from the specified endpoint.
     * @param endpointUrl The URL of the endpoint to fetch from.
     * @param itemType The type of objects to fetch.
     * @param <T> The type of objects to fetch.
     * @return A list of objects fetched from the endpoint.
     * @throws Exception If an error occurs during the request.
     */
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

    public <T> List<T> fetchObjects(String endpointUrl,Map<String, Object> requestParams, Class<T> itemType) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(requestParams);

        MvcResult result = mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, itemType));
    }

    /**
     * Fetches an object from the specified endpoint URL and converts it into the provided item type.
     *
     * @param endpointUrl The URL endpoint from which to fetch the object.
     * @param itemType    The class type of the object to be fetched.
     * @param <T>         The generic type of the object to be fetched.
     * @return The fetched object of type T.
     * @throws Exception If an error occurs during the fetching or parsing of the object.
     */
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

    /**
     * Fetches from the specified endpoint URL and expects an unauthorized response according to the provided matcher.
     *
     * @param endpointUrl The URL endpoint from which to fetch.
     * @param matcher     The expected result matcher for the unauthorized response.
     * @throws Exception If an error occurs during the fetching or matching of the response.
     */
    public void fetchAndExpectUnauthorized(String endpointUrl, ResultMatcher matcher) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    /**
     * Posts an object to the specified endpoint URL and expects a response according to the provided matcher.
     *
     * @param endpointUrl The URL endpoint to which the object is to be posted.
     * @param object      The object to be posted.
     * @param matcher     The expected result matcher for the response.
     * @param <T>         The generic type of the object to be posted.
     * @throws Exception If an error occurs during the posting or matching of the response.
     */
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

    /**
     * Sends a POST request to the specified endpoint URL with the provided object as the request body.
     * Expects a certain result based on the provided ResultMatcher.
     * Retrieves and returns the response body as a Map of String keys to Object values.
     *
     * @param endpointUrl The URL endpoint to send the POST request to.
     * @param object The object to be serialized and sent as the request body.
     * @param matcher The ResultMatcher to apply to the response.
     * @param <T> The type of the object being sent in the request body.
     * @return A Map representing the response body, with String keys and Object values.
     * @throws Exception If there are any errors during the request or response handling.
     */
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

    /**
     * Sends a POST request to the specified endpoint URL with the provided object as the request body.
     * Expects a certain result based on the provided ResultMatcher.
     *
     * @param endpointUrl The URL endpoint to send the PATCH request to.
     * @param object The object to be serialized and sent as the request body.
     * @param matcher The ResultMatcher to apply to the response.
     * @throws Exception If there are any errors during the request or response handling.
     */
    public <T> void postAndExpect(String endpointUrl,
                                  T object,
                                  ResultMatcher matcher) throws Exception {
        ObjectMapper objectMapper = prepObjMapper();
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print());
    }

    /**
     * Sends a PATCH request to the specified endpoint URL with the provided object as the request body.
     * Expects a certain result based on the provided ResultMatcher.
     *
     * @param endpointUrl The URL endpoint to send the PATCH request to.
     * @param object The object to be serialized and sent as the request body.
     * @param matcher The ResultMatcher to apply to the response.
     * @throws Exception If there are any errors during the request or response handling.
     */
    public <T> void patchAndExpect(String endpointUrl,
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

    /**
     * Posts a multipart request to the specified endpoint URL with the provided MenuItem object, file, and matcher.
     *
     * @param endpointUrl The URL endpoint to which the multipart request will be sent.
     * @param menuItem The MenuItem object to be sent as part of the request payload.
     * @param file The MockMultipartFile object representing the file to be sent in the multipart request.
     * @param matcher The ResultMatcher object to validate the response received from the server.
     * @throws Exception If an error occurs during the request execution.
     */
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

    /**
     * Prepares and returns an ObjectMapper instance with JavaTimeModule registered.
     *
     * @return An ObjectMapper instance prepared with JavaTimeModule.
     */
    private ObjectMapper prepObjMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * Sends a POST request to the specified endpoint, expecting a response with status code 200,
     * and deserializes the response body to the specified class type.
     *
     * @param endpoint The URL endpoint to which the POST request will be sent.
     * @param id The identifier used in the request.
     * @param classType The class type to which the response body will be deserialized.
     * @param <T> The type of the response body.
     * @return An instance of the deserialized class.
     * @throws Exception If an error occurs during the request execution or deserialization process.
     */
    public <T> T getObjectExpect200(String endpoint, Integer id, Class<T> classType) throws Exception {
        Map<String, Object> responseParams =
                postAndReturnResponseBody(endpoint, id, status().isOk());
        String paramName = classType.getSimpleName().toLowerCase();
        return deserializeObject(responseParams.get(paramName), classType);
    }

    /**
     * Deserializes an object into an instance of the specified class type.
     *
     * @param object    The object to be deserialized.
     * @param classType The class type to which the object will be deserialized.
     * @param <T>       The type of the class.
     * @return An instance of the class type after deserialization.
     */
    public <T> T deserializeObject(Object object, Class<T> classType) {
        return prepObjMapper().convertValue(object, classType);
    }

    /**
     * Performs a POST request to the specified URL with the provided object and expects error responses.
     *
     * @param url The URL to which the POST request will be sent.
     * @param t   The object to be sent as part of the request.
     * @param <T> The type of the object.
     * @return A map containing error responses received from the server.
     * @throws Exception If an error occurs during the request.
     */
    public <T> Map<?, ?> postAndExpectErrors(String url, T t) throws Exception {
        Map<String, Object> responseParams =
                postAndReturnResponseBody(url, t, status().isBadRequest());
        return (Map<?, ?>) responseParams.get("errors");
    }

    /**
     * Performs a POST request to the specified URL with the provided object and expects unauthorized response.
     *
     * @param url The URL to which the POST request will be sent.
     * @param t   The object to be sent as part of the request.
     * @param <T> The type of the object.
     * @throws Exception If an error occurs during the request.
     */
    public <T> void postAndExpectUnauthorized(String url, T t) throws Exception {
        postAndExpect(url, t, status().isUnauthorized());
    }

    /**
     * Performs a PATCH request to the specified URL with the provided object and expects unauthorized response.
     *
     * @param url The URL to which the POST request will be sent.
     * @param t   The object to be sent as part of the request.
     * @param <T> The type of the object.
     * @throws Exception If an error occurs during the request.
     */
    public <T> void patchAndExpectUnauthorized(String url, T t) throws Exception {
        patchAndExpect(url, t, status().isUnauthorized());
    }

    /**
     * Performs a POST request to the specified URL with the provided object and expects a successful (200) response.
     *
     * @param url The URL to which the POST request will be sent.
     * @param t   The object to be sent as part of the request.
     * @param <T> The type of the object.
     * @throws Exception If an error occurs during the request.
     */
    public <T> void postAndExpect200(String url, T t) throws Exception {
        postAndExpect(url, t, status().isOk());
    }

    /**
     * Performs a PATCH request to the specified URL with the provided object and expects a successful (200) response.
     *
     * @param url The URL to which the PATCH request will be sent.
     * @param t   The object to be sent as part of the request.
     * @param <T> The type of the object.
     * @throws Exception If an error occurs during the request.
     */
    public <T> void patchAndExpect200(String url, T t) throws Exception {
        patchAndExpect(url, t, status().isOk());
    }

    /**
     * Performs a multipart POST request to the specified URL with the provided menu item and file,
     * expecting a successful (200) response.
     *
     * @param url       The URL to which the POST request will be sent.
     * @param menuItem  The menu item to be sent as part of the request.
     * @param file      The file to be sent as part of the request.
     * @throws Exception If an error occurs during the request.
     */
    public void postAndExpect200(String url, MenuItem menuItem, MockMultipartFile file) throws Exception {
        postMultipartRequest(url, menuItem, file, status().isOk());
    }
}