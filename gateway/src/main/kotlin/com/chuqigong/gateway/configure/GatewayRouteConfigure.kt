package com.chuqigong.gateway.configure

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRouteConfigure {
    @Bean
    fun apiServiceRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes {
            route(id = "route-to-api-service") {
                path("/api/**")
                uri("http://api-service.default.svc.cluster.local:8876")
            }
        }
    }
}