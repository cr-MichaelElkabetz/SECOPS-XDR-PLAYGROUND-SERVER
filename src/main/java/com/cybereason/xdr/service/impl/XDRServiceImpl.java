package com.cybereason.xdr.service.impl;

import com.cybereason.xdr.service.XDRService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class XDRServiceImpl implements XDRService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XDRServiceImpl.class);
    private static final String BASE_URL = "http://localhost:8081";
    private final OkHttpClient httpClient = new OkHttpClient();


    @Override
    public String execute(String message) {
        LOGGER.info("Received a message to deliver: ", message);
        return sendMessageToTopic(message);
    }

    String sendMessageToTopic(String message) {
        Response response = null;

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), message);

        Request request = new Request.Builder()
                .url(BASE_URL + "/publish")
                .post(body)
                .build();

        Call call = httpClient.newCall(request);
        try {
            response = call.execute();
        } catch (IOException e) {
            LOGGER.error("Failed to execute the request: ", e.getMessage(), e);
        }
        if (response == null)
            return "OK";
        else {
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "OK";
        }
    }
}
