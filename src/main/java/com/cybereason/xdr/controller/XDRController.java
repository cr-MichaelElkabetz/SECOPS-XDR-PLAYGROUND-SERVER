package com.cybereason.xdr.controller;

import com.cybereason.xdr.service.XDRService;
import io.javalin.Javalin;

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
            String response = xDRServiceImpl.process(message, msName);
            if ("ERROR".equalsIgnoreCase(response)) {
                ctx.result("{\"message\":\"Processing failed\"}");
            } else {
                ctx.result("{\"message\":\"" + response + "\"}");
            }
            ctx.res.setStatus(200);
        });
    }
}
