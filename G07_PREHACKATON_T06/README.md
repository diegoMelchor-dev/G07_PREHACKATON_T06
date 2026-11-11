# G01_PREHACKATON_T06 — Gestión de Eventos

Aplicación de escritorio JavaFX + MySQL para el módulo **Gestión de Eventos**. Incluye CRUD, búsqueda, filtros, validaciones y exportación de reporte PDF con JasperReports.

## Requisitos
- JDK 21
- IntelliJ IDEA con soporte Maven
- Docker Desktop (recomendado) o MySQL 8+
- Conexión a Internet la primera vez para descargar dependencias Maven

## 1. Crear la base de datos
1. Inicia Docker Desktop.
2. Ejecuta en PowerShell:

```powershell
docker volume create t06_mysql_data
docker run -d --name t06-mysql --restart unless-stopped -p 3307:3306 -e MYSQL_ROOT_PASSWORD="T06_Mysql2026!" -e MYSQL_DATABASE=db_eventos -v t06_mysql_data:/var/lib/mysql mysql:8.4
```

Si ya existe el contenedor `t06-mysql`, no lo vuelvas a crear. Inícialo con:

```powershell
docker start t06-mysql
```

3. Abre MySQL Workbench y conecta a:
   - Host: `127.0.0.1`
   - Port: `3307`
   - User: `root`
   - Password: `T06_Mysql2026!`
4. Abre `database/db_eventos.sql` y ejecútalo completo.

> El script crea la base `db_eventos` y las tres tablas requeridas en el diseño: `eventos`, `participantes` e `inscripciones`. La interfaz de esta entrega implementa el módulo asignado: eventos.

## 2. Abrir y ejecutar
1. Descomprime el ZIP.
2. En IntelliJ selecciona **Open** y abre la carpeta que contiene `pom.xml`.
3. Espera a que Maven descargue las dependencias y pulsa **Reload All Maven Projects** si lo solicita.
4. Ejecuta `MainApp` desde Maven: `Plugins > javafx > javafx:run`, o desde terminal:

```powershell
mvn clean javafx:run
```

También puedes usar el botón Maven de IntelliJ. Se requiere Maven instalado o integrado por IntelliJ.

## 3. Configurar MySQL (si usas otros datos)
La conexión se configura en `src/main/resources/db.properties`. También puedes establecer variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`, que tienen prioridad sobre el archivo.

## 4. Funcionalidades
- Registrar evento.
- Listar eventos desde MySQL.
- Seleccionar un registro y cargarlo al formulario.
- Actualizar evento existente.
- Eliminar físicamente con confirmación.
- Buscar por nombre o lugar.
- Filtrar por estado.
- Validar campos obligatorios, longitudes y duplicados (nombre + fecha + lugar).
- Exportar el listado filtrado a PDF mediante JasperReports.

## 5. Reporte PDF
El botón **Generar PDF** abre un diálogo para guardar el archivo. El reporte usa los datos visibles en la tabla (incluye filtros aplicados).

## 6. Estructura
```text
src/main/java/vallegrande/edu/pe/g01_prehackaton_t06/
├── MainApp.java
├── controller/HelloController.java
├── model/Evento.java
├── dao/EventoDAO.java
├── database/Conexion.java
└── report/ReporteService.java
src/main/resources/
├── db.properties
├── .../hello-view.fxml
└── reports/eventos.jrxml
database/db_eventos.sql
```

## Nota de entrega
Este ZIP contiene el código fuente y configuración del módulo de Gestión de Eventos. La ejecución real depende de tener JDK 21, MySQL/Docker iniciado y las dependencias Maven descargadas. Antes de entregar, ejecuta la aplicación en tu equipo y captura evidencias reales de CRUD, búsqueda/filtros y PDF. No se incluyen capturas ficticias ni datos de acceso a servicios externos.
