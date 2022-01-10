package com.cybereason.xdr.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface XDRService {
    String process(String message, String msName);

    String getUserAccounts(String tenantID) throws JsonProcessingException;

    String getBigtableData(String type) throws JsonProcessingException;
}
