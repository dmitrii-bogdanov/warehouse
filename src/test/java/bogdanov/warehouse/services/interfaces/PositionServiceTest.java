package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class PositionServiceTest {

    @Autowired
    private PositionService positionService;
    @Autowired
    private PositionRepository positionRepository;

    private List<PositionDTO> dtoList;
    private List<PositionDTO> resultList;
    private PositionDTO dto;
    private PositionDTO result;

    @BeforeEach
    private void clear() {
        positionService.getAll().stream()
                .forEach(p -> positionService.delete(p.getName()));
    }

    @BeforeEach
    private void initializeVariables() {
        dtoList = new LinkedList<>();
        resultList = null;
        dto = new PositionDTO("admin");
        result = null;
    }

    @Test
    void addString_FirstEntry() {
        result = positionService.add(dto.getName());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void addString_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO("user"));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0).getName()));
        resultList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertNotNull(resultList.get(0).getId());
        assertNotNull(resultList.get(1).getId());
        assertTrue(resultList.get(0).getId() > 0);
        assertTrue(resultList.get(1).getId() > 0);
        assertEquals(dtoList.get(0).getName(), resultList.get(0).getName());
        assertEquals(dtoList.get(1).getName(), resultList.get(1).getName());
        assertNotEquals(resultList.get(0), resultList.get(1));
    }

    @Test
    void addString_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(dto.getName()));
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0).getName()));
        resultList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertEquals(resultList.get(0), resultList.get(1));
    }

    @Test
    void addString_BlankName() {
        dto.setName(null);
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto.getName()));
        dto.setName(Strings.EMPTY);
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto.getName()));
        dto.setName(" ");
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto.getName()));
        dto.setName("\t");
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto.getName()));
    }

    @Test
    void addDto_FirstEntry() {
        result = positionService.add(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
    }


    @Test
    void addDto_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO("user"));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0)));
        resultList.add(positionService.add(dtoList.get(1)));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertNotNull(resultList.get(0).getId());
        assertNotNull(resultList.get(1).getId());
        assertTrue(resultList.get(0).getId() > 0);
        assertTrue(resultList.get(1).getId() > 0);
        assertNotEquals(resultList.get(0), resultList.get(1));
        assertEquals(dtoList.get(0).getName(), resultList.get(0).getName());
        assertEquals(dtoList.get(1).getName(), resultList.get(1).getName());
    }

    @Test
    void addDto_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(dto.getName()));
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0)));
        resultList.add(positionService.add(dtoList.get(1)));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertEquals(resultList.get(0), resultList.get(1));
    }

    @Test
    void addDto_BlankName() {
        dto.setName(null);
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto));
        dto.setName(Strings.EMPTY);
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto));
        dto.setName(" ");
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto));
        dto.setName("\t");
        assertThrows(IllegalArgumentException.class,
                () -> positionService.add(dto));
    }

    @Test
    void addListDto() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO("user"));
        dtoList.add(new PositionDTO(dtoList.get(1).getName()));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        assertEquals(dtoList.get(1).getName(), dtoList.get(2).getName());
        resultList = positionService.add(dtoList);
        assertNotNull(resultList);
        assertEquals(dtoList.size(), resultList.size());
        for (PositionDTO r : resultList) {
            assertNotNull(r);
            assertNotNull(r.getId());
            assertTrue(r.getId() > 0);
        }
        for (int i = 0; i < dtoList.size(); i++) {
            assertEquals(dtoList.get(i).getName(), resultList.get(i).getName());
        }
        assertNotEquals(resultList.get(0), resultList.get(1));
        assertEquals(resultList.get(1), resultList.get(2));
    }

    @Test
    void getAll() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO("staff"));
        dtoList.add(new PositionDTO(dtoList.get(1).getName()));
        dtoList.add(new PositionDTO("user"));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        assertEquals(dtoList.get(1).getName(), dtoList.get(2).getName());
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(3).getName());
        assertNotEquals(dtoList.get(1).getName(), dtoList.get(3).getName());
        dtoList = positionService.add(dtoList).stream().distinct().toList();
        resultList = positionService.getAll();
        assertNotNull(resultList);
        assertEquals(dtoList.size(), resultList.size());
        for (PositionDTO r : resultList) {
            assertNotNull(r);
            assertNotNull(r.getId());
            assertTrue(r.getId() > 0);
        }
        assertTrue(resultList.containsAll(dtoList));
        assertEquals(resultList.size(), resultList.stream().distinct().toList().size());
    }

}