package sw2.lab5.Config;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.UsuarioRepository;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    public WebSecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processLogin")
                .successHandler((request, response, authentication) -> {
                    DefaultSavedRequest defaultSavedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                    Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
                    usuario.setPassword(null);
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", usuario);
                    if (defaultSavedRequest != null) {
                        String targetURL = defaultSavedRequest.getRedirectUrl();
                        session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                        redirectStrategy.sendRedirect(request, response, targetURL);
                    } else {
                        String rol = "";
                        for (GrantedAuthority role : authentication.getAuthorities()) {
                            rol = role.getAuthority();
                            break;
                        }
                        if (rol.equals("admin")) {
                            redirectStrategy.sendRedirect(request, response, "/user/list");
                        } else {
                            redirectStrategy.sendRedirect(request, response, "/post");
                        }
                    }
                });

        http.logout()
                .logoutSuccessUrl("/post")
                .deleteCookies("JSESSIONID", "SESSION")
                .invalidateHttpSession(true);

        http.authorizeHttpRequests()
                .requestMatchers("/user/list", "/user/editar", "/user/actualizar", "/user/borrar").hasAuthority("admin")
                .requestMatchers("/user", "/user/edit", "/user/guardar").hasAnyAuthority("admin", "user")
                .requestMatchers("/post/create", "/post/edit", "/post/save", "/post/delete", "/post/mine").hasAnyAuthority("admin", "user")
                .requestMatchers("/friend", "/friend/**").hasAnyAuthority("admin", "user")
                .requestMatchers("/", "/post", "/loginForm", "/error", "/css/**", "/js/**", "/webfonts/**").permitAll()
                .anyRequest().denyAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sql1 = "SELECT email, password, active FROM user WHERE email = ?";
        String sql2 = "SELECT u.email, r.role_name FROM user u "
                + "INNER JOIN role r ON (u.id_role = r.id_role) "
                + "WHERE u.email = ? AND u.active = 1";
        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }
}