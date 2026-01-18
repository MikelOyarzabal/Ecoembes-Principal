package DS_06.Ecoembes.client.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import DS_06.Ecoembes.client.data.Contenedor;
import DS_06.Ecoembes.client.data.PlantaReciclaje;
import DS_06.Ecoembes.client.data.ResumenAsignacion;
import DS_06.Ecoembes.client.proxies.IEcoembesServiceProxy;
import DS_06.Ecoembes.client.service.EmailService;
import jakarta.servlet.http.HttpSession;

@Controller
public class EcoembesWebController {
    
    private final IEcoembesServiceProxy serviceProxy;
    private final EmailService emailService;
    
    @Value("${ecoembes.alerta.umbral-saturacion:75}")
    private int umbralSaturacion;
    
    public EcoembesWebController(IEcoembesServiceProxy serviceProxy, EmailService emailService) {
        this.serviceProxy = serviceProxy;
        this.emailService = emailService;
    }
    
    // ============ AUTENTICACIÓN ============
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }
    
    @PostMapping("/login")
    public String login(
            @RequestParam String email, 
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        String token = serviceProxy.login(email, password);
        
        if (token != null && !token.isEmpty()) {
            session.setAttribute("token", token);
            session.setAttribute("email", email);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token != null) {
            serviceProxy.logout(token);
        }
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada correctamente");
        return "redirect:/login";
    }
    
    // ============ DASHBOARD ============
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("email", session.getAttribute("email"));
        
        List<Contenedor> contenedores = serviceProxy.getAllContenedores(token);
        List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
        
        model.addAttribute("totalContenedores", contenedores.size());
        model.addAttribute("totalPlantas", plantas.size());
        
        // Verificar alertas de saturación
        List<PlantaReciclaje> plantasSaturadas = new ArrayList<>();
        for (PlantaReciclaje planta : plantas) {
            if (planta.estaSaturada(umbralSaturacion)) {
                plantasSaturadas.add(planta);
            }
        }
        model.addAttribute("plantasSaturadas", plantasSaturadas);
        model.addAttribute("hayAlertaSaturacion", !plantasSaturadas.isEmpty());
        
        return "dashboard";
    }
    
    // ============ CONTENEDORES ============
    
    @GetMapping("/contenedores")
    public String listarContenedores(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        List<Contenedor> contenedores = serviceProxy.getAllContenedores(token);
        model.addAttribute("contenedores", contenedores);
        model.addAttribute("email", session.getAttribute("email"));
        
        return "contenedores";
    }
    
    @GetMapping("/contenedores/nuevo")
    public String nuevoContenedorForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("email", session.getAttribute("email"));
        return "nuevo-contenedor";
    }
    
    @PostMapping("/contenedores/crear")
    public String crearContenedor(
            @RequestParam int codigoPostal,
            @RequestParam float capacidad,
            @RequestParam(required = false, defaultValue = "false") boolean enviarEmail,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        String email = (String) session.getAttribute("email");
        Contenedor nuevo = serviceProxy.crearContenedor(token, codigoPostal, capacidad);
        
        if (nuevo != null) {
            String mensaje = "Contenedor creado exitosamente con ID: " + nuevo.getId();
            
            // Enviar email si se solicita
            if (enviarEmail && email != null) {
                boolean emailEnviado = emailService.enviarNotificacionCreacionContenedor(
                    email, nuevo.getId(), codigoPostal, capacidad);
                
                if (emailEnviado) {
                    mensaje += ". Notificación enviada a " + email;
                } else {
                    mensaje += ". (No se pudo enviar la notificación por email)";
                }
            }
            
            redirectAttributes.addFlashAttribute("success", mensaje);
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al crear el contenedor");
        }
        
        return "redirect:/contenedores";
    }
    
    // ============ EDITAR/ACTUALIZAR CONTENEDOR ============
    
    @GetMapping("/contenedores/editar/{id}")
    public String editarContenedorForm(
            @PathVariable("id") long contenedorId,
            HttpSession session, 
            Model model) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        Contenedor contenedor = serviceProxy.getContenedorById(token, contenedorId);
        
        if (contenedor == null) {
            return "redirect:/contenedores";
        }
        
        model.addAttribute("contenedor", contenedor);
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("nivelesLlenado", List.of("VERDE", "AMARILLO", "ROJO", "LLENO"));
        
        return "editar-contenedor";
    }
    
    @PostMapping("/contenedores/actualizar")
    public String actualizarContenedor(
            @RequestParam long id,
            @RequestParam int codigoPostal,
            @RequestParam float capacidad,
            @RequestParam String nivelLlenado,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        Contenedor actualizado = serviceProxy.actualizarContenedor(token, id, codigoPostal, capacidad, nivelLlenado);
        
        if (actualizado != null) {
            redirectAttributes.addFlashAttribute("success", 
                "Contenedor " + id + " actualizado. Nota: Para cambios persistentes, " +
                "usar Swagger UI en http://localhost:8080/swagger-ui.html o Postman.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el contenedor");
        }
        
        return "redirect:/contenedores";
    }
    
    // ============ CONSULTA POR ZONA ============
    
    @GetMapping("/contenedores/zona")
    public String consultarZona(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("email", session.getAttribute("email"));
        return "contenedores-zona";
    }
    
    @PostMapping("/contenedores/zona/buscar")
    public String buscarPorZona(
            @RequestParam int codigoPostal,
            @RequestParam String fecha,
            HttpSession session,
            Model model) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = sdf.parse(fecha);
            
            List<Contenedor> contenedores = serviceProxy.getContenedoresPorZona(token, fechaDate, codigoPostal);
            
            model.addAttribute("contenedores", contenedores);
            model.addAttribute("codigoPostal", codigoPostal);
            model.addAttribute("fecha", fecha);
            
            // Estadísticas de la zona
            if (!contenedores.isEmpty()) {
                int totalCapacidad = 0;
                int verdes = 0, amarillos = 0, rojos = 0, llenos = 0;
                
                for (Contenedor c : contenedores) {
                    totalCapacidad += c.getCapacidad();
                    switch (c.getNivelDeLlenado().toUpperCase()) {
                        case "VERDE" -> verdes++;
                        case "AMARILLO" -> amarillos++;
                        case "ROJO" -> rojos++;
                        case "LLENO" -> llenos++;
                    }
                }
                
                model.addAttribute("estadisticas", true);
                model.addAttribute("totalCapacidadZona", totalCapacidad);
                model.addAttribute("contenedoresVerdes", verdes);
                model.addAttribute("contenedoresAmarillos", amarillos);
                model.addAttribute("contenedoresRojos", rojos);
                model.addAttribute("contenedoresLlenos", llenos);
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al buscar contenedores: " + e.getMessage());
        }
        
        model.addAttribute("email", session.getAttribute("email"));
        return "contenedores-zona";
    }
    
    // ============ PLANTAS DE RECICLAJE ============
    
    @GetMapping("/plantas")
    public String listarPlantas(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
        model.addAttribute("plantas", plantas);
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("umbralSaturacion", umbralSaturacion);
        
        return "plantas";
    }
    
    @GetMapping("/plantas/capacidad")
    public String consultarCapacidad(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
        model.addAttribute("plantas", plantas);
        model.addAttribute("email", session.getAttribute("email"));
        
        return "plantas-capacidad";
    }
    
    @PostMapping("/plantas/capacidad/consultar")
    public String consultarCapacidadPost(
            @RequestParam long plantaId,
            @RequestParam(required = false) String fecha,
            HttpSession session,
            Model model) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            Date fechaDate = null;
            if (fecha != null && !fecha.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fechaDate = sdf.parse(fecha);
            }
            
            Integer capacidad = serviceProxy.getCapacidadPlanta(token, plantaId, fechaDate);
            
            List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
            PlantaReciclaje plantaSeleccionada = plantas.stream()
                .filter(p -> p.getId() == plantaId)
                .findFirst()
                .orElse(null);
            
            model.addAttribute("capacidad", capacidad);
            model.addAttribute("plantaSeleccionada", plantaSeleccionada);
            model.addAttribute("fecha", fecha);
            model.addAttribute("plantas", plantas);
            
            // Verificar alerta de saturación
            if (plantaSeleccionada != null && plantaSeleccionada.estaSaturada(umbralSaturacion)) {
                model.addAttribute("alertaSaturacion", true);
                model.addAttribute("porcentajeOcupacion", plantaSeleccionada.getPorcentajeOcupacion());
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al consultar capacidad: " + e.getMessage());
        }
        
        model.addAttribute("email", session.getAttribute("email"));
        return "plantas-capacidad";
    }
    
    // ============ ASIGNACIONES ============
    
    @GetMapping("/asignaciones")
    public String asignacionesForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        List<Contenedor> contenedores = serviceProxy.getAllContenedores(token);
        List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
        
        model.addAttribute("contenedores", contenedores);
        model.addAttribute("plantas", plantas);
        model.addAttribute("email", session.getAttribute("email"));
        
        return "asignaciones";
    }
    
    @PostMapping("/asignaciones/asignar")
    public String asignarContenedores(
            @RequestParam(value = "contenedorIds", required = false) List<Long> contenedorIds,
            @RequestParam long plantaId,
            @RequestParam(required = false, defaultValue = "false") boolean enviarNotificacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        String email = (String) session.getAttribute("email");
        
        if (contenedorIds == null || contenedorIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un contenedor");
            return "redirect:/asignaciones";
        }
        
        // Obtener datos antes de la asignación
        List<Contenedor> contenedoresSeleccionados = new ArrayList<>();
        for (Long id : contenedorIds) {
            Contenedor c = serviceProxy.getContenedorById(token, id);
            if (c != null) {
                contenedoresSeleccionados.add(c);
            }
        }
        
        PlantaReciclaje planta = serviceProxy.getPlantaById(token, plantaId);
        
        // Realizar la asignación
        boolean exito = serviceProxy.asignarContenedoresAPlanta(token, contenedorIds, plantaId);
        
        if (exito) {
            // Obtener planta actualizada
            PlantaReciclaje plantaActualizada = serviceProxy.getPlantaById(token, plantaId);
            
            // Crear resumen de asignación
            ResumenAsignacion resumen = new ResumenAsignacion(plantaActualizada, contenedoresSeleccionados);
            
            // Guardar resumen en sesión para mostrar en página de confirmación
            session.setAttribute("ultimaAsignacion", resumen);
            
            // Enviar notificación por email si se solicita
            if (enviarNotificacion && email != null) {
                emailService.enviarNotificacionAsignacion(
                    email,
                    plantaActualizada.getNombre(),
                    resumen.getTotalContenedores(),
                    resumen.getCapacidadTotalAsignada(),
                    resumen.getCapacidadRestantePlanta()
                );
            }
            
            // Verificar y enviar alerta de saturación
            if (resumen.isAlertaSaturacion()) {
                emailService.enviarAlertaSaturacion(
                    email,
                    plantaActualizada.getNombre(),
                    resumen.getPorcentajeOcupacionPlanta(),
                    resumen.getCapacidadRestantePlanta()
                );
                redirectAttributes.addFlashAttribute("alertaSaturacion", true);
            }
            
            return "redirect:/asignaciones/confirmacion";
            
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "Error al asignar contenedores. Verifique la capacidad disponible.");
            return "redirect:/asignaciones";
        }
    }
    
    @GetMapping("/asignaciones/confirmacion")
    public String confirmacionAsignacion(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        ResumenAsignacion resumen = (ResumenAsignacion) session.getAttribute("ultimaAsignacion");
        
        if (resumen == null) {
            return "redirect:/asignaciones";
        }
        
        model.addAttribute("resumen", resumen);
        model.addAttribute("email", session.getAttribute("email"));
        
        // Limpiar el resumen de la sesión
        session.removeAttribute("ultimaAsignacion");
        
        return "confirmacion-asignacion";
    }
}
