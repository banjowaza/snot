package com.github.banjowaza.snot;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  Generic Snot Exception handler for SlackClient that reads the response body to check for errors 
 *  
 */
public class SnotRag extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = //NOSONAR
            LoggerFactory.getLogger(SnotRag.class);

    private static final String TEMPORARY_HEADER = "X-internal-payload";

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        //Slack always returns a 200, unfortunately
        //have to parse and inspect the body to find an error
        String payload = new String(getResponseBody(response));
        if (hasErrorInBody(payload)) {
            //stuff this in a temporary header so we can read it in #handleError
            response.getHeaders()
                    .add(TEMPORARY_HEADER, payload);
            return true;
        }

        return false;
    }

    //error unless we find "ok":true in json
    private boolean hasErrorInBody(String payload) {
        try {
            JsonNode rootNode = mapper.readTree(payload);
            JsonNode okNode = rootNode.get("ok");
            return okNode == null || !okNode.asBoolean(false);
        } catch (IOException e) {
            LOGGER.warn("failed to parse Slack response json: {}", payload, e);
        }
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        //extract the payload from the temporary header
        String payload = response.getHeaders()
                .get(TEMPORARY_HEADER)
                .get(0);
        response.getHeaders()
                .keySet()
                .remove(TEMPORARY_HEADER);
        LOGGER.warn("an error occurred sending a request to Slack: {}", payload);
        throw new RuntimeException("an error occurred sending a request to Slack");
    }
}
