package com.cybereason.xdr.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Made with BIG ♥ by Mike Elkabetz
 * Date: 02/01/2022
 */

@Getter
@Setter
@Builder
@Data
public class SingleMessage {
    JsonNode jsonNode;
    String elementType;
    String dataSource;
    String schemaId;
}