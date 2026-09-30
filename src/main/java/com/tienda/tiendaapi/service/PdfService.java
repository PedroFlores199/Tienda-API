package com.tienda.tiendaapi.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.tienda.tiendaapi.domain.Factura;
import com.tienda.tiendaapi.domain.LineaFactura;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generarFacturaPdf(Factura factura) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Factura Número: " + factura.getNumeroFactura()));
            document.add(new Paragraph("Fecha: " + factura.getFechaEmision()));
            document.add(new Paragraph("Cliente: " + factura.getNombreCliente() + " - NIF: " + factura.getNifCliente()));
            document.add(new Paragraph("Dirección: " + factura.getDireccionCliente()));
            document.add(new Paragraph("--------------------------------------------------"));

            for (LineaFactura lf : factura.getLineas()) {
                document.add(new Paragraph(lf.getCantidad() + " x " + lf.getNombreProducto() + " - " + lf.getPrecioUnitario() + "€"));
            }

            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Total Base: " + factura.getTotalBase() + "€"));
            document.add(new Paragraph("Total IVA: " + factura.getTotalIva() + "€"));
            document.add(new Paragraph("Total Factura: " + factura.getTotal() + "€"));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
