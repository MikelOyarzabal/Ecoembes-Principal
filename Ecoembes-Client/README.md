# 🌿 Ecoembes Client - Aplicación Web

Cliente web desarrollado con **Spring Boot + Thymeleaf** para el sistema de gestión de reciclaje Ecoembes.

## 📋 Requisitos

- Java 21
- Gradle 8.x
- Servidor Ecoembes corriendo en `http://localhost:8080`
- Navegador web moderno

## 🚀 Cómo ejecutar

### Opción 1: Usando Gradle Wrapper (Recomendado)

```bash
# En Linux/Mac
./gradlew bootRun

# En Windows
gradlew.bat bootRun
```

### Opción 2: Usando Gradle instalado

```bash
gradle bootRun
```

La aplicación estará disponible en: **http://localhost:8083**

## 🔑 Credenciales de Prueba

- **Email:** `admin@ecoembes.com`
- **Contraseña:** `admin`

Otros usuarios disponibles (según DataInitializer del servidor):
- `juan.reciclaje@eco.com` / `EcoJ@n123!`
- `maria.verde@eco.com` / `V3rd3M@r1@!`
- `carlos.bio@eco.com` / `B1oC@rlos!`

## 📁 Estructura del Proyecto

```
Ecoembes-Client/
├── src/main/java/DS_06/Ecoembes/client/
│   ├── data/                    # DTOs (Contenedor, PlantaReciclaje, Credentials)
│   ├── proxies/                 # Service Proxy para comunicación HTTP
│   │   ├── IEcoembesServiceProxy.java
│   │   └── EcoembesServiceProxy.java
│   └── web/                     # Controlador web y aplicación principal
│       ├── EcoembesWebController.java
│       └── EcoembesWebClientApplication.java
├── src/main/resources/
│   ├── application.properties   # Configuración (puerto, URL servidor)
│   └── templates/               # Vistas Thymeleaf
│       ├── login.html
│       ├── dashboard.html
│       ├── contenedores.html
│       ├── nuevo-contenedor.html
│       ├── contenedores-zona.html
│       ├── plantas.html
│       ├── plantas-capacidad.html
│       └── asignaciones.html
└── build.gradle
```

## 🎯 Funcionalidades Implementadas

### ✅ Autenticación
- ✅ Login con email y contraseña
- ✅ Logout
- ✅ Gestión de sesiones con tokens

### 📦 Gestión de Contenedores
- ✅ Listar todos los contenedores
- ✅ Crear nuevo contenedor
- ✅ Consultar contenedores por zona (código postal y fecha)

### 🏭 Plantas de Reciclaje
- ✅ Listar todas las plantas
- ✅ Consultar capacidad disponible (con/sin fecha)
- ✅ Ver estadísticas y ocupación

### 🔗 Asignaciones
- ✅ Asignar múltiples contenedores a una planta
- ✅ Validación de capacidad disponible
- ✅ Interfaz visual de selección

## 🎨 Tecnologías Utilizadas

- **Backend:** Spring Boot 3.5.7
- **Vista:** Thymeleaf
- **CSS:** Bootstrap 5.3.0
- **HTTP Client:** RestTemplate
- **Sesiones:** HTTP Session Management

## ⚙️ Configuración

El archivo `application.properties` permite configurar:

```properties
# Puerto del cliente web
server.port=8083

# URL del servidor Ecoembes
ecoembes.server.url=http://localhost:8080

# Timeout de sesión
server.servlet.session.timeout=30m
```

## 📝 Patrones de Diseño Implementados

1. **Client Controller** - `EcoembesWebController` maneja todas las peticiones web
2. **Service Proxy** - `EcoembesServiceProxy` abstrae la comunicación HTTP con el servidor
3. **DTO (Data Transfer Object)** - Clases en el paquete `data/`
4. **MVC (Model-View-Controller)** - Arquitectura Spring MVC con Thymeleaf

## 🐛 Solución de Problemas

### El servidor no conecta
- Verificar que el servidor Ecoembes esté corriendo en el puerto 8080
- Revisar la configuración en `application.properties`

### Error de autenticación
- Verificar que las credenciales sean correctas
- Asegurarse de que el servidor tenga los usuarios inicializados (DataInitializer)

### Puerto en uso
- Cambiar el puerto en `application.properties`: `server.port=8084`

## 📚 Endpoints del Servidor Utilizados

El cliente consume los siguientes endpoints del servidor Ecoembes:

- `POST /auth/login` - Autenticación
- `POST /auth/logout` - Cerrar sesión
- `GET /reciclaje/contenedores` - Listar contenedores
- `POST /reciclaje/contenedores` - Crear contenedor
- `GET /reciclaje/contenedores/zona` - Contenedores por zona
- `GET /reciclaje/plantasreciclaje` - Listar plantas
- `GET /reciclaje/plantasreciclaje/{id}/capacidad` - Consultar capacidad
- `POST /reciclaje/plantasreciclaje/{id}/contenedores` - Asignar contenedores

## 🔄 Flujo de Uso

1. **Login** → Ingresar credenciales
2. **Dashboard** → Vista general del sistema
3. **Operaciones:**
   - Crear nuevos contenedores
   - Consultar estado por zonas
   - Ver plantas y su capacidad
   - Asignar contenedores a plantas

## 👥 Autores

Proyecto desarrollado para la asignatura de Diseño de Software - Universidad de Deusto

---

💡 **Nota:** Este es el **Prototipo 3** que implementa el lado cliente web con Thymeleaf según las especificaciones del proyecto.
