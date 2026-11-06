package sw2.lab5.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "post")
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_Post;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Usuario user;
    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 75, message = "El titulo no puede pasar de 75 caracteres")
    private String title;
    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 5000, message = "El contenido no puede pasar de 5000 caracteres")
    private String content;
    @Size(max = 255, message = "El resumen no puede pasar de 255 caracteres")
    private String summary;
    private boolean published;

    public int getId_Post() {
        return id_Post;
    }
    public void setId_Post(int id_Post) {
        this.id_Post = id_Post;
    }
    public Usuario getUser() {
        return user;
    }
    public void setUser(Usuario user) {
        this.user = user;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public boolean isPublished() {
        return published;
    }
    public void setPublished(boolean published) {
        this.published = published;
    }
}
