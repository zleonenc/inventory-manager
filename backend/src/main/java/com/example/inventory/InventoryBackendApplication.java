package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Inventory Manager API", version = "v1", description = "API for managing products, categories, and inventory metrics", contact = @Contact(name = "Inventory Team")), servers = {
        @Server(url = "/", description = "Default server")
})
public class InventoryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryBackendApplication.class, args);
    }

}
