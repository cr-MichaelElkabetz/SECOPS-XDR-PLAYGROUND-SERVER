package com.cybereason.xdr.model;

import lombok.*;

import java.util.List;

/**
 * Most of the code copied and pasted 😂
 * With ♥ by Mike Elkabetz
 * Date: 15/11/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    String sid;
    String adSid;
    String adLogonName;
    String adDisplayName;
    String adDepartment;
    String adTitle;
    String adCreated;
    String accountStatus;
    String employeeNumber;
    String userIdentity;
    String provider;
    String accountType;

    @Singular
    List<String> emailAddresses;

    @Singular
    List<String> phoneNumbers;
}
