package contsocket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Servidor ContSocket Ltd. - Planta de reciclaje
 * Acepta conexiones de clientes y delega cada una a un ContSocketService
 */
public class ContSocketServer {
    
    private static final int DEFAULT_PORT = 9090;
    private static int numClients = 0;
    
    public static void main(String[] args) {
        int serverPort = DEFAULT_PORT;
        
        // El puerto es OPCIONAL - si no se proporciona, usa el por defecto
        if (args.length > 0) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("# ContSocketServer: Puerto inválido '" + args[0] + "', usando " + DEFAULT_PORT);
            }
        } else {
            System.out.println(" - ContSocketServer: No port specified, using default port " + DEFAULT_PORT);
        }
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   ContSocket Ltd. Server Started      ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        try (ServerSocket tcpServerSocket = new ServerSocket(serverPort)) {
            System.out.println(" - ContSocketServer: Waiting for connections '" + 
                             tcpServerSocket.getInetAddress().getHostAddress() + 
                             ":" + tcpServerSocket.getLocalPort() + "' ...");
            System.out.println(" - Planta ID: 2");
            System.out.println(" - Capacidad inicial: 1000 kg\n");
            
            while (true) {
                new ContSocketService(tcpServerSocket.accept());
                System.out.println(" - ContSocketServer: New client connection accepted. Client number: " + ++numClients);
            }
            
        } catch (IOException e) {
            System.err.println("# ContSocketServer: IO error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}