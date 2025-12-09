Diseño del API REST de los servicios externos de las plantas de reciclaje (descripción en texto): 
1. Módulo de Autenticación
1.1. POST /auth/login
Descripción: Iniciar sesión en el sistema
Parámetros:
•        email (String, requerido): Email del usuario
•        password (String, requerido): Contraseña del usuario
Respuestas:
200 OK - Login exitoso
{
  "token": "a1b2c3d4e5f6"
}
401 Unauthorized - Credenciales inválidas
{
  "error": "Invalid credentials"
}
1.2. POST /auth/signup
Descripción: Registrar un nuevo usuario
Parámetros:
•        nickname (String, requerido): Nombre de usuario
•        email (String, requerido): Email del usuario
•        password (String, requerido): Contraseña
Respuestas:
204 No Content - Usuario creado exitosamente
409 Conflict - El usuario ya existe
1.3. POST /auth/logout
Descripción: Cerrar sesión del sistema
Parámetros:
•        token (String): Token de autenticación
Respuestas:
204 No Content - Logout exitoso
401 Unauthorized - Token inválido
1.4. POST /auth/validate
Descripción: Comprobar si el token está activo
Parámetros:
•        token (String): Token de autenticación
Respuestas:
200 OK - Token válido
401 Unauthorized - Token inválido
2. Módulo de Contenedores
2.1. GET /reciclaje/contenedores
Descripción: Obtener todos los contenedores del sistema
Parámetros:
•        token (String, opcional): Token de autenticación
Respuestas:
200 OK - Lista de contenedores
204 No Content - No hay contenedores en el sistema
500 Internal Server Error - Error del servidor
2.2. GET /reciclaje/contenedores/{contenedorId}/llenado
Descripción: Consultar nivel de llenado histórico de un contenedor
Parámetros:
•        contenedorId (Long, path): ID del contenedor
•        date (String, query): Fecha en formato ISO-8601. Ejemplo: 2025-01-15T00:00:00.000+00:00
•        token (String, opcional): Token de autenticación
Respuestas:
200 OK - Nivel de llenado. Valores posibles: "VERDE", "AMARILLO", "ROJO"
404 Not Found - No hay datos para esa fecha
500 Internal Server Error - Error del servidor
2.3. GET /reciclaje/contenedores/zona
Descripción: Consultar contenedores por código postal y fecha
Parámetros:
•        date (String, requerido): Fecha ISO-8601
•        codigoPostal (Integer, requerido): Código postal
•        token (String, opcional): Token de autenticación
Respuestas:
200 OK - Lista de contenedores en la zona
204 No Content - No hay contenedores en esa zona/fecha
500 Internal Server Error - Error del servidor
2.4. POST /reciclaje/contenedores/{contenedorId}
Descripción: Crear un nuevo contenedor
Parámetros:
•        codigoPostal (Integer, requerido): Código postal
•        capacidad (Float, requerido): Capacidad en kg
•        token (String, requerido): Token de autenticación
Respuestas:
201 Created - Contenedor creado exitosamente
401 Unauthorized - Usuario no autenticado
409 Conflict - El contenedor ya existe
500 Internal Server Error - Error del servidor
3. Módulo de Plantas de Reciclaje
3.1. GET /reciclaje/plantasreciclaje
Descripción: Obtener todas las plantas de reciclaje
Parámetros:
•        token (String, opcional): Token de autenticación
Respuestas:
200 OK - Lista de plantas de reciclaje
204 No Content - No hay plantas de reciclaje
500 Internal Server Error - Error del servidor
3.2.POST/reciclaje/plantasreciclaje/{idPlanta}/contenedor/{idContenedor}
Descripción: Asignar contenedor a planta de reciclaje
Parámetros:
•        idPlanta (Long, path): ID de la planta
•        idContenedor (Long, path): ID del contenedor
•        token (String, query): Token de autenticación
Respuestas:
204 No Content - Contenedor asignado exitosamente
401 Unauthorized - Usuario no autenticado
404 Not Found - Contenedor o planta no encontrados
409 Conflict - Capacidad insuficiente en la planta o contenedor ya asignado
500 Internal Server Error - Error del servidor
Comportamiento especial:
•        Si la planta es tipo PLASSB: Se comunica vía HTTP REST con el servidor PlasSB (puerto 8081)
•        Si la planta es tipo CONTSOCKET: Se comunica vía TCP Sockets con el servidor ContSocket (puerto 9090)
•        Si la planta es tipo DESCONOCIDO o local: Asigna localmente sin comunicación externa
•        Si falla la comunicación con el servicio externo: Intenta asignación local como fallback
4. Integración con Servicios Externos
4.1. Arquitectura de Comunicación
El sistema Ecoembes se comunica con dos tipos de servicios externos:
•        PlasSB Server: HTTP REST API (puerto 8081) - URL: http://localhost:8081/api/plassb - Persistencia: JPA + H2
•        ContSocket Server: TCP Sockets (puerto 9090) - Protocolo: Mensajes de texto delimitados por ":" - Persistencia: En memoria
4.2. Flujo de Asignación
Proceso de asignación de contenedor a planta externa:
•        1. Cliente solicita asignar contenedor a planta
•        2. Ecoembes identifica el tipo de planta (PLASSB / CONTSOCKET / LOCAL)
•        3. Factory crea el Gateway apropiado
•        4. Gateway consulta capacidad en servicio externo
•        5. Si hay capacidad, envía el contenedor al servicio externo
•        6. Actualiza la base de datos local de Ecoembes
•        7. Retorna respuesta al cliente
4.3. Manejo de Errores
Si falla la comunicación con el servicio externo:
•        Se registra un WARNING en los logs del servidor
•        Se intenta asignación local como fallback
•        Si la asignación local también falla, se retorna error 409 al cliente


