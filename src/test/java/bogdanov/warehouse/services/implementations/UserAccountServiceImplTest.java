package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.junit.jupiter.api.Assertions.*;

//TODO Remove null id check before findById everywhere
//TODO catch standard repository exception instead

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class UserAccountServiceImplTest {

    @Autowired
    private UserAccountService service;

    @Test
    void getByIdWithNullId() {
        UserEntity user = service.getByIdWithoutNullCheck(null);
        assertNotNull(user);
    }

    @Test
    void getByIdWithNullId_Message() {
        InvalidDataAccessApiUsageException e = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> service.getByIdWithoutNullCheck(null));
        log.info("exception : " + e.getClass().getSimpleName());
        log.info("message : " + e.getMessage());
        log.info("cause : " + e.getCause().getClass().getSimpleName());
        log.info("cause message : " + e.getCause().getMessage());
    }
}