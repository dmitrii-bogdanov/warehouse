package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.dto.NomenclatureDTO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class NomenclatureExceptionTest {

    private NomenclatureException e;
    private NomenclatureDTO dto1, dto2;
    private RuntimeException r1, r2;

    @BeforeEach
    private void resetVariables() {
        e = new NomenclatureException();
        dto1 = new NomenclatureDTO(1L, "name-1", "code-1", 11L);
        dto2 = new NomenclatureDTO(2L, "name-2", "code-2", 22L);
        r1 = new RuntimeException("Runtime-1");
        r2 = new RuntimeException("Runtime-2");
    }

    @Test
    void test1_ExceptionHasCorrectName() {
        assertEquals(
                e.getClass().getSimpleName(),
                e.getException(),
                "Variable \"exception\" has incorrect value"
        );
    }

    @Test
    void test2_InitializationWithEmptyListAndMap() {
        assertTrue(e.getAccepted().isEmpty(), "Empty list is not created at initialization");
        assertTrue(e.getExceptions().isEmpty(), "Empty map is not created at initialization");
    }

    @Test
    void test3_AddingRuntimeExceptionFromDifferentDTO() {
        e.add(dto1, r1);
        e.add(dto2, r2);

        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage(),
                e.getExceptions().get(dto1.toFormattedString()),
                "Incorrect value in map"
        );
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toFormattedString()),
                "Incorrect value is put in map"
        );
    }

    @Test
    void test4_AddingRuntimeExceptionFromSameDTO() {
        e.add(dto1, r1);
        e.add(dto1, r2);

        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage()
                        + "\n" + r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                    e.getExceptions().get(dto1.toFormattedString()),
                "Incorrect concatenation in case of putting value with existing key"
                );
    }

    @Test
    void test5_isEmpty_isNotEmpty() {
        assertTrue(e.isEmpty());
        assertFalse(e.isNotEmpty());
        e.add(dto1, r1);
        assertFalse(e.isEmpty());
        assertTrue(e.isNotEmpty());
    }

    @Test
    void test6_size() {
        assertEquals(0, e.size());
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e.add(dto2,r2);
        assertEquals(2, e.size());
        e.add(dto1, r2);
        assertEquals(2, e.size());
    }

    @Test
    void test7_accept() {
        assertEquals(0, e.countAccepted());
        e.accept(dto1);
        assertEquals(1, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto1));
        e.accept(dto2);
        assertEquals(2, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto2));
    }

    @Test
    void test8_acceptAll() {
        Collection<NomenclatureDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        assertEquals(0, e.countAccepted());
        e.acceptAll(list);
        assertEquals(2, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto2));
        assertTrue(e.getAccepted().contains(dto1));
    }

}