package com.hackybear.hungry_scan_core.test_utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Utility class that extends {@link ApiRequestUtils} to include JWT-based authentication in API requests.
 * This class provides methods for sending authenticated POST and PATCH requests,
 * facilitating the testing of REST endpoints that require JWT authentication.
 */
@Component
public class ApiJwtRequestUtils extends ApiRequestUtils {

    /**
     * Constructs an instance of {@code ApiJwtRequestUtils} using the provided {@link MockMvc} instance.
     *
     * @param mockMvc the {@link MockMvc} instance to be used for making requests.
     */
    public ApiJwtRequestUtils(MockMvc mockMvc) {
        super(mockMvc);
    }

    /**
     * Sends an authenticated POST request to the specified endpoint and validates the response.
     *
     * @param endpointUrl the URL of the endpoint to send the POST request to.
     * @param object      the request payload to be sent in the POST request body.
     * @param jwt         the JWT token to include in the Authorization header.
     * @param matcher     the expected {@link ResultMatcher} for response validation.
     * @param <T>         the type of the request payload.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T> void postAndExpect(String endpointUrl, T object, String jwt, ResultMatcher matcher) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(post(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    /**
     * Sends an authenticated PATCH request to the specified endpoint and validates the response.
     *
     * @param endpointUrl the URL of the endpoint to send the PATCH request to.
     * @param object      the request payload to be sent in the PATCH request body.
     * @param jwt         the JWT token to include in the Authorization header.
     * @param matcher     the expected {@link ResultMatcher} for response validation.
     * @param <T>         the type of the request payload.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T> void patchAndExpect(String endpointUrl, T object, String jwt, ResultMatcher matcher) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(object);

        mockMvc.perform(patch(endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(jsonRequest))
                .andExpect(matcher)
                .andDo(print())
                .andReturn();
    }

    /**
     * Sends an authenticated PATCH request with two parameters to the specified endpoint and validates the response.
     *
     * @param url     the URL of the endpoint to send the PATCH request to.
     * @param t       the first parameter, typically used as an ID in the request.
     * @param r       the second parameter, typically representing a value in the request.
     * @param jwt     the JWT token to include in the Authorization header.
     * @param matcher the expected {@link ResultMatcher} for response validation.
     * @param <T>     the type of the first parameter.
     * @param <R>     the type of the second parameter.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T, R> void patchAndExpect(String url, T t, R r, String jwt, ResultMatcher matcher) throws Exception {
        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .param("id", t.toString())
                        .param("value", r.toString()))
                .andExpect(matcher)
                .andDo(print());
    }

    /**
     * Sends an authenticated POST request to the specified endpoint expecting a successful 200 OK response.
     *
     * @param url the URL of the endpoint to send the POST request to.
     * @param t   the request payload to be sent in the POST request body.
     * @param jwt the JWT token to include in the Authorization header.
     * @param <T> the type of the request payload.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T> void postAndExpect200(String url, T t, String jwt) throws Exception {
        postAndExpect(url, t, jwt, status().isOk());
    }

    /**
     * Sends an authenticated PATCH request to the specified endpoint expecting a successful 200 OK response.
     *
     * @param url the URL of the endpoint to send the PATCH request to.
     * @param t   the request payload to be sent in the PATCH request body.
     * @param jwt the JWT token to include in the Authorization header.
     * @param <T> the type of the request payload.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T> void patchAndExpect200(String url, T t, String jwt) throws Exception {
        patchAndExpect(url, t, jwt, status().isOk());
    }

    /**
     * Sends an authenticated PATCH request with two parameters to the specified endpoint,
     * expecting a successful 200 OK response.
     *
     * @param url the URL of the endpoint to send the PATCH request to.
     * @param t   the first parameter, typically used as an ID in the request.
     * @param r   the second parameter, typically representing a value in the request.
     * @param jwt the JWT token to include in the Authorization header.
     * @param <T> the type of the first parameter.
     * @param <R> the type of the second parameter.
     * @throws Exception if an error occurs during the request or response handling.
     */
    public <T, R> void patchAndExpect200(String url, T t, R r, String jwt) throws Exception {
        patchAndExpect(url, t, r, jwt, status().isOk());
    }
}