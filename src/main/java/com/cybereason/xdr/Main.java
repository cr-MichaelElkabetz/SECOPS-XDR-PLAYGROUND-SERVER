package com.cybereason.xdr;

import com.cybereason.xdr.controller.XDRController;
import com.cybereason.xdr.service.impl.XDRServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .start(8888);
        //TODO: when using Docker please change the path to /static/index.html
        app._conf.addSinglePageRoot("/", "./src/main/resources/static/index.html", Location.EXTERNAL);
        //TODO: when using Docker please change the path to /static/
        app._conf.addStaticFiles("./src/main/resources/static/", Location.EXTERNAL);
        LOGGER.info("*** XDR Playground Server is up and running ***");
        new XDRController(app, new XDRServiceImpl());
    }
}
