
package com.cubetiqs.api.book;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.logging.common.LogConfig;
import io.helidon.openapi.OpenApiFeature;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

/**
 * The application main class.
 */
public class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * 
     * @param args command line arguments.
     */
    public static void main(String[] args) {

        // load logging configuration
        LogConfig.configureRuntime();

        // initialize global config from default configuration
        Config config = Config.create();
        Config.global(config);

        WebServer server = WebServer.builder()
                .config(config.get("server"))
                .addFeature(OpenApiFeature.create(Config.global().get("openapi")))
                .routing(Main::routing)
                .mediaContext(MediaContext.builder()
                        .mediaSupportsDiscoverServices(false)
                        .addMediaSupport(JacksonSupport.create(new ObjectMapper()))
                        .build())
                .build()
                .start();

        System.out.println("WEB server is up! http://0.0.0.0:" + server.port());

    }

    /**
     * Updates HTTP Routing.
     */
    static void routing(HttpRouting.Builder routing) {
        routing
                .register("/books", new BookService())
                .get("/", (req, res) -> res.send("ok"));
    }
}