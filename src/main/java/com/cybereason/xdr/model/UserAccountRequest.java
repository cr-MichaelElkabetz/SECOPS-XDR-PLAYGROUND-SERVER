package com.cybereason.xdr.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Most of the code copied and pasted 😂
 * With ♥ by Mike Elkabetz
 * Date: 17/11/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRequest {
    String tenantID;
    String accountIdentifier;

    @JsonProperty("userAccount")
    UserAccount userAccount;
}
