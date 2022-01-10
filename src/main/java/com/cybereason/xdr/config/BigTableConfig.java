package com.cybereason.xdr.config;

import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Make with BIG ♥ by Mike Elkabetz
 * Date: 30/11/2021
 */

@Slf4j
@Configuration
@Data
public class BigTableConfig {

    private static String projectId = "xdr-prod-host-project";
    private static String instanceId = "xdr-us-east1";
    private String emulatorHost;
    private int emulatorPort;

    public static BigtableTableAdminClient bigtableAdmin() {
        BigtableTableAdminClient adminClient = null;
        try {
            BigtableTableAdminSettings adminSettings =
                    BigtableTableAdminSettings.newBuilder()
                            .setProjectId(projectId)
                            .setInstanceId(instanceId)
                            .build();
            adminClient = BigtableTableAdminClient.create(adminSettings);
            log.info("BigtableAdminClient initialized successfully. projectId: {}, instanceId: {}", projectId, instanceId);
        } catch (IOException e) {
            log.error("Unable to connect GCP Bigtable", e.getMessage(), e);
        }
        return adminClient;
    }

    public static BigtableDataClient bigtableDataClient() {
        BigtableDataClient bigtableDataClient = null;
        try {
            BigtableDataSettings settings =
                    BigtableDataSettings.newBuilder().setProjectId(projectId).setInstanceId(instanceId).build();
            bigtableDataClient = BigtableDataClient.create(settings);
            log.info("BigtableDataClient initialized successfully. projectId: {}, instanceId: {}", projectId, instanceId);
        } catch (IOException e) {
            log.error("Unable to connect GCP Bigtable", e.getMessage(), e);
        }
        return bigtableDataClient;
    }
}
