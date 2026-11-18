# UCB Segura - Sistema de Monitoreo y Alertas de Seguridad

**Universidad Católica Boliviana San Pablo • Campus Tupuraya (Gestión 2026)**

UCB Segura es un sistema de monitoreo y emisión de alertas de seguridad en tiempo real diseñado para estudiantes universitarios en las zonas circundantes del campus Tupuraya de la UCB Cochabamba.

---

## 📌 Funcionalidades Principales

1. **🔐 Autenticación y Registro Institucional Exclusivo**:
   - Registro restringido únicamente a direcciones de correo institucional UCB (`@ucb.edu.bo`, `@estudiantes.ucb.edu.bo`, `@est.ucb.edu.bo`, `@cba.ucb.edu.bo`).
   - Acceso seguro mediante contraseña y carné institucional UCB.

2. **🗺️ Mapa Interactivo de Zonas de Riesgo en Vivo**:
   - Mapa nítido renderizado con **OpenStreetMap (osmdroid)**.
   - Polígonos de riesgo codificados por colores:
     - 🔴 **Alto Riesgo**: Pasajes oscuros y vías de alto peligro nocturno.
     - 🟠 **Riesgo Medio**: Paradas de transporte y zonas con aglomeraciones.
     - 🟡 **Riesgo Bajo**: Áreas exteriores con precaución estándar.
     - 🟢 **Zona Segura**: Accesos principales con vigilancia 24/7.
   - **Punto Azul de Ubicación en Tiempo Real (`📍 Aquí estoy`)** con botón de enfoque GPS.

3. **📋 Módulo de Reportes de Incidentes y Selección en Mapa**:
   - Emisión de notificaciones sobre *Robo/Asalto*, *Riña*, *Falta de luz* o *Persona sospechosa*.
   - **Seleccionador de ubicación dinámico en mapa**: Toca cualquier punto del campus para fijar la ubicación exacta con un pin rojo objetivo.
   - Sincronización instantánea con el mapa de seguridad y la lista de *Mis Reportes*.

4. **🚨 Botón de Pánico (SOS) y Alertas de Emergencia**:
   - Emisión de alerta de emergencia en tiempo real para notificar a la comunidad UCB.
   - Rastreo GPS temporal activado exclusivamente durante la emisión de la alerta.

---

## 🛠️ Tecnología y Arquitectura

- **Lenguaje**: Kotlin (Multiplatform / Jetpack Compose Multiplatform)
- **UI Framework**: Compose Multiplatform & Material Design 3
- **Mapas**: OpenStreetMap (`org.osmdroid:osmdroid-android`)
- **Plataformas Soportadas**: Android (Móvil) & Web (Wasm)
