package com.cybereason.xdr.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Made with BIG ♥ by Mike Elkabetz
 * Date: 09/01/2022
 */

@Setter
@Getter
@Builder
public class BigtableAccountsData {
    String tableName;
    String key;
    UserAccount userAccount;
}
