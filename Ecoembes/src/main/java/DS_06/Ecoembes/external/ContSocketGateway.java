package DS_06.Ecoembes.external;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import DS_06.Ecoembes.entity.Contenedor;
import DS_06.Ecoembes.external.IPlantaReciclajeGateway;

@Service
public class ContSocketGateway implements IPlantaReciclajeGateway {
    
    @Value("${planta.external.contsocket.host:localhost}")
    private String host;
    
    @Value("${planta.external.contsocket.port:9090}")
    private int port;
    
	private static String DELIMITER = "#";

	private static final int ERROR_CODE = -1;

    private static final int SOCKET_TIMEOUT_MS = 5000;
    public ContSocketGateway(String host, int port) {
    	this.host = host;
    	this.port = port;
	}
    
    @Override
    public int consultarCapacidadDisponible(long plantaId) {
		StringTokenizer tokenizer = null;

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            
            // Enviar comando
            String comando = "CONSULTAR_CAPACIDAD:" + plantaId;
            out.writeUTF(comando);
            //out.flush();
            
            // Leer respuesta
            String respuesta = in.readUTF();
			tokenizer = new StringTokenizer(respuesta, DELIMITER);

			if (tokenizer.hasMoreTokens() && tokenizer.nextToken().equals("OK")) {
	            if (tokenizer.hasMoreTokens()) {
	                try {
	                    return Integer.parseInt(tokenizer.nextToken());
	                } catch (NumberFormatException e) {
	                    System.err.println("Error: respuesta no es un número válido");
	                    return ERROR_CODE;
	                }
	            }
	        }
	        return ERROR_CODE;
	        
	    } catch (UnknownHostException e) {
	        System.err.println("Error: host desconocido - " + e.getMessage());
	    } catch (EOFException e) {
	        System.err.println("Error: EOF - " + e.getMessage());
	    } catch (IOException e) {
	        System.err.println("Error de IO: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("Error inesperado: " + e.getMessage());
	    }
	    
	    return ERROR_CODE;   
    }
    
    @Override
    public boolean enviarContenedor(long plantaId, Contenedor contenedor) {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {
            
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            
            // Construir mensaje
            String comando = String.format("ENVIAR_CONTENEDOR:%d:%d:%f:%s",
                plantaId, 
                contenedor.getId(),
                contenedor.getCapacidad(),
                contenedor.getNivelDeLlenado().name());
            
            out.writeUTF(comando);
            out.flush();
            
            String respuesta = in.readUTF();
            StringTokenizer tokenizer = new StringTokenizer(respuesta, DELIMITER);
            
            if (!tokenizer.hasMoreTokens()) {
                return false;
            }
            
            String estado = tokenizer.nextToken();
            if ("OK".equals(estado)) {
                return true;
            } else if ("ERROR".equals(estado) && tokenizer.hasMoreTokens()) {
                String mensajeError = tokenizer.nextToken();
                throw new RuntimeException("Error del servidor ContSocket: " + mensajeError);
            } else {
                return false;
            }
            
        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Timeout al conectar con ContSocket", e);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Host desconocido: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando contenedor por socket: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String obtenerEstado(long plantaId) {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {
            
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            
            // Enviar comando
            String comando = "CONSULTAR_ESTADO:" + plantaId;
            out.writeUTF(comando);
            out.flush();
            
            // Leer respuesta
            String respuesta = in.readUTF();
            StringTokenizer tokenizer = new StringTokenizer(respuesta, DELIMITER);
            
            if (!tokenizer.hasMoreTokens()) {
                return "DESCONOCIDO";
            }
            
            String prefijo = tokenizer.nextToken();
            if ("ESTADO".equals(prefijo) && tokenizer.hasMoreTokens()) {
                return tokenizer.nextToken();
            } else if ("ERROR".equals(prefijo) && tokenizer.hasMoreTokens()) {
                return "ERROR_" + tokenizer.nextToken();
            } else {
                return "DESCONOCIDO";
            }
            
        } catch (SocketTimeoutException e) {
            System.err.println("Timeout consultando estado: " + e.getMessage());
            return "ERROR_TIMEOUT";
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
            return "ERROR_HOST";
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return "ERROR_CONEXION";
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            return "ERROR_INESPERADO";
        }
    }
}