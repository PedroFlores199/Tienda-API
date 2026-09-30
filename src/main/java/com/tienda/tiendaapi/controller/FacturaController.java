package com.tienda.tiendaapi.controller;

import com.tienda.tiendaapi.dto.FacturaDTO;
import com.tienda.tiendaapi.service.FacturaService;
import com.tienda.tiendaapi.service.PdfService;
import com.tienda.tiendaapi.domain.Factura;
import com.tienda.tiendaapi.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.io.StringWriter;
import com.opencsv.CSVWriter;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {
    private final FacturaService facturaService;
    private final FacturaRepository facturaRepository;
    private final PdfService pdfService;

    @GetMapping
    public Page<FacturaDTO> listar(
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            Pageable pageable) {
        return facturaService.listarFacturas(clienteNombre, desde, hasta, pageable);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow();
        byte[] pdf = pdfService.generarFacturaPdf(factura);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Factura_" + factura.getNumeroFactura() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/exportar")
    public ResponseEntity<String> exportarCsv(@RequestParam int anio, @RequestParam int mes) throws Exception {
        List<Factura> facturas = facturaService.getFacturasPorMes(anio, mes);
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        writer.writeNext(new String[]{"Numero", "Fecha", "Cliente", "Total Base", "Total IVA", "Total"});
        for (Factura f : facturas) {
            writer.writeNext(new String[]{
                f.getNumeroFactura(),
                f.getFechaEmision().toString(),
                f.getNombreCliente(),
                f.getTotalBase().toString(),
                f.getTotalIva().toString(),
                f.getTotal().toString()
            });
        }
        writer.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facturas_" + anio + "_" + mes + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sw.toString());
    }
}
