package com.cybereason.xdr.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Made with BIG ♥ by Mike Elkabetz
 * Date: 10/01/2022
 */

@Setter
@Getter
@Builder
public class BigtableIdentitiesData {
    String tableName;
    String key;
    UserIdentity userIdentity;
}