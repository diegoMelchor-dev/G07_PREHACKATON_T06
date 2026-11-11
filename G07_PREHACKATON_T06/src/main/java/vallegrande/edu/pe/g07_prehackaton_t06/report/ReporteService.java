package vallegrande.edu.pe.g07_prehackaton_t06.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import vallegrande.edu.pe.g07_prehackaton_t06.model.Evento;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteService {

    public void generarPDF(List<Evento> eventos, String rutaDestino) throws JRException {
        InputStream plantilla = getClass().getResourceAsStream("/reports/eventos.jrxml");
        if (plantilla == null) {
            throw new JRException("No se encontró la plantilla /reports/eventos.jrxml");
        }

        try (plantilla) {
            JasperReport reporte = JasperCompileManager.compileReport(plantilla);
            JRBeanCollectionDataSource datos = new JRBeanCollectionDataSource(eventos);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TITULO_REPORTE", "REPORTE GENERAL DE EVENTOS");
            parametros.put("TOTAL_EVENTOS", eventos.size());

            JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros, datos);
            JasperExportManager.exportReportToPdfFile(impresion, rutaDestino);
        } catch (java.io.IOException e) {
            throw new JRException("No se pudo cerrar la plantilla del reporte", e);
        }
    }
}
