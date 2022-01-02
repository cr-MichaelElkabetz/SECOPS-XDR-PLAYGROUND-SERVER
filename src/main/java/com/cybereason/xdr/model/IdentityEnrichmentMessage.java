package com.cybereason.xdr.model;

import com.cybereason.models.CybereasonDataObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * Made with BIG ♥ by Mike Elkabetz
 * Date: 02/01/2022
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityEnrichmentMessage {
    String customerId;
    Collection<CybereasonDataObject> cybereasonDataObjects;
    Map<String, String> attributes;
}