# Módulo de Gestión de Participantes - Sistema de Eventos y Conferencias

Este repositorio contiene el desarrollo, pruebas funcionales y empaquetado del módulo de **Gestión de Participantes** desarrollado en Java bajo el paradigma de Programación Orientada a Objetos (POO).

---

## Características y Requisitos Implementados

| Requisito / Funcionalidad | Estado | Descripción |
| :--- | :---: | :--- |
| **Cargar datos desde Aiven** | ✅ | Conexión a la base de datos MySQL en la nube (Aiven) y lectura automática al iniciar. |
| **CRUD Completo con Confirmación** | ✅ | Operaciones de registro, actualización y eliminación de participantes con diálogos de confirmación. |
| **Validaciones de Entrada** | ✅ | Control de campos obligatorios vacíos, formato de DNI (8 dígitos) y prevención de DNI duplicado. |
| **Buscador Dinámico y Filtro** | ✅ | Filtrado en tiempo real mediante caja de texto y selección de Institución mediante `ComboBox`. |
| **Reporte en JasperViewer** | ✅ | Visualización de reporte interactivo agrupado por institución con subtotales y total general. |
| **Exportación a PDF** | ✅ | Generación y exportación directa del reporte a archivo PDF. |
| **Empaquetado Standalone (.exe)** | ✅ | Distribución ejecutable mediante `jpackage` con entorno JRE embebido en `/dist/CuyConecta/`. |

---

## Empaquetado y Distribución Standalone (.exe)

Se realizó el empaquetado ejecutable del módulo (`CuyConecta.exe`) en el directorio `/dist/CuyConecta`, lo que permite ejecutar la aplicación directamente en cualquier sistema operativo Windows sin necesidad de tener Java instalado previamente.

### Estructura de la Distribución (`/dist/CuyConecta`):
- `CuyConecta.exe`: Lanzador binario nativo de Windows para la aplicación.
- `runtime/`: Entorno de ejecución Java (JRE) embebido optimizado.
- `app/`: Contiene el código fuente compilado (`.jar`) y las dependencias del proyecto.
- `config.properties`: Archivo de configuración externa para la conexión a la base de datos (Aiven).

---

## 🧪 Matriz de Casos de Prueba (Evidencias)

| Archivo | Descripción del Caso de Prueba | Resultado Esperado |
| :--- | :--- | :--- |
| **TAREA1.webp** | Carga inicial del módulo | La tabla muestra automáticamente los participantes de prueba almacenados en Aiven. |
| **TAREA2.webp** | Estado inicial | Vista limpia del formulario y la tabla lista para operar. |
| **TAREA3.webp** | Confirmación de eliminación | Se despliega diálogo emergente solicitando confirmación indicando el nombre y DNI. |
| **TAREA4.webp** | Eliminación del registro | Mensaje de éxito tras eliminar el registro de la BD y la interfaz. |
| **TAREA5.webp** | Registro de participante válido | Mensaje de éxito indicando el ID asignado y actualización inmediata de la tabla. |
| **TAREA6.webp** | Limpieza del formulario | Restablecimiento de los campos del formulario para un nuevo ingreso. |
| **TAREA7.webp** | Registro con campos vacíos | Alerta de validación listando los campos obligatorios incompletos. |
| **TAREA8.webp** | Actualización de registro | Mensaje informativo confirmando que la edición fue exitosa. |
| **TAREA9.webp** | Registro con DNI inválido | Alerta indicando que el DNI debe ser numérico y de exactamente 8 dígitos. |
| **TAREA10.webp** | Selección de fila en la tabla | Carga automática de los datos del registro seleccionado en el formulario. |
| **TAREA11.webp** | Búsqueda dinámica | La tabla filtra los resultados automáticamente a medida que se escribe. |
| **TAREA12.webp** | Registro con DNI duplicado | Alerta bloqueando el registro al detectar un DNI ya existente en Aiven. |
| **TAREA13.webp** | Filtro por ComboBox | La tabla actualiza su contenido mostrando solo participantes de la institución elegida. |
| **TAREA14.webp** | Filtro combinado | Filtrado en tiempo real combinando texto en el buscador e institución en el ComboBox. |
| **TAREA15.webp** | Botón Refrescar | Restablece los filtros aplicados y recarga la lista de participantes. |
| **TAREA16.webp** | Visualización de reporte | Apertura de `JasperViewer` agrupado por institución con subtotales y total general. |
| **TAREA17.webp** | Exportación a PDF | Generación exitosa del archivo PDF listo para su apertura. |

---

## 📸 Evidencias Gráficas

| Archivo | Captura de Pantalla |
| :--- | :--- |
| **TAREA1.webp** | ![](docs/evidencias/TAREA1.webp) |
| **TAREA2.webp** | ![](docs/evidencias/TAREA2.webp) |
| **TAREA3.webp** | ![](docs/evidencias/TAREA3.webp) |
| **TAREA4.webp** | ![](docs/evidencias/TAREA4.webp) |
| **TAREA5.webp** | ![](docs/evidencias/TAREA5.webp) |
| **TAREA6.webp** | ![](docs/evidencias/TAREA6.webp) |
| **TAREA7.webp** | ![](docs/evidencias/TAREA7.webp) |
| **TAREA8.webp** | ![](docs/evidencias/TAREA8.webp) |
| **TAREA9.webp** | ![](docs/evidencias/TAREA9.webp) |
| **TAREA10.webp** | ![](docs/evidencias/TAREA10.webp) |
| **TAREA11.webp** | ![](docs/evidencias/TAREA11.webp) |
| **TAREA12.webp** | ![](docs/evidencias/TAREA12.webp) |
| **TAREA13.webp** | ![](docs/evidencias/TAREA13.webp) |
| **TAREA14.webp** | ![](docs/evidencias/TAREA14.webp) |
| **TAREA15.webp** | ![](docs/evidencias/TAREA15.webp) |
| **TAREA16.webp** | ![](docs/evidencias/TAREA16.webp) |
| **TAREA17.webp** | ![](docs/evidencias/TAREA17.webp) |
