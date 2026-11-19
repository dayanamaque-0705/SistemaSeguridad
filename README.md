# UCB Segura

**Universidad Católica Boliviana "San Pablo" • Campus Tupuraya (Gestión 2026)**

---

## 1. Resumen del Proyecto

UCB Segura es un sistema de monitoreo y alertas de seguridad en tiempo real diseñado para los estudiantes de la Universidad Católica Boliviana en el Campus Tupuraya (Cochabamba). Su propósito es mitigar la vulnerabilidad generada por la deficiencia en el alumbrado público, la presencia de grupos en la vía pública y la falta de herramientas tecnológicas de prevención, permitiendo a la comunidad estudiantil desplazarse con mayor tranquilidad.

## 2. Objetivos

### 2.1 Objetivo General
Desarrollar un sistema de monitoreo y alertas de seguridad para los estudiantes universitarios en las zonas circundantes de la Universidad Católica Boliviana San Pablo durante la gestión 2026.

### 2.2 Objetivos Específicos
- Módulo de autenticación exclusivo para correos institucionales UCB (@ucb.edu.bo o @estudiantes.ucb.edu.bo).
- Módulo de reportes ciudadanos inmediatos para incidentes, riñas o presencia sospechosa.
- Mapa interactivo con zonas de riesgo y áreas de mayor vulnerabilidad.
- Sistema de alertas de emergencia en tiempo real (botón de pánico SOS).

## 3. Alcances y Límites

- **Alcance**: Aplicación multiplataforma orientada a estudiantes UCB, registro institucional obligatorio, reportes de incidentes, mapa de calor/riesgo y botón de pánico SOS.
- **Límites**: Acceso exclusivo para miembros UCB (sin transeúntes externos); sin integración directa con la Policía Nacional; cobertura restringida al perímetro del Campus Tupuraya y rutas de tránsito estudiantil; rastreo GPS activo únicamente durante la emergencia.

## 4. Arquitectura y Tecnologías

- **Lenguaje y UI**: Kotlin Multiplatform, Compose Multiplatform, Material Design 3.
- **Mapas**: osmdroid centrado en Campus Tupuraya (-17.3714, -66.1432).
- **Backend y Mensajería**: Firebase Realtime Database, Firebase Cloud Messaging (FCM), Firebase Authentication.
- **Estructura**: Organización modular por capas (model, application, data, ui) y funcionalidades (auth, home, panic_alert, profile, reports).

## 5. Configuración Inicial

1. Clonar el repositorio y abrir el proyecto en Android Studio.
2. Colocar el archivo `google-services.json` en el directorio de `androidApp`.
