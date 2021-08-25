package bogdanov.warehouse.dto;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NomenclatureDTOTest {

    private NomenclatureDTO dto;

    @BeforeEach
    private void initializeVariables() {
        dto = new NomenclatureDTO();
    }

    @Test
    @Order(1)
    void test1_NoArgsConstructor() {
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getCode());
        assertNull(dto.getAmount());
    }

    @Test
    @Order(2)
    void test2_setName() {
        String name = "name";
        dto.setName(name);
        assertEquals(name.toUpperCase(Locale.ROOT), dto.getName());
        name = null;
        dto.setName(name);
        assertEquals(Strings.EMPTY, dto.getName());
        name = Strings.EMPTY;
        dto.setName(name);
        assertEquals(Strings.EMPTY, dto.getName());
        name = "\t";
        dto.setName(name);
        assertEquals(Strings.EMPTY, dto.getName());
    }

    @Test
    @Order(3)
    void test3_setCode() {
        String code = "code";
        dto.setCode(code);
        assertEquals(code.toUpperCase(Locale.ROOT), dto.getCode());
        code = null;
        dto.setCode(code);
        assertEquals(Strings.EMPTY, dto.getCode());
        code = Strings.EMPTY;
        dto.setCode(code);
        assertEquals(Strings.EMPTY, dto.getCode());
        code = "\t";
        dto.setCode(code);
        assertEquals(Strings.EMPTY, dto.getCode());
    }

    @Test
    @Order(4)
    void test4_AllArgsConstructor() {
        dto = new NomenclatureDTO(null, null, null, null);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNotNull(dto.getName());
        assertNotNull(dto.getCode());
        assertNull(dto.getAmount());
        Long id = 23L;
        String name = "name";
        String code = "code";
        Long amount = 44L;
        dto = new NomenclatureDTO(id, name, code, amount);
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(name.toUpperCase(Locale.ROOT), dto.getName());
        assertEquals(code.toUpperCase(Locale.ROOT), dto.getCode());
        assertEquals(amount, dto.getAmount());
    }

    @Test
    @Order(5)
    void test5_isEmpty_isNotEmpty() {
        assertTrue(dto.isEmpty());
        assertFalse(dto.isNotEmpty());
        dto.setId(2L);
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto.setId(null);
        assertTrue(dto.isEmpty());
        assertFalse(dto.isNotEmpty());
        dto.setAmount(23L);
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto.setAmount(null);
        assertTrue(dto.isEmpty());
        assertFalse(dto.isNotEmpty());
        dto.setName("name");
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto.setName(null);
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto = new NomenclatureDTO();
        assertTrue(dto.isEmpty());
        assertFalse(dto.isNotEmpty());
        dto.setCode("code");
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto.setCode(null);
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
        dto = new NomenclatureDTO(23L, "name", "code", 44L);
        assertFalse(dto.isEmpty());
        assertTrue(dto.isNotEmpty());
    }

    @Test
    @Order(6)
    void test6_dtoCloningConstructor() {
        NomenclatureDTO clone = new NomenclatureDTO(dto);
        assertEquals(dto, clone);
    }

}