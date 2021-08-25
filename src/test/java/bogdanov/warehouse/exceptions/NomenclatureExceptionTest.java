package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.dto.NomenclatureDTO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NomenclatureExceptionTest {

    private NomenclatureException e, e2;
    private NomenclatureDTO dto1, dto2;
    private RuntimeException r1, r2;

    @BeforeEach
    private void resetVariables() {
        e = new NomenclatureException();
        e2 = new NomenclatureException();
        dto1 = new NomenclatureDTO(1L, "name-1", "code-1", 11L);
        dto2 = new NomenclatureDTO(2L, "name-2", "code-2", 22L);
        r1 = new RuntimeException("Runtime-1");
        r2 = new RuntimeException("Runtime-2");
    }

    @Test
    @Order(1)
    void test1_exceptionHasCorrectName() {
        assertEquals(
                e.getClass().getSimpleName(),
                e.getException(),
                "Variable \"exception\" has incorrect value"
        );
    }

    @Test
    @Order(2)
    void test2_initializationWithEmptyListAndMap() {
        assertTrue(e.getAccepted().isEmpty(), "Empty list is not created at initialization");
        assertTrue(e.getExceptions().isEmpty(), "Empty map is not created at initialization");
    }

    @Test
    @Order(3)
    void test3_addRuntimeExceptionFromDifferentDTO() {

        assertEquals(0, e.getExceptions().size());
        e.add(dto1, r1);
        assertEquals(1, e.getExceptions().size());
        e.add(dto2, r2);
        assertEquals(2, e.getExceptions().size());

        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage(),
                e.getExceptions().get(dto1.toString()),
                "Incorrect value in map"
        );
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toString()),
                "Incorrect value is put in map"
        );
    }

    @Test
    @Order(4)
    void test4_addRuntimeExceptionFromSameDTO() {

        assertEquals(0, e.getExceptions().size());
        e.add(dto1, r1);
        assertEquals(1, e.getExceptions().size());
        e.add(dto1, r2);
        assertEquals(1, e.getExceptions().size());
        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage()
                        + "\n" + r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto1.toString()),
                "Incorrect concatenation in case of putting value with existing key"
        );
    }

    @Test
    @Order(5)
    void test5_isEmpty_isNotEmpty() {
        assertTrue(e.isEmpty());
        assertFalse(e.isNotEmpty());
        e.add(dto1, r1);
        assertFalse(e.isEmpty());
        assertTrue(e.isNotEmpty());
    }

    @Test
    @Order(6)
    void test6_size() {
        assertEquals(0, e.size());
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e.add(dto2, r2);
        assertEquals(2, e.size());
        e.add(dto1, r2);
        assertEquals(2, e.size());
    }

    @Test
    @Order(7)
    void test7_countAccepted() {

        assertEquals(e.getAccepted().size(), e.countAccepted());
        e.getAccepted().add(dto1);
        assertEquals(e.getAccepted().size(), e.countAccepted());
        e.getAccepted().add(dto2);
        assertEquals(e.getAccepted().size(), e.countAccepted());
    }

    @Test
    @Order(8)
    void test8_accept() {
        assertEquals(0, e.countAccepted());
        e.accept(dto1);
        assertEquals(1, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto1));
        e.accept(dto2);
        assertEquals(2, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto2));
    }

    @Test
    @Order(9)
    void test9_acceptAll() {
        Collection<NomenclatureDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        assertEquals(0, e.countAccepted());
        e.acceptAll(list);
        assertEquals(2, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto2));
        assertTrue(e.getAccepted().contains(dto1));
    }

    @Test
    @Order(10)
    void test10_addNomenclatureExceptionWithDifferentDTO() {
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e2.add(dto2, r2);
        assertEquals(1, e.size());
        e.add(e2);
        assertEquals(2, e.size());
        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage(),
                e.getExceptions().get(dto1.toString())
        );
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toString())
        );
    }

    @Test
    @Order(11)
    void test11_addNomenclatureExceptionWithSameDTO() {
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e2.add(dto1, r2);
        assertEquals(1, e.size());
        e.add(e2);
        assertEquals(1, e.size());
        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage()
                        + "\n" + r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto1.toString())
        );
    }

    @Test
    @Order(12)
    void test12_addNomenclatureExceptionWithCrossedAcceptedAndExceptions() {
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e.accept(dto2);
        assertEquals(1, e.countAccepted());
        e2.add(dto2, r2);
        assertEquals(1, e.size());
        assertEquals(1, e2.size());
        e2.accept(dto1);
        assertEquals(1, e.countAccepted());
        assertEquals(1, e2.countAccepted());
        e.add(e2);
        assertEquals(2, e.size());
        assertEquals(0, e.countAccepted());
        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage(),
                e.getExceptions().get(dto1.toString())
        );
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toString())
        );
    }

    @Test
    @Order(13)
    void test13_addNomenclatureExceptionSomething1() {
        e.accept(dto1);
        e.accept(dto2);
        assertEquals(2, e.countAccepted());
        e2.add(dto2, r2);
        assertEquals(0, e.size());
        assertEquals(1, e2.size());
        e2.accept(dto1);
        assertEquals(2, e.countAccepted());
        assertEquals(1, e2.countAccepted());
        e.add(e2);
        assertEquals(1, e.size());
        assertEquals(1, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto1));
        assertFalse(e.getAccepted().contains(dto2));
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toString())
        );
    }

    @Test
    @Order(14)
    void test14_addNomenclatureExceptionSomething2() {
        e.add(dto1, r1);
        assertEquals(1, e.size());
        e.accept(dto2);
        assertEquals(1, e.countAccepted());
        assertEquals(1, e.size());
        assertEquals(0, e2.size());
        e2.accept(dto2);
        e2.accept(dto1);
        assertEquals(1, e.countAccepted());
        assertEquals(2, e2.countAccepted());
        e.add(e2);
        assertEquals(1, e.size());
        assertEquals(1, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto2));
        assertFalse(e.getAccepted().contains(dto1));
        assertEquals(
                r1.getClass().getSimpleName() + " : " + r1.getMessage(),
                e.getExceptions().get(dto1.toString())
        );
    }

    @Test
    @Order(15)
    void test15_addNomenclatureExceptionSomething3() {
        e.accept(dto1);
        e.accept(dto2);
        assertEquals(2, e.countAccepted());
        e2.add(dto2, r2);
        assertEquals(0, e.size());
        assertEquals(1, e2.size());
        e.add(e2);
        assertEquals(1, e.size());
        assertEquals(1, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto1));
        assertFalse(e.getAccepted().contains(dto2));
        assertEquals(
                r2.getClass().getSimpleName() + " : " + r2.getMessage(),
                e.getExceptions().get(dto2.toString())
        );
    }

    @Test
    @Order(16)
    void test16_addNomenclatureExceptionSomething4() {
        e.accept(dto1);
        NomenclatureDTO dto3 = new NomenclatureDTO(3L, "name-3", "code-3", 33L);
        RuntimeException r3 = new RuntimeException("Runtime-3");
        e.add(dto3, r3);
        assertEquals(1, e.countAccepted());
        assertEquals(1, e.size());
        e2.accept(dto2);
        assertEquals(1, e.countAccepted());
        assertEquals(1, e2.countAccepted());
        e.add(e2);
        assertEquals(1, e.size());
        assertEquals(2, e.countAccepted());
        assertTrue(e.getAccepted().contains(dto1));
        assertTrue(e.getAccepted().contains(dto2));
        assertFalse(e.getAccepted().contains(dto3));
        assertEquals(
                r3.getClass().getSimpleName() + " : " + r3.getMessage(),
                e.getExceptions().get(dto3.toString())
        );
    }

}