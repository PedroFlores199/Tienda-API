package com.tienda.tiendaapi;

import com.tienda.tiendaapi.domain.*;
import com.tienda.tiendaapi.dto.*;
import com.tienda.tiendaapi.repository.ClienteRepository;
import com.tienda.tiendaapi.repository.FacturaRepository;
import com.tienda.tiendaapi.repository.PedidoRepository;
import com.tienda.tiendaapi.repository.ProductoRepository;
import com.tienda.tiendaapi.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TiendaApiApplicationTests {

    @Autowired private PedidoService pedidoService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private FacturaRepository facturaRepository;

    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setup() {
        facturaRepository.deleteAll();
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = new Cliente(null, "Test User", "test@test.com", "Direccion", "12345678Z");
        cliente = clienteRepository.save(cliente);

        producto = new Producto(null, "Prod 1", new BigDecimal("10.00"), TipoIva.GENERAL, 50);
        producto = productoRepository.save(producto);
    }

    @Test
    void crearPedidoCorrecto() {
        CreatePedidoDTO dto = new CreatePedidoDTO();
        dto.setClienteId(cliente.getId());
        CreateLineaPedidoDTO l = new CreateLineaPedidoDTO();
        l.setProductoId(producto.getId());
        l.setCantidad(2);
        dto.setLineas(Collections.singletonList(l));

        PedidoDTO res = pedidoService.crearPedido(dto);
        assertThat(res).isNotNull();
        assertThat(res.getTotalBase()).isEqualByComparingTo("20.00");
        assertThat(res.getTotalIva()).isEqualByComparingTo("4.20"); // 21% de 20
        assertThat(res.getTotal()).isEqualByComparingTo("24.20");

        Producto p = productoRepository.findById(producto.getId()).orElseThrow();
        assertThat(p.getStock()).isEqualTo(48);
    }

    @Test
    void crearPedidoSinStockNadaSeGuarda() {
        CreatePedidoDTO dto = new CreatePedidoDTO();
        dto.setClienteId(cliente.getId());
        CreateLineaPedidoDTO l = new CreateLineaPedidoDTO();
        l.setProductoId(producto.getId());
        l.setCantidad(100); // More than stock
        dto.setLineas(Collections.singletonList(l));

        long countPedidosBefore = pedidoRepository.count();

        assertThrows(RuntimeException.class, () -> pedidoService.crearPedido(dto));

        assertThat(pedidoRepository.count()).isEqualTo(countPedidosBefore);
        Producto p = productoRepository.findById(producto.getId()).orElseThrow();
        assertThat(p.getStock()).isEqualTo(50); // No stock deducted
    }

    @Test
    void transicionInvalida() {
        CreatePedidoDTO dto = new CreatePedidoDTO();
        dto.setClienteId(cliente.getId());
        CreateLineaPedidoDTO l = new CreateLineaPedidoDTO();
        l.setProductoId(producto.getId());
        l.setCantidad(2);
        dto.setLineas(Collections.singletonList(l));

        PedidoDTO p = pedidoService.crearPedido(dto);
        
        // Try to transition to ENTREGADO from CREADO (invalid)
        assertThrows(RuntimeException.class, () -> pedidoService.cambiarEstado(p.getId(), EstadoPedido.ENTREGADO));
    }

    @Test
    void veinteFacturasEnParaleloNumeradasDel1al20() throws InterruptedException {
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Prepare 20 orders in ENVIADO state
        Long[] pedidoIds = new Long[numThreads];
        for (int i = 0; i < numThreads; i++) {
            CreatePedidoDTO dto = new CreatePedidoDTO();
            dto.setClienteId(cliente.getId());
            CreateLineaPedidoDTO l = new CreateLineaPedidoDTO();
            l.setProductoId(producto.getId());
            l.setCantidad(1);
            dto.setLineas(Collections.singletonList(l));

            PedidoDTO p = pedidoService.crearPedido(dto);
            pedidoService.cambiarEstado(p.getId(), EstadoPedido.PAGADO);
            pedidoService.cambiarEstado(p.getId(), EstadoPedido.ENVIADO);
            pedidoIds[i] = p.getId();
        }

        // Deliver them in parallel
        for (int i = 0; i < numThreads; i++) {
            final Long pid = pedidoIds[i];
            executor.submit(() -> {
                try {
                    pedidoService.cambiarEstado(pid, EstadoPedido.ENTREGADO);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        List<Factura> facturas = facturaRepository.findAll();
        assertThat(facturas).hasSize(20);

        List<String> numeros = facturas.stream().map(Factura::getNumeroFactura).sorted().collect(Collectors.toList());
        int year = java.time.LocalDate.now().getYear();
        for (int i = 0; i < 20; i++) {
            String expected = String.format("%04d-A-%06d", year, i + 1);
            assertThat(numeros.get(i)).isEqualTo(expected);
        }
    }
}
