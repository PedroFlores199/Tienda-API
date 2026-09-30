package com.tienda.tiendaapi;

import org.springframework.boot.SpringApplication;

public class TestTiendaApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(TiendaApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
