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
        app.post("/api/execute", ctx -> {
            ctx.res.setContentType("application/json");
            String message = ctx.body();
            ctx.result(xDRServiceImpl.execute(message));
            ctx.res.setStatus(200);
        });
    }
}
