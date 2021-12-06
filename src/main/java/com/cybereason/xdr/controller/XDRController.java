package com.cybereason.xdr.controller;

import com.cybereason.xdr.service.XDRService;
import io.javalin.Javalin;

import java.time.Duration;
import java.time.Instant;

public class XDRController {
    public XDRController(Javalin app, XDRService xDRServiceImpl) {
        app.get("/health", ctx -> {
            ctx.res.setContentType("application/json");
            ctx.res.setStatus(200);
            ctx.result("OK");
        });
        app.post("/api/execute/{msName}", ctx -> {
            String msName = ctx.pathParam("msName");
            String message = ctx.body();

            ctx.res.setContentType("application/json");
            Instant start = Instant.now();

            String response = xDRServiceImpl.process(message, msName);
            if ("ERROR".equalsIgnoreCase(response)) {
                ctx.result("{\"message\":\"Processing failed\"}");
            } else {
                Instant end = Instant.now();
                String timeElapsed = Duration.between(start, end).toString()
                        .substring(2)
                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                        .toLowerCase();
                ctx.result("{\"message\":" + response + ", \"timeElapsed\":\"" + timeElapsed + "\"}");
            }
            ctx.res.setStatus(200);
        });

        app.get("/api/user-account/get/all/{tenantID}", ctx -> {
            String tenantID = ctx.pathParam("tenantID");

            ctx.res.setContentType("application/json");
            String response = xDRServiceImpl.getUserAccounts(tenantID);
            if ("ERROR".equalsIgnoreCase(response)) {
                ctx.result("{\"message\":\"Processing failed\"}");
            } else {
                ctx.result(response);
            }
            ctx.res.setStatus(200);
        });
    }
}
