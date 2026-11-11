package vallegrande.edu.pe.cuyconectacrud.report;

import vallegrande.edu.pe.cuyconectacrud.dao.InscripcionDAO;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Reporte de Inscritos por Evento (Integrante 3).
 *
 * Reporte tabular JasperReports agrupado por evento, con subtotal de
 * inscritos por cada evento y total general. Los datos salen del DAO
 * (el SQL no vive en el controlador).
 */
public class ReporteInscripciones {

    private static final String RUTA_JRXML =
            "/reportes/inscritos_por_evento.jrxml";

    private final InscripcionDAO dao;

    public ReporteInscripciones() {
        this.dao = new InscripcionDAO();
    }

    /** Compila la plantilla JRXML incluida en el classpath. */
    private JasperReport compilar() throws JRException {
        try (InputStream in =
                getClass().getResourceAsStream(RUTA_JRXML)) {

            if (in == null) {
                throw new JRException(
                        "No se encontró la plantilla " + RUTA_JRXML);
            }
            return JasperCompileManager.compileReport(in);

        } catch (IOException e) {
            throw new JRException(
                    "No se pudo leer la plantilla del reporte", e);
        }
    }

    /** Llena el reporte con las inscripciones ordenadas por evento. */
    private JasperPrint crearPrint() throws SQLException, JRException {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_LOCALE", Locale.forLanguageTag("es-PE"));

        return JasperFillManager.fillReport(
                compilar(),
                parametros,
                new JRBeanCollectionDataSource(dao.listarParaReporte())
        );
    }

    /** Muestra el reporte en el visor de JasperReports. */
    public void mostrarEnVisor() throws SQLException, JRException {
        JasperPrint print = crearPrint();

        SwingUtilities.invokeLater(() ->
                JasperViewer.viewReport(print, false)
        );
    }

    /** Exporta el reporte a un archivo PDF en la ruta indicada. */
    public void exportarPdf(String rutaArchivo)
            throws SQLException, JRException {

        JasperExportManager.exportReportToPdfFile(
                crearPrint(), rutaArchivo);
    }
}
