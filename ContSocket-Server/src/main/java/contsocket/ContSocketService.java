package contsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servicio que maneja la comunicación con un cliente individual
 * Cada instancia se ejecuta en su propio thread y gestiona una conexión
 */
public class ContSocketService extends Thread {
    
    private DataInputStream in;
    private DataOutputStream out;
    private Socket tcpSocket;
    
    private static final String DELIMITER = ":";
    private static final int CAPACIDAD_INICIAL = 10000; // kg
    
    // Almacenamiento compartido entre todas las instancias (static)
    private static final Map<Long, PlantaData> plantas = new ConcurrentHashMap<>();
    
    // Inicialización estática de datos
    static {
        // Crear planta ContSocket con ID 2
        PlantaData plantaContSocket = new PlantaData(2L, "ContSocket Ltd.", CAPACIDAD_INICIAL);
        plantas.put(2L, plantaContSocket);
        System.out.println(" ✓ Planta ContSocket inicializada (ID: 2, Capacidad: " + CAPACIDAD_INICIAL + " kg)");
    }
    
    public ContSocketService(Socket socket) {
        try {
            this.tcpSocket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.err.println("# ContSocketService - TCPConnection IO error:" + e.getMessage());
        }
    }
    
    public void run() {
        try {
            String data = this.in.readUTF();
            System.out.println("   - ContSocketService - Received data from '" + 
                             tcpSocket.getInetAddress().getHostAddress() + ":" + 
                             tcpSocket.getPort() + "' -> '" + data + "'");
            
            // Procesar comando y obtener respuesta
            String response = this.processCommand(data);
            
            this.out.writeUTF(response);
            System.out.println("   - ContSocketService - Sent data to '" + 
                             tcpSocket.getInetAddress().getHostAddress() + ":" + 
                             tcpSocket.getPort() + "' -> '" + response + "'");
            
        } catch (EOFException e) {
            System.err.println("   # ContSocketService - TCPConnection EOF error" + e.getMessage());
        } catch (IOException e) {
            System.err.println("   # ContSocketService - TCPConnection IO error:" + e.getMessage());
        } finally {
            try {
                tcpSocket.close();
            } catch (IOException e) {
                System.err.println("   # ContSocketService - TCPConnection IO error:" + e.getMessage());
            }
        }
    }
    
    /**
     * Procesa el comando recibido y devuelve la respuesta
     * Formato: OPERACION:PARAMETRO1:PARAMETRO2:...
     */
    public String processCommand(String command) {
        String response = null;
        
        if (command != null && !command.trim().isEmpty()) {
            try {
                String[] parts = command.split(DELIMITER);
                String operation = parts[0];
                
                System.out.println("   - Processing operation: " + operation);
                
                switch (operation) {
                    case "CONSULTAR_CAPACIDAD":
                        response = handleConsultarCapacidad(parts);
                        break;
                    
                    case "ENVIAR_CONTENEDOR":
                        response = handleEnviarContenedor(parts);
                        break;
                    
                    case "CONSULTAR_ESTADO":
                        response = handleConsultarEstado(parts);
                        break;
                    
                    default:
                        response = "ERROR" + DELIMITER + "Operación desconocida: " + operation;
                }
                
            } catch (Exception e) {
                System.err.println("   # ContSocketService - Command processing error:" + e.getMessage());
                response = "ERROR" + DELIMITER + e.getMessage();
            }
        }
        
        return (response == null) ? "ERROR" + DELIMITER + "Invalid command" : response;
    }
    
    /**
     * Maneja CONSULTAR_CAPACIDAD:plantaId
     * Retorna: CAPACIDAD:valor o ERROR:mensaje
     */
    private String handleConsultarCapacidad(String[] parts) {
        if (parts.length < 2) {
            return "ERROR" + DELIMITER + "Falta el parámetro plantaId";
        }
        
        try {
            long plantaId = Long.parseLong(parts[1]);
            PlantaData planta = plantas.get(plantaId);
            
            if (planta == null) {
                return "ERROR" + DELIMITER + "Planta no encontrada: " + plantaId;
            }
            
            int capacidad = planta.consultarCapacidadDisponible();
            System.out.println("      → Capacidad disponible: " + capacidad + " kg");
            
            return "CAPACIDAD" + DELIMITER + capacidad;
            
        } catch (NumberFormatException e) {
            return "ERROR" + DELIMITER + "PlantaId inválido";
        }
    }
    
