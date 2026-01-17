package DS_06.Ecoembes.client.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import DS_06.Ecoembes.client.data.Contenedor;
import DS_06.Ecoembes.client.data.PlantaReciclaje;
import DS_06.Ecoembes.client.proxies.IEcoembesServiceProxy;
import jakarta.servlet.http.HttpSession;

@Controller
public class EcoembesWebController {
    
    private final IEcoembesServiceProxy serviceProxy;
    
    public EcoembesWebController(IEcoembesServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }
    
    // ============ AUTENTICACIÓN ============
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        // Si ya está logueado, redirigir al dashboard
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
    public String logout(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token != null) {
            serviceProxy.logout(token);
        }
        session.invalidate();
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
        
        // Obtener estadísticas básicas
        List<Contenedor> contenedores = serviceProxy.getAllContenedores(token);
        List<PlantaReciclaje> plantas = serviceProxy.getAllPlantas(token);
        
        model.addAttribute("totalContenedores", contenedores.size());
        model.addAttribute("totalPlantas", plantas.size());
        
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
            HttpSession session,
            Model model) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        Contenedor nuevo = serviceProxy.crearContenedor(token, codigoPostal, capacidad);
        
        if (nuevo != null) {
            model.addAttribute("success", "Contenedor creado exitosamente con ID: " + nuevo.getId());
        } else {
            model.addAttribute("error", "Error al crear el contenedor");
        }
        
        return "redirect:/contenedores";
    }
    
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
            HttpSession session,
            Model model) {
        
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        if (contenedorIds == null || contenedorIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un contenedor");
            return "redirect:/asignaciones";
        }
        
        boolean exito = serviceProxy.asignarContenedoresAPlanta(token, contenedorIds, plantaId);
        
        if (exito) {
            model.addAttribute("success", "Contenedores asignados exitosamente");
        } else {
            model.addAttribute("error", "Error al asignar contenedores. Verifique la capacidad disponible.");
        }
        
        return "redirect:/asignaciones";
    }
}
