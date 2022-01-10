package com.cybereason.xdr.model;

import lombok.*;

import java.util.List;

/**
 * Made with BIG ♥ by Mike Elkabetz
 * Date: 10/01/2022
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentity {
    String id;

    @Singular
    List<String> nameAliases;

    @Singular
    List<String> userAccounts;

    @Singular
    List<String> displayNames;
}