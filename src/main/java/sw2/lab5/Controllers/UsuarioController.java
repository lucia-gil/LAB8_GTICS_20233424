package sw2.lab5.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sw2.lab5.Entity.Rol;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.FriendRequestRepository;
import sw2.lab5.Repository.PostRepository;
import sw2.lab5.Repository.RolRepository;
import sw2.lab5.Repository.UsuarioRepository;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PostRepository postRepository;
    private final FriendRequestRepository friendRequestRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                             PostRepository postRepository, FriendRequestRepository friendRequestRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.postRepository = postRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @GetMapping("")
    public String perfil(Model model, Authentication auth) {
        model.addAttribute("perfil", usuarioRepository.findByEmail(auth.getName()));
        return "user/perfil";
    }

    @GetMapping("/edit")
    public String editarPerfil(Model model, @ModelAttribute("usuario") Usuario usuario, Authentication auth) {
        Usuario actual = usuarioRepository.findByEmail(auth.getName());
        usuario.setFirstname(actual.getFirstname());
        usuario.setLastname(actual.getLastname());
        usuario.setIntro(actual.getIntro());
        usuario.setProfile(actual.getProfile());
        model.addAttribute("modoAdmin", false);
        return "user/form";
    }

    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                                Model model, Authentication auth, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoAdmin", false);
            return "user/form";
        }
        Usuario actual = usuarioRepository.findByEmail(auth.getName());
        actual.setFirstname(usuario.getFirstname());
        actual.setLastname(usuario.getLastname());
        actual.setIntro(usuario.getIntro());
        actual.setProfile(usuario.getProfile());
        usuarioRepository.save(actual);
        actual.setPassword(null);
        session.setAttribute("usuario", actual);
        return "redirect:/user";
    }

    @GetMapping("/list")
    public String lista(Model model) {
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        return "user/list";
    }

    @GetMapping("/editar")
    public String editarUsuario(Model model, @ModelAttribute("usuario") Usuario usuario, @RequestParam("id") int id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario existente = optionalUsuario.get();
            usuario.setId_user(existente.getId_user());
            usuario.setFirstname(existente.getFirstname());
            usuario.setLastname(existente.getLastname());
            usuario.setIntro(existente.getIntro());
            usuario.setProfile(existente.getProfile());
            usuario.setActive(existente.isActive());
            usuario.setRol(existente.getRol());
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("modoAdmin", true);
            return "user/form";
        }
        return "redirect:/user/list";
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                                    Model model, Authentication auth) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("modoAdmin", true);
            return "user/form";
        }
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getId_user());
        if (optionalUsuario.isPresent()) {
            Usuario existente = optionalUsuario.get();
            existente.setFirstname(usuario.getFirstname());
            existente.setLastname(usuario.getLastname());
            existente.setIntro(usuario.getIntro());
            existente.setProfile(usuario.getProfile());
            boolean esElMismo = existente.getEmail().equals(auth.getName());
            if (!esElMismo) {
                existente.setActive(usuario.isActive());
                if (usuario.getRol() != null) {
                    Optional<Rol> optionalRol = rolRepository.findById(usuario.getRol().getId_role());
                    if (optionalRol.isPresent()) {
                        existente.setRol(optionalRol.get());
                    }
                }
            }
            usuarioRepository.save(existente);
        }
        return "redirect:/user/list";
    }

    @PostMapping("/borrar")
    public String borrar(@RequestParam("id") int id, Model model, Authentication auth) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario existente = optionalUsuario.get();
            String error = null;
            if (existente.getEmail().equals(auth.getName())) {
                error = "No puedes eliminar tu propia cuenta";
            } else if (!postRepository.findByUser(existente).isEmpty()) {
                error = "No se puede eliminar: el usuario tiene posts";
            } else if (!friendRequestRepository.findBySender(existente).isEmpty()
                    || !friendRequestRepository.findByReceiver(existente).isEmpty()) {
                error = "No se puede eliminar: el usuario tiene solicitudes de amistad";
            }
            if (error != null) {
                model.addAttribute("error", error);
                model.addAttribute("listausuarios", usuarioRepository.findAll());
                return "user/list";
            }
            usuarioRepository.deleteById(id);
        }
        return "redirect:/user/list?eliminado";
    }
}
