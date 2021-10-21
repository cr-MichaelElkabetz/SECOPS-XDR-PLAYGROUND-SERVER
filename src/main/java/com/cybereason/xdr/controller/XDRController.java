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
    }
}
