package com.programacion.distribuida.todo;

import io.quarkus.datasource.runtime.DataSourcesBuildTimeConfig;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ToDoLifeCycle {


    @Inject
    @ConfigProperty(name = "consul.host", defaultValue = "127.0.0.1")
    String consulHost;

    @Inject
    @ConfigProperty(name = "consul.port", defaultValue = "8500")
    Integer consulPort;

    @Inject
    @ConfigProperty(name = "quarkus.http.port")
    Integer appPort;

    String serviceId;
    @Inject
    DataSourcesBuildTimeConfig dataSourcesBuildTimeConfig;
    @Inject
    Vertx vertx;

    void init(@Observes StartupEvent event, Vertx vertx) throws UnknownHostException {
        //para evento de inicializacion

        ConsulClientOptions options = new ConsulClientOptions()
                .setHost(consulHost)
                .setPort(consulPort);

        ConsulClient consulClient = ConsulClient.create(vertx,options);

        serviceId = UUID.randomUUID().toString();

        var ipAddress = InetAddress.getLocalHost();

        //. registrar
        var tags = List.of(
                "traefik.enable=true",
                "traefik.http.routers.app-todos.rule=PathPrefix(`/app-todos`)",
                "traefik.http.routers.app-todos.middlewares=strip-prefix-todos",
                "traefik.http.middlewares.strip-prefix-todos.stripPrefix.prefixes=/app-todos"
        );

        var checkOptions = new CheckOptions()
                .setHttp(String.format("http://%s:%s/ping", ipAddress.getHostAddress(),appPort))
                .setInterval("10s")
                .setDeregisterAfter("20s");

        ServiceOptions serviceOptions = new ServiceOptions()
                .setName("app-todos")
                .setId(serviceId)
                .setAddress(ipAddress.getHostAddress())
                .setPort(appPort)
                .setTags(tags)
                .setCheckOptions(checkOptions);

        consulClient.registerServiceAndAwait(serviceOptions);

    }

    void stop(@Observes ShutdownEvent event) {
        //para cuando se detiene el proyecto
        System.out.println("Stopping Todo's services...");

        ConsulClientOptions options = new ConsulClientOptions()
                .setHost(consulHost)
                .setPort(consulPort);

        ConsulClient consulClient = ConsulClient.create(vertx, options);

        consulClient.deregisterServiceAndAwait(serviceId);

    }
}
