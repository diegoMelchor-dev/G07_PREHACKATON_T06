package vallegrande.edu.pe.cuyconectacrud.report;

import vallegrande.edu.pe.cuyconectacrud.dao.ParticipanteDAO;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
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
 * Reporte de Participantes por Institución (Integrante 2).
 *
 * Reporte tabular JasperReports agrupado por institución, con
 * subtotal por cada institución y total general. Los datos salen
 * del DAO (el SQL no vive en el controlador).
 */
public class ReporteParticipantes {

    private static final String RUTA_JRXML =
            "/reportes/participantes_por_institucion.jrxml";

    private final ParticipanteDAO dao;

    public ReporteParticipantes() {
        this.dao = new ParticipanteDAO();
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

    /** Llena el reporte con los participantes ordenados por institución. */
    private JasperPrint crearPrint()
            throws SQLException, JRException {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_LOCALE", new java.util.Locale("es", "PE"));

        return JasperFillManager.fillReport(
                compilar(),
                parametros,
                new JRBeanCollectionDataSource(
                        dao.listarOrdenadoPorInstitucion())
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
