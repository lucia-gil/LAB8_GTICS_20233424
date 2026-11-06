package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.FriendRequest;
import sw2.lab5.Entity.Usuario;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    FriendRequest findBySenderAndReceiver(Usuario sender, Usuario receiver);
    List<FriendRequest> findBySender(Usuario sender);
    List<FriendRequest> findByReceiver(Usuario receiver);
}
