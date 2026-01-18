package DS_06.Ecoembes.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${ecoembes.notification.email:notificaciones@ecoembes.com}")
    private String fromEmail;
    
    @Value("${ecoembes.email.enabled:false}")
    private boolean emailEnabled;
    
    /**
     * Envía notificación de creación de contenedor
     */
    public boolean enviarNotificacionCreacionContenedor(String destinatario, long contenedorId, 
            int codigoPostal, float capacidad) {
        
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EMAIL SIMULADO] Notificación de creación de contenedor:");
            System.out.println("  - Destinatario: " + destinatario);
            System.out.println("  - Contenedor ID: " + contenedorId);
            System.out.println("  - Código Postal: " + codigoPostal);
            System.out.println("  - Capacidad: " + capacidad + " kg");
            return true;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("🆕 Nuevo Contenedor Creado - Ecoembes");
            
            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                padding: 20px; border-radius: 10px; color: white;">
                        <h1>♻️ Ecoembes - Nuevo Contenedor</h1>
                    </div>
                    <div style="padding: 20px; background: #f8f9fa; border-radius: 10px; margin-top: 20px;">
                        <h2>Contenedor Creado Exitosamente</h2>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>ID del Contenedor:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;">%d</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>Código Postal:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;">%d</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>Capacidad:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;">%.2f kg</td>
                            </tr>
                        </table>
                    </div>
                    <p style="color: #6c757d; font-size: 12px; margin-top: 20px;">
                        Este es un mensaje automático del sistema Ecoembes.
                    </p>
                </body>
                </html>
                """.formatted(contenedorId, codigoPostal, capacidad);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("[EMAIL] Notificación de creación enviada a: " + destinatario);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("[EMAIL ERROR] Error enviando notificación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía notificación de asignación de contenedores a planta
     */
    public boolean enviarNotificacionAsignacion(String destinatario, String nombrePlanta, 
            int totalContenedores, float capacidadTotalAsignada, int capacidadRestantePlanta) {
        
        int envasesEstimados = (int) (capacidadTotalAsignada * 50);
        
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EMAIL SIMULADO] Notificación de asignación:");
            System.out.println("  - Destinatario: " + destinatario);
            System.out.println("  - Planta: " + nombrePlanta);
            System.out.println("  - Total Contenedores: " + totalContenedores);
            System.out.println("  - Capacidad Asignada: " + capacidadTotalAsignada + " kg");
            System.out.println("  - Envases Estimados: " + envasesEstimados);
            System.out.println("  - Capacidad Restante: " + capacidadRestantePlanta + " kg");
            return true;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("📦 Asignación Completada - " + totalContenedores + " Contenedores - Ecoembes");
            
            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                padding: 20px; border-radius: 10px; color: white;">
                        <h1>♻️ Ecoembes - Asignación Completada</h1>
                    </div>
                    <div style="padding: 20px; background: #d4edda; border-radius: 10px; margin-top: 20px; border-left: 4px solid #28a745;">
                        <h2 style="color: #155724;">✅ Asignación Exitosa</h2>
                        <p>Se han asignado contenedores a la planta de reciclaje.</p>
                    </div>
                    <div style="padding: 20px; background: #f8f9fa; border-radius: 10px; margin-top: 20px;">
                        <h3>📊 Resumen de la Asignación</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>Planta de Destino:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>Total Contenedores:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><span style="font-size: 1.2em; color: #007bff;">%d</span></td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>Capacidad Total Asignada:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;">%.2f kg</td>
                            </tr>
                            <tr style="background: #e7f3ff;">
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><strong>📦 Envases Estimados:</strong></td>
                                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><span style="font-size: 1.3em; color: #28a745; font-weight: bold;">~%,d envases</span></td>
                            </tr>
                            <tr>
                                <td style="padding: 10px;"><strong>Capacidad Restante en Planta:</strong></td>
                                <td style="padding: 10px;">%,d kg</td>
                            </tr>
                        </table>
                    </div>
                    <p style="color: #6c757d; font-size: 12px; margin-top: 20px;">
                        Este es un mensaje automático del sistema Ecoembes.
                    </p>
                </body>
                </html>
                """.formatted(nombrePlanta, totalContenedores, capacidadTotalAsignada, 
                             envasesEstimados, capacidadRestantePlanta);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("[EMAIL] Notificación de asignación enviada a: " + destinatario);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("[EMAIL ERROR] Error enviando notificación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía alerta de saturación de planta
     */
    public boolean enviarAlertaSaturacion(String destinatario, String nombrePlanta, 
            int porcentajeOcupacion, int capacidadDisponible) {
        
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EMAIL SIMULADO] ⚠️ ALERTA DE SATURACIÓN:");
            System.out.println("  - Destinatario: " + destinatario);
            System.out.println("  - Planta: " + nombrePlanta);
            System.out.println("  - Ocupación: " + porcentajeOcupacion + "%");
            System.out.println("  - Capacidad Disponible: " + capacidadDisponible + " kg");
            return true;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("⚠️ ALERTA: Planta " + nombrePlanta + " al " + porcentajeOcupacion + "% de capacidad");
            helper.setPriority(1);
            
            String colorAlerta = porcentajeOcupacion >= 90 ? "#dc3545" : "#ffc107";
            String nivelAlerta = porcentajeOcupacion >= 90 ? "CRÍTICO" : "ADVERTENCIA";
            
            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <div style="background: %s; padding: 20px; border-radius: 10px; color: white;">
                        <h1>⚠️ ALERTA DE SATURACIÓN - %s</h1>
                    </div>
                    <div style="padding: 20px; background: #fff3cd; border-radius: 10px; margin-top: 20px; border-left: 4px solid %s;">
                        <h2>La planta %s está alcanzando su capacidad máxima</h2>
                        <div style="text-align: center; margin: 20px 0;">
                            <div style="font-size: 3em; font-weight: bold; color: %s;">%d%%</div>
                            <div style="font-size: 1.2em;">de ocupación</div>
                        </div>
                        <p><strong>Capacidad disponible:</strong> %,d kg</p>
                    </div>
                    <div style="padding: 20px; background: #f8f9fa; border-radius: 10px; margin-top: 20px;">
                        <h3>Acciones Recomendadas:</h3>
                        <ul>
                            <li>Revisar la programación de recogida</li>
                            <li>Considerar redistribuir contenedores a otras plantas</li>
                            <li>Verificar el estado de procesamiento de la planta</li>
                        </ul>
                    </div>
                    <p style="color: #6c757d; font-size: 12px; margin-top: 20px;">
                        Este es un mensaje automático de alerta del sistema Ecoembes.
                    </p>
                </body>
                </html>
                """.formatted(colorAlerta, nivelAlerta, colorAlerta, nombrePlanta, 
                             colorAlerta, porcentajeOcupacion, capacidadDisponible);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("[EMAIL] ⚠️ Alerta de saturación enviada a: " + destinatario);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("[EMAIL ERROR] Error enviando alerta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía email simple de texto
     */
    public boolean enviarEmailSimple(String destinatario, String asunto, String mensaje) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("[EMAIL SIMULADO] Email simple:");
            System.out.println("  - Para: " + destinatario);
            System.out.println("  - Asunto: " + asunto);
            System.out.println("  - Mensaje: " + mensaje);
            return true;
        }
        
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);
            
            mailSender.send(email);
            return true;
            
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] " + e.getMessage());
            return false;
        }
    }
    
    public boolean isEmailEnabled() {
        return emailEnabled && mailSender != null;
    }
}
