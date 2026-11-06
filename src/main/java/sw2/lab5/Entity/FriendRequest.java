package sw2.lab5.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_request")
public class FriendRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private int idRequest;
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Usuario sender;
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Usuario receiver;
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    public int getIdRequest() {
        return idRequest;
    }
    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }
    public Usuario getSender() {
        return sender;
    }
    public void setSender(Usuario sender) {
        this.sender = sender;
    }
    public Usuario getReceiver() {
        return receiver;
    }
    public void setReceiver(Usuario receiver) {
        this.receiver = receiver;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
