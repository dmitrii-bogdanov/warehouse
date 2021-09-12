package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
    private PositionEntity resultEntity;
    private List<PositionEntity> resultEntityList;

    @BeforeEach
    private void clear() {
        positionRepository.deleteAll();
    }

    @BeforeEach
    private void initializeVariables() {
        dtoList = new LinkedList<>();
        resultList = null;
        dto = new PositionDTO(null, "admin");
        result = null;
        resultEntity = null;
        resultEntityList = null;
    }

    @Test
    void addString_FirstEntry() {
        resultEntity = positionService.add(dto.getName());
        assertNotNull(resultEntity);
        assertNotNull(resultEntity.getId());
        assertTrue(resultEntity.getId() > 0);
        assertEquals(dto.getName().toUpperCase(Locale.ROOT), resultEntity.getName());
    }

    @Test
    void addString_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);// 0
        dtoList.add(new PositionDTO(null, "user"));// 1
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultEntityList = new LinkedList<>();
        resultEntityList.add(positionService.add(dtoList.get(0).getName()));
        resultEntityList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultEntityList.get(0));
        assertNotNull(resultEntityList.get(1));
        assertNotNull(resultEntityList.get(0).getId());
        assertNotNull(resultEntityList.get(1).getId());
        assertTrue(resultEntityList.get(0).getId() > 0);
        assertTrue(resultEntityList.get(1).getId() > 0);
        assertEquals(dtoList.get(0).getName().toUpperCase(Locale.ROOT), resultEntityList.get(0).getName().toUpperCase(Locale.ROOT));
        assertEquals(dtoList.get(1).getName().toUpperCase(Locale.ROOT), resultEntityList.get(1).getName().toUpperCase(Locale.ROOT));
        assertNotEquals(resultEntityList.get(0), resultEntityList.get(1));
    }

    @Test
    void addString_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, dto.getName()));
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultEntityList = new LinkedList<>();
        resultEntityList.add(positionService.add(dtoList.get(0).getName()));
        resultEntityList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultEntityList.get(0));
        assertNotNull(resultEntityList.get(1));
        assertEquals(resultEntityList.get(0), resultEntityList.get(1));
    }

    @Test
    void addString_BlankName() {
        dto.setName(null);
        ArgumentException e;
        e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        dto.setName(Strings.EMPTY);
        e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        dto.setName(" ");
        e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        dto.setName("\t");
        e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addDto_FirstEntry() {
        result = positionService.add(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName().toUpperCase(), result.getName());
    }


    @Test
    void addDto_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, "user"));
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
        assertEquals(dtoList.get(0).getName().toUpperCase(), resultList.get(0).getName());
        assertEquals(dtoList.get(1).getName().toUpperCase(), resultList.get(1).getName());
    }

    @Test
    void addDto_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);//0
        dtoList.add(new PositionDTO(null, dto.getName()));//1
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0)));
        resultList.add(positionService.add(dtoList.get(1)));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertEquals(resultList.get(0), resultList.get(1));
    }

    @Test
    void addDto_TabName() {
        dto.setName("\t");
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addListDto() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, "user"));
        dtoList.add(new PositionDTO(null, dtoList.get(1).getName()));
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
            assertEquals(dtoList.get(i).getName().toUpperCase(), resultList.get(i).getName());
        }
        assertNotEquals(resultList.get(0), resultList.get(1));
        assertEquals(resultList.get(1), resultList.get(2));
    }

    @Test
    void getAll() {
        dtoList.add(dto);//0
        dtoList.add(new PositionDTO(null, "staff"));//1
        dtoList.add(new PositionDTO(null, dtoList.get(1).getName()));//2
        dtoList.add(new PositionDTO(null, "user"));//3
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        assertEquals(dtoList.get(1).getName(), dtoList.get(2).getName());
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(3).getName());
        assertNotEquals(dtoList.get(1).getName(), dtoList.get(3).getName());
        log.info("dtoList");
        dtoList.forEach(d -> log.info(d.getId() + " " + d.getName()));
        dtoList = positionService.add(dtoList).stream().distinct().toList();
        log.info("dtoList");
        dtoList.forEach(d -> log.info(d.getId() + " " + d.getName()));
        resultList = positionService.getAll();
        log.info("resultList");
        resultList.forEach(d -> log.info(d.getId() + " " + d.getName()));
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