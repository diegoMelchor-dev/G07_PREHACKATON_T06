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

## Matriz de Casos de Prueba (Evidencias)

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

## Evidencias Gráficas

| Archivo | Captura de Pantalla |
| :--- | :--- |
| **TAREA1.webp** | ![](docs/evidencias/TAREA1.webp) |<img width="1135" height="728" alt="image" src="https://github.com/user-attachments/assets/ccc5e606-c49c-4449-9dfb-cadd22852b61" />

| **TAREA2.webp** | ![](docs/evidencias/TAREA2.webp) |<img width="1135" height="728" alt="image" src="https://github.com/user-attachments/assets/55ad9a1d-734f-4257-91bc-6a7db1c05a27" />

| **TAREA3.webp** | ![](docs/evidencias/TAREA3.webp) |<img width="567" height="393" alt="image" src="https://github.com/user-attachments/assets/e67b5f2b-af16-49e1-a408-bc411b217b32" />

| **TAREA4.webp** | ![](docs/evidencias/TAREA4.webp) |<img width="887" height="613" alt="image" src="https://github.com/user-attachments/assets/b3269065-60f3-45b7-aeb2-3c93a97655f8" />

| **TAREA5.webp** | ![](docs/evidencias/TAREA5.webp) |<img width="1101" height="765" alt="image" src="https://github.com/user-attachments/assets/ef5acbbb-e775-4d00-941b-a19b0441ec2c" />

| **TAREA6.webp** | ![](docs/evidencias/TAREA6.webp) |<img width="996" height="540" alt="image" src="https://github.com/user-attachments/assets/739c5523-a0e6-419c-94ff-749d4e521eb7" />

| **TAREA7.webp** | ![](docs/evidencias/TAREA7.webp) |<img width="624" height="466" alt="image" src="https://github.com/user-attachments/assets/8250d5b4-995c-45fd-939d-1878682a4cbd" />

| **TAREA8.webp** | ![](docs/evidencias/TAREA8.webp) |<img width="557" height="300" alt="image" src="https://github.com/user-attachments/assets/a9f7834c-072e-4f8c-a4d6-6ea09d0c391a" />

| **TAREA9.webp** | ![](docs/evidencias/TAREA9.webp) |<img width="801" height="394" alt="image" src="https://github.com/user-attachments/assets/1d780be3-4b99-47e7-b70f-7d15cca4fe10" />

| **TAREA10.webp** | ![](docs/evidencias/TAREA10.webp) |<img width="1068" height="439" alt="image" src="https://github.com/user-attachments/assets/b4cb0130-b5e6-404c-b07a-2937dcaf62c6" />

| **TAREA11.webp** | ![](docs/evidencias/TAREA11.webp) |<img width="1049" height="655" alt="image" src="https://github.com/user-attachments/assets/e76596d6-9b85-4c0f-85c5-4c252647851d" />

| **TAREA12.webp** | ![](docs/evidencias/TAREA12.webp) |<img width="1049" height="655" alt="image" src="https://github.com/user-attachments/assets/562ae241-2db2-4b57-8914-ab3004828eb1" />

| **TAREA13.webp** | ![](docs/evidencias/TAREA13.webp) |<img width="1041" height="345" alt="image" src="https://github.com/user-attachments/assets/b0849251-8747-4c45-bcb4-c72b4c23540c" />

| **TAREA14.webp** | ![](docs/evidencias/TAREA14.webp) |<img width="1128" height="516" alt="image" src="https://github.com/user-attachments/assets/e887f33e-51f5-4105-9f31-75a0b91fa6b3" />

| **TAREA15.webp** | ![](docs/evidencias/TAREA15.webp) |<img width="926" height="462" alt="image" src="https://github.com/user-attachments/assets/8e50202f-4536-451b-be68-a777febfa5b5" />

| **TAREA16.webp** | ![](docs/evidencias/TAREA16.webp) |<img width="1522" height="784" alt="image" src="https://github.com/user-attachments/assets/5fbf1379-5575-4e13-be8c-26162439e660" />

| **TAREA17.webp** | ![](docs/evidencias/TAREA17.webp) |<img width="1456" height="816" alt="image" src="https://github.com/user-attachments/assets/d95e2825-63bb-4712-ae56-e468de960527" />