    /**
     * Maneja ENVIAR_CONTENEDOR:plantaId:contenedorId:capacidad:nivelLlenado
     * Retorna: OK o ERROR:mensaje
     */
    private String handleEnviarContenedor(String[] parts) {
        if (parts.length < 5) {
            return "ERROR" + DELIMITER + "Faltan parámetros";
        }
        
        try {
            long plantaId = Long.parseLong(parts[1]);
            long contenedorId = Long.parseLong(parts[2]);
            float capacidadFloat = Float.parseFloat(parts[3]);
            int capacidad = (int) capacidadFloat;
            String nivelLlenado = parts[4];
            
            PlantaData planta = plantas.get(plantaId);
            
            if (planta == null) {
                return "ERROR" + DELIMITER + "Planta no encontrada: " + plantaId;
            }
            
            // Intentar recibir el contenedor
            boolean recibido = planta.recibirContenedor(contenedorId, capacidad, nivelLlenado);
            
            if (recibido) {
                System.out.println("      📦 Contenedor " + contenedorId + " recibido");
                System.out.println("         - Capacidad: " + capacidad + " kg");
                System.out.println("         - Nivel: " + nivelLlenado);
                System.out.println("         - Capacidad restante: " + planta.consultarCapacidadDisponible() + " kg");
                System.out.println("         - Total contenedores: " + planta.getTotalContenedores());
                return "OK";
            } else {
                System.out.println("      ⚠️  Contenedor rechazado: capacidad insuficiente");
                return "ERROR" + DELIMITER + "Capacidad insuficiente";
            }
            
        } catch (NumberFormatException e) {
            return "ERROR" + DELIMITER + "Parámetros inválidos";
        }
    }
    
    /**
     * Maneja CONSULTAR_ESTADO:plantaId
     * Retorna: ESTADO:valor o ERROR:mensaje
     */
    private String handleConsultarEstado(String[] parts) {
        if (parts.length < 2) {
            return "ERROR" + DELIMITER + "Falta el parámetro plantaId";
        }
        
        try {
            long plantaId = Long.parseLong(parts[1]);
            PlantaData planta = plantas.get(plantaId);
            
            if (planta == null) {
                return "ERROR" + DELIMITER + "Planta no encontrada: " + plantaId;
            }
            
            String estado = planta.obtenerEstado();
            
            System.out.println("      → Estado: " + estado);
            System.out.println("         - Ocupación: " + planta.getCapacidadOcupada() + "/" + 
                             planta.getCapacidadTotal() + " kg (" + planta.getPorcentajeOcupacion() + "%)");
            System.out.println("         - Contenedores: " + planta.getTotalContenedores());
            
            return "ESTADO" + DELIMITER + estado;
            
        } catch (NumberFormatException e) {
            return "ERROR" + DELIMITER + "PlantaId inválido";
        }
    }
    
    // ==================== CLASES INTERNAS ====================
    
    /**
     * Clase para almacenar datos de una planta de reciclaje
     * Thread-safe usando estructuras concurrentes
     */
    private static class PlantaData {
        private final Long id;
        private final String nombre;
        private final int capacidadTotal;
        private int capacidadDisponible;
        private final List<ContenedorData> contenedores;
        
        public PlantaData(Long id, String nombre, int capacidad) {
            this.id = id;
            this.nombre = nombre;
            this.capacidadTotal = capacidad;
            this.capacidadDisponible = capacidad;
            this.contenedores = new CopyOnWriteArrayList<>();
        }
        
        /**
         * Intenta recibir un contenedor si hay capacidad disponible
         * @return true si se recibió, false si no hay capacidad
         */
        public synchronized boolean recibirContenedor(long contenedorId, int capacidad, String nivelLlenado) {
            if (capacidadDisponible < capacidad) {
                return false;
            }
            
            ContenedorData contenedor = new ContenedorData(contenedorId, capacidad, nivelLlenado);
            contenedores.add(contenedor);
            capacidadDisponible -= capacidad;
            
            return true;
        }
        
        public int consultarCapacidadDisponible() {
            return capacidadDisponible;
        }
        
        public String obtenerEstado() {
            int porcentaje = getPorcentajeOcupacion();
            
            if (porcentaje < 50) {
                return "DISPONIBLE";
            } else if (porcentaje < 80) {
                return "MEDIO";
            } else if (porcentaje < 100) {
                return "CASI_LLENO";
            } else {
                return "LLENO";
            }
        }
        
        public int getCapacidadTotal() {
            return capacidadTotal;
        }
        
        public int getCapacidadOcupada() {
            return capacidadTotal - capacidadDisponible;
        }
        
        public int getPorcentajeOcupacion() {
            return (getCapacidadOcupada() * 100) / capacidadTotal;
        }
        
        public int getTotalContenedores() {
            return contenedores.size();
        }
    }
    
    /**
     * Clase para almacenar datos de un contenedor
     */
    private static class ContenedorData {
        private final long id;
        private final int capacidad;
        private final String nivelLlenado;
        private final Date fechaRecepcion;
        
        public ContenedorData(long id, int capacidad, String nivelLlenado) {
            this.id = id;
            this.capacidad = capacidad;
            this.nivelLlenado = nivelLlenado;
            this.fechaRecepcion = new Date();
        }
    }
}