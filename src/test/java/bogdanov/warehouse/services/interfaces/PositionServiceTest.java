package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.repositories.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class PositionServiceTest {

    @Autowired
    private PositionService positionService;




}