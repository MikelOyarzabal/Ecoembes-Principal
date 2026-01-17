# 🌿 Ecoembes Client - Aplicación Web (Actualizado)

Cliente web desarrollado con **Spring Boot + Thymeleaf** para el sistema de gestión de reciclaje Ecoembes.

## ✅ Funcionalidades Implementadas

| Requisito | Estado | Descripción |
|-----------|--------|-------------|
| Login | ✅ | Autenticación con email y contraseña |
| Crear contenedor | ✅ | Con opción de enviar email de confirmación |
| Actualizar contenedor | ✅ | Formulario de edición (+ nota para usar Swagger/Postman) |
| Consultar zona | ✅ | Estado de contenedores por código postal y fecha con estadísticas |
| Consultar capacidad plantas | ✅ | Con alertas de saturación |
| Asignar contenedores | ✅ | Asignación múltiple con validación de capacidad |
| Notificación post-asignación | ✅ | Email con resumen: total contenedores y envases estimados |
| Alerta por saturación | ✅ | Cuando planta supera 75% de ocupación |
| Logout | ✅ | Cierre de sesión |

## 📋 Requisitos

- Java 21
- Gradle 8.x
- Servidor Ecoembes corriendo en `http://localhost:8080`

## 🚀 Cómo ejecutar

```bash
# En Linux/Mac
./gradlew bootRun

# En Windows
gradlew.bat bootRun
```

La aplicación estará disponible en: **http://localhost:8083**

## 📧 Configuración de Email (Gmail)

### Opción 1: Variables de entorno (Recomendado)

```bash
export GMAIL_USERNAME=tu-email@gmail.com
export GMAIL_APP_PASSWORD=tu-app-password
export EMAIL_ENABLED=true
```

### Opción 2: Editar application.properties

```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
ecoembes.email.enabled=true
```

### Obtener App Password de Gmail:
1. Ve a https://myaccount.google.com/security
2. Activa la verificación en 2 pasos
3. Ve a "Contraseñas de aplicaciones"
4. Genera una nueva contraseña para "Correo" en "Otro (nombre personalizado)"
5. Usa esa contraseña de 16 caracteres

### Modo de prueba (sin enviar emails reales):
Si `ecoembes.email.enabled=false`, los emails se "simulan" y se muestran en la consola.

## 🔑 Credenciales de Prueba

- **Email:** `admin@ecoembes.com`
- **Contraseña:** `admin`

## 📁 Estructura del Proyecto

```
Ecoembes-Client/
├── src/main/java/DS_06/Ecoembes/client/
│   ├── data/                    # DTOs
│   │   ├── Contenedor.java
│   │   ├── PlantaReciclaje.java
│   │   ├── Credentials.java
│   │   └── ResumenAsignacion.java   # NUEVO
│   ├── proxies/                 # Service Proxy
│   │   ├── IEcoembesServiceProxy.java
│   │   └── EcoembesServiceProxy.java
│   ├── service/                 # NUEVO
│   │   └── EmailService.java    # Servicio de email con JavaMail
│   └── web/
│       ├── EcoembesWebController.java  # Actualizado
│       └── EcoembesWebClientApplication.java
├── src/main/resources/
│   ├── application.properties   # Configuración con email
│   └── templates/
│       ├── login.html
│       ├── dashboard.html           # Actualizado con alertas
│       ├── contenedores.html        # Actualizado con edición
│       ├── nuevo-contenedor.html    # Actualizado con email
│       ├── editar-contenedor.html   # NUEVO
│       ├── contenedores-zona.html   # Actualizado con estadísticas
│       ├── plantas.html             # Actualizado con alertas
│       ├── plantas-capacidad.html
│       ├── asignaciones.html        # Actualizado con notificaciones
│       └── confirmacion-asignacion.html  # NUEVO
└── build.gradle                 # Actualizado con spring-boot-starter-mail
```

## 🎯 Flujo de Uso Completo

1. **Login** → Ingresar credenciales
2. **Crear contenedor** → Opcionalmente enviar email de confirmación
3. **Editar contenedor** → (Nota: usar Swagger/Postman para persistir cambios)
4. **Consultar zona** → Ver estado de contenedores con estadísticas
5. **Consultar capacidad** → Ver plantas con alertas de saturación
6. **Asignar contenedores** → Seleccionar múltiples, validar capacidad
7. **Confirmación** → Ver resumen con:
   - Total de contenedores
   - Capacidad asignada
   - **Envases estimados** (~50 envases/kg)
   - Capacidad restante en planta
8. **Alerta automática** → Si planta supera 75% de ocupación
9. **Logout** → Cerrar sesión

## 📊 Estimación de Envases

La aplicación estima la cantidad de envases basándose en:
- **1 kg de plástico ≈ 50 envases** (promedio)

Esta estimación se muestra en:
- Página de confirmación de asignación
- Email de notificación

## ⚙️ Configuración de Alertas

```properties
# Umbral de saturación (porcentaje)
ecoembes.alerta.umbral-saturacion=75
```

Cuando una planta supera este umbral:
1. Se muestra alerta visual en el dashboard
2. Se resalta la planta en la lista
3. Se envía email de alerta automática tras asignación

## 📝 Endpoints del Servidor Requeridos

El cliente consume los siguientes endpoints:

- `POST /auth/login` - Autenticación
- `POST /auth/logout` - Cerrar sesión
- `GET /reciclaje/contenedores` - Listar contenedores
- `POST /reciclaje/contenedores` - Crear contenedor
- `PUT /reciclaje/contenedores/{id}` - Actualizar contenedor (opcional)
- `GET /reciclaje/contenedores/zona` - Contenedores por zona
- `GET /reciclaje/plantasreciclaje` - Listar plantas
- `GET /reciclaje/plantasreciclaje/{id}/capacidad` - Consultar capacidad
- `POST /reciclaje/plantasreciclaje/{id}/contenedores` - Asignar contenedores

## 🔧 Notas Técnicas

### Actualización de Contenedores
El endpoint PUT para actualizar contenedores puede no existir en el servidor actual. 
En ese caso:
1. La aplicación simula la actualización localmente
2. Muestra un mensaje indicando usar Swagger UI o Postman
3. Swagger UI disponible en: http://localhost:8080/swagger-ui.html

### Emails en Desarrollo
Por defecto, `ecoembes.email.enabled=false` para desarrollo.
Los emails se simulan y se muestran en la consola del servidor.

---

💡 **Proyecto actualizado** - Cumple con todos los requisitos del prototipo 3.
