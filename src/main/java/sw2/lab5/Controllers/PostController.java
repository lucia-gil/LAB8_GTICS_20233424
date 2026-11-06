package sw2.lab5.Controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;
import sw2.lab5.Repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostController {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    public PostController(PostRepository postRepository, UsuarioRepository usuarioRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority role : auth.getAuthorities()) {
            if (role.getAuthority().equals("admin")) {
                return true;
            }
        }
        return false;
    }

    private boolean puedeModificar(Post post, Authentication auth) {
        if (esAdmin(auth)) {
            return true;
        }
        return post.getUser().getEmail().equals(auth.getName());
    }

    private void cargarIdentidad(Model model, Authentication auth) {
        int idUsuario = 0;
        if (auth != null) {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName());
            idUsuario = usuario.getId_user();
        }
        model.addAttribute("esAdmin", esAdmin(auth));
        model.addAttribute("idUsuario", idUsuario);
    }

    @GetMapping("")
    public String lista(Model model, Authentication auth) {
        List<Post> lista;
        if (esAdmin(auth)) {
            lista = postRepository.findAll();
        } else {
            lista = postRepository.findByPublished(true);
        }
        model.addAttribute("lista", lista);
        model.addAttribute("tituloLista", "Posts");
        cargarIdentidad(model, auth);
        return "post/lista";
    }

    @GetMapping("/mine")
    public String misPosts(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName());
        model.addAttribute("lista", postRepository.findByUser(usuario));
        model.addAttribute("tituloLista", "Mis posts");
        cargarIdentidad(model, auth);
        return "post/lista";
    }

    @GetMapping("/create")
    public String nuevo(@ModelAttribute("post") Post post) {
        post.setPublished(true);
        return "post/form";
    }

    @GetMapping("/edit")
    public String editar(@ModelAttribute("post") Post post, @RequestParam("id") int id, Authentication auth) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post existente = optionalPost.get();
            if (!puedeModificar(existente, auth)) {
                return "redirect:/post?acceso=denegado";
            }
            post.setId_Post(existente.getId_Post());
            post.setTitle(existente.getTitle());
            post.setSummary(existente.getSummary());
            post.setContent(existente.getContent());
            post.setPublished(existente.isPublished());
            return "post/form";
        }
        return "redirect:/post";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute("post") @Valid Post post, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            return "post/form";
        }
        if (post.getId_Post() == 0) {
            Usuario autor = usuarioRepository.findByEmail(auth.getName());
            post.setUser(autor);
            postRepository.save(post);
        } else {
            Optional<Post> optionalPost = postRepository.findById(post.getId_Post());
            if (optionalPost.isPresent()) {
                Post existente = optionalPost.get();
                if (!puedeModificar(existente, auth)) {
                    return "redirect:/post?acceso=denegado";
                }
                existente.setTitle(post.getTitle());
                existente.setSummary(post.getSummary());
                existente.setContent(post.getContent());
                existente.setPublished(post.isPublished());
                postRepository.save(existente);
            }
        }
        if (esAdmin(auth)) {
            return "redirect:/post";
        }
        return "redirect:/post/mine";
    }

    @PostMapping("/delete")
    public String borrar(@RequestParam("id") int id, Authentication auth) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            if (!puedeModificar(optionalPost.get(), auth)) {
                return "redirect:/post?acceso=denegado";
            }
            postRepository.deleteById(id);
        }
        return "redirect:/post";
    }
}