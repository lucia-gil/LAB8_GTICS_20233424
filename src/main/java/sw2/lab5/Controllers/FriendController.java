package sw2.lab5.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sw2.lab5.Entity.FriendRequest;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.FriendRequestRepository;
import sw2.lab5.Repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/friend")
public class FriendController {

    private final UsuarioRepository usuarioRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendController(UsuarioRepository usuarioRepository, FriendRequestRepository friendRequestRepository) {
        this.usuarioRepository = usuarioRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    private void cargarPagina(Model model, Authentication auth) {
        Usuario yo = usuarioRepository.findByEmail(auth.getName());
        List<FriendRequest> enviadas = friendRequestRepository.findBySender(yo);
        List<Usuario> candidatos = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll()) {
            boolean yaEnviada = false;
            for (FriendRequest f : enviadas) {
                if (f.getReceiver().getId_user() == u.getId_user()) {
                    yaEnviada = true;
                }
            }
            if (u.getId_user() != yo.getId_user() && u.isActive() && !yaEnviada) {
                candidatos.add(u);
            }
        }
        model.addAttribute("enviadas", enviadas);
        model.addAttribute("candidatos", candidatos);
    }
    @GetMapping("")
    public String pagina(Model model, Authentication auth) {
        cargarPagina(model, auth);
        return "friend/lista";
    }
    @PostMapping("/send")
    public String enviar(@RequestParam("receiverId") int receiverId, Model model, Authentication auth) {
        Usuario yo = usuarioRepository.findByEmail(auth.getName());
        Optional<Usuario> optionalReceptor = usuarioRepository.findById(receiverId);
        String error = null;
        Usuario receptor = null;
        if (!optionalReceptor.isPresent()) {
            error = "El usuario no existe";
        } else {
            receptor = optionalReceptor.get();
            if (receptor.getId_user() == yo.getId_user()) {
                error = "No puedes enviarte una solicitud a ti mismo";
            } else if (!receptor.isActive()) {
                error = "El usuario no esta activo";
            } else if (friendRequestRepository.findBySenderAndReceiver(yo, receptor) != null) {
                error = "Ya enviaste una solicitud a este usuario";
            } else if (friendRequestRepository.findBySenderAndReceiver(receptor, yo) != null) {
                error = "Este usuario ya te envio una solicitud";
            }
        }
        if (error != null) {
            model.addAttribute("error", error);
            cargarPagina(model, auth);
            return "friend/lista";
        }
        FriendRequest solicitud = new FriendRequest();
        solicitud.setSender(yo);
        solicitud.setReceiver(receptor);
        solicitud.setSentAt(LocalDateTime.now());
        friendRequestRepository.save(solicitud);
        return "redirect:/friend?enviada";
    }
}