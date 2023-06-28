package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.TestRedisConfiguration;
import com.hw.cloudstorage.model.entity.BlockedToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BlacklistTokenRepositoryTest {

    @Autowired
    private BlacklistTokenRepository repository;

    @Test
    public void saveBlockedToken_shouldReturnBlockedTokenObject() {
        String token = "s.t.o.k.e.n.";
        BlockedToken blockedToken = new BlockedToken(token, 30L);
        BlockedToken savedToken = repository.save(blockedToken);

        assertEquals(blockedToken, savedToken);
    }

    @Test
    public void findByIdBlockedToken_shouldReturnBlockedTokenObject() {
        String token = "f.t.o.k.e.n.";
        repository.save(new BlockedToken(token, 10L));
        Optional<BlockedToken> blockedToken = repository.findById(token);

        assertTrue(blockedToken.get().getToken().contains(token));
    }

}