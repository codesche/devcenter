package org.com.invitechat.repository;

import java.util.Optional;
import org.com.invitechat.entity.InviteToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteTokenRepository extends MongoRepository<InviteToken, String> {
    Optional<InviteToken> findByToken(String token);
    boolean existsByToken(String token);
}
