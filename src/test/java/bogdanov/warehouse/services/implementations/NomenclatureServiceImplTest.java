package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
class NomenclatureServiceImplTest {

    @Autowired
    private NomenclatureService nomenclatureService;

    @Autowired
    private NomenclatureRepository nomenclatureRepository;

    @Autowired
    private Mapper mapper;

    //region NomenclatureDTO constants
    private final NomenclatureDTO NAME1_CODE1 =
            new NomenclatureDTO(null, "NAME1", "CODE1", null);

    private final NomenclatureDTO NAME1_NULL_CODE =
            new NomenclatureDTO(null, "NAME1", null, null);

    private final NomenclatureDTO NAME1_EMPTY_CODE =
            new NomenclatureDTO(null, "NAME1", Strings.EMPTY, null);

    private final NomenclatureDTO NAME1_BLANK_CODE =
            new NomenclatureDTO(null, "NAME1", "\t", null);

    private final NomenclatureDTO NAME2_CODE1 =
            new NomenclatureDTO(null, "NAME2", "CODE1", null);

    private final NomenclatureDTO NAME2_CODE2 =
            new NomenclatureDTO(null, "NAME2", "CODE2", null);

    private final NomenclatureDTO NAME2_NULL_CODE =
            new NomenclatureDTO(null, "NAME2", null, null);

    private final NomenclatureDTO NAME3_CODE3 =
            new NomenclatureDTO(null, "NAME3", "CODE3", null);

    private final NomenclatureDTO NAME3_NULL_CODE =
            new NomenclatureDTO(null, "NAME3", null, null);

    private final NomenclatureDTO NAME3_CODE1 =
            new NomenclatureDTO(null, "NAME3", "CODE1", null);

    private final NomenclatureDTO NAME3_CODE2 =
            new NomenclatureDTO(null, "NAME3", "CODE2", null);
    //endregion

    private NomenclatureException e;
    private NomenclatureEntity entity;
    private NomenclatureDTO dto, result;

    @BeforeEach
    private void clear() {
        nomenclatureRepository.deleteAll();
    }

    @BeforeEach
    private void initializeVariables() {
        e = new NomenclatureException();
        entity = new NomenclatureEntity();
        dto = new NomenclatureDTO();
        result = null;
    }

    //region checkId()
    @Test
    @Order(1)
    void test1_checkIdDto_dtoWithNullId() {
        dto.setId(null);
        assertThrows(NullIdException.class,
                () -> {
                    nomenclatureService.checkId(dto);
                },
                "CheckId(dto) should have thrown NullIdException in case of null id");
    }

    @Test
    @Order(2)
    void test2_checkIdDto_dtoWithNotExistingId() {
        dto.setId(1L);
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    nomenclatureService.checkId(dto);
                },
                "Should have thrown ResourceNotFoundException"
        );
        assertEquals("Nomenclature with id : " + dto.getId() + " not found", ex.getMessage());
    }

    @Test
    @Order(3)
    void test3_checkIdDto_dtoWithExistingId() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertTrue(nomenclatureService.checkId(dto));
    }

    @Test
    @Order(4)
    void test4_checkIdDtoException_dtoWithNullId() {
        dto.setId(null);
        assertFalse(nomenclatureService.checkId(dto, e));
        assertTrue(e.getExceptions().get(dto.toString()).contains(NullIdException.class.getSimpleName()));
    }

    @Test
    @Order(5)
    void test5_checkIdDtoException_dtoWithNotExistingId() {
        dto.setId(1L);
        assertFalse(nomenclatureService.checkId(dto, e));
        assertTrue(e.getExceptions().get(dto.toString()).contains(ResourceNotFoundException.class.getSimpleName()));
    }

    @Test
    @Order(6)
    void test6_checkIdDtoException_dtoWithExistingId() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertTrue(nomenclatureService.checkId(dto, e));
        assertTrue(e.isEmpty());
    }
    //endregion

    //region checkIdAndRetrieve
    @Test
    @Order(7)
    void test7_checkIdAndRetrieveDto_dtoWithNullId() {
        dto.setId(null);
        assertThrows(
                NullIdException.class,
                () -> {
                    nomenclatureService.checkIdAndRetrieve(dto);
                },
                "Should have thrown NullIdException"
        );
    }

    @Test
    @Order(8)
    void test8_checkIdAndRetrieveDto_dtoWithNotExistingId() {
        dto.setId(1L);
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    nomenclatureService.checkIdAndRetrieve(dto);
                },
                "Should have thrown ResourceNotFoundException"
        );
        assertEquals("Nomenclature with id : " + dto.getId() + " not found", ex.getMessage());
    }

    @Test
    @Order(9)
    void test9_checkIdAndRetrieveDto_dtoWithExistingId() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertEquals(dto, mapper.convert(nomenclatureService.checkIdAndRetrieve(dto)));
    }

    @Test
    @Order(10)
    void test10_checkIdAndRetrieveDtoException_dtoWithNullId() {
        dto.setId(null);
        nomenclatureService.checkIdAndRetrieve(dto, e);
        assertEquals(1, e.size());
        assertTrue(e.getExceptions().get(dto.toString()).contains(NullIdException.class.getSimpleName()));
    }

    @Test
    @Order(11)
    void test11_checkIdAndRetrieveDtoException_dtoWithNotExistingId() {
        dto.setId(1L);
        nomenclatureService.checkIdAndRetrieve(dto, e);
        assertTrue(e.getExceptions().get(dto.toString()).contains(ResourceNotFoundException.class.getSimpleName()));
    }

    @Test
    @Order(12)
    void test12_checkIdAndRetrieveDtoException_dtoWithExistingId() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(name);
        dto = mapper.convert(nomenclatureRepository.save(entity));

        assertEquals(dto, mapper.convert(nomenclatureService.checkIdAndRetrieve(dto, e)));
        assertEquals(0, e.size());
    }
    //endregion

    //region checkNameAvailability()
    @Test
    @Order(13)
    void test13_checkNameAvailabilityDto_dtoWithNullName() {
        dto.setName(null);
        assertThrows(NomenclatureBlankNameException.class,
                () -> {
                    nomenclatureService.checkNameAvailability(dto);
                },
                "Should have thrown NomenclatureBlankNameException"
        );
    }

    @Test
    @Order(14)
    void test14_checkNameAvailabilityDto_dtoWithEmptyName() {
        dto.setName(Strings.EMPTY);
        assertThrows(NomenclatureBlankNameException.class,
                () -> {
                    nomenclatureService.checkNameAvailability(dto);
                },
                "Should have thrown NomenclatureBlankNameException"
        );
    }

    @Test
    @Order(15)
    void test15_checkNameAvailabilityDto_dtoWithBlankName() {
        dto.setName("\t");
        assertThrows(NomenclatureBlankNameException.class,
                () -> {
                    nomenclatureService.checkNameAvailability(dto);
                },
                "Should have thrown NomenclatureBlankNameException"
        );
    }

    @Test
    @Order(16)
    void test16_checkNameAvailabilityDto_dtoWithAvailableName() {
        dto.setName("name");
        assertTrue(nomenclatureService.checkNameAvailability(dto));
    }

    @Test
    @Order(17)
    void test17_checkNameAvailabilityDto_dtoWithAlreadyTakenName() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = Strings.EMPTY;
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        NomenclatureAlreadyTakenNameException ex = assertThrows(NomenclatureAlreadyTakenNameException.class,
                () -> {
                    nomenclatureService.checkNameAvailability(dto);
                },
                "Should have thrown NomenclatureAlreadyTakenNameException"
        );
        assertEquals("Name : " + dto.getName() + " belongs to id : " + dto.getId(), ex.getMessage());
    }

    @Test
    @Order(18)
    void test18_checkNameAvailabilityDtoException_dtoWithNullName() {
        dto.setName(null);
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(19)
    void test19_checkNameAvailabilityDtoException_dtoWithEmptyName() {
        dto.setName(Strings.EMPTY);
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(20)
    void test20_checkNameAvailabilityDtoException_dtoWithBlankName() {
        dto.setName("\t");
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(21)
    void test21_checkNameAvailabilityDtoException_dtoWithAvailableName() {
        dto.setName("name");
        assertTrue(nomenclatureService.checkNameAvailability(dto, e));
        assertEquals(0, e.size());
    }

    @Test
    @Order(22)
    void test22_checkNameAvailabilityDtoException_dtoWithAlreadyTakenName() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = Strings.EMPTY;
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
    }
    //endregion

    //region checkCodeAvailability()
    @Test
    @Order(23)
    void test23_checkCodeAvailabilityDto_dtoWithNullCode() {
        dto.setCode(null);
        assertTrue(nomenclatureService.checkCodeAvailability(dto), "Should have accepted null value");
    }

    @Test
    @Order(24)
    void test24_checkCodeAvailabilityDto_dtoWithEmptyCode() {
        dto.setCode(Strings.EMPTY);
        assertTrue(nomenclatureService.checkCodeAvailability(dto), "Should have accepted empty string");
    }

    @Test
    @Order(25)
    void test25_checkCodeAvailabilityDto_dtoWithNullCode() {
        dto.setCode("\t");
        assertTrue(nomenclatureService.checkCodeAvailability(dto), "Should have accepted blank string");
    }

    @Test
    @Order(26)
    void test26_checkCodeAvailabilityDto_dtoWithAvailableCode() {
        dto.setCode("code");
        assertTrue(nomenclatureService.checkCodeAvailability(dto));
    }

    @Test
    @Order(27)
    void test27_checkCodeAvailabilityDto_dtoWithAlreadyTakenCode() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        NomenclatureAlreadyTakenCodeException ex = assertThrows(
                NomenclatureAlreadyTakenCodeException.class,
                () -> {
                    nomenclatureService.checkCodeAvailability(dto);
                },
                "Should have thrown " + NomenclatureAlreadyTakenCodeException.class.getSimpleName()
        );
        assertEquals("Code : " + dto.getCode() + " belongs to id : " + dto.getId(), ex.getMessage());
    }

    @Test
    @Order(28)
    void test28_checkCodeAvailabilityDtoException_dtoWithNullCode() {
        dto.setCode(null);
        assertTrue(nomenclatureService.checkCodeAvailability(dto, e));
        assertEquals(0, e.size());
    }

    @Test
    @Order(29)
    void test29_checkCodeAvailabilityDtoException_dtoWithEmptyCode() {
        dto.setCode(Strings.EMPTY);
        assertTrue(nomenclatureService.checkCodeAvailability(dto, e));
        assertEquals(0, e.size());
    }

    @Test
    @Order(30)
    void test30_checkCodeAvailabilityDtoException_dtoWithBlankCode() {
        dto.setCode("\t");
        assertTrue(nomenclatureService.checkCodeAvailability(dto, e));
        assertEquals(0, e.size());
    }

    @Test
    @Order(31)
    void test31_checkCodeAvailabilityDtoException_dtoWithAvailableCode() {
        dto.setCode("code");
        assertTrue(nomenclatureService.checkCodeAvailability(dto, e));
        assertEquals(0, e.size());
    }

    @Test
    @Order(32)
    void test32_checkCodeAvailabilityDtoException_dtoWithAlreadyTakenCode() {
        String name = "name".toUpperCase(Locale.ROOT);
        String code = "code".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(code);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertFalse(nomenclatureService.checkCodeAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }
    //endregion

    @Test
    @Order(33)
    void test33_createNew_dtoWithAvailableNameWithAvailableCode() {
        dto = NAME1_CODE1;

        result = nomenclatureService.createNew(dto);

        assertTrue(Objects.nonNull(result));
        assertTrue(result.isNotEmpty());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getCode(), result.getCode());
        assertEquals(0, result.getAmount());
        assertEquals(0, e.size());


    }

    //region createNew()
    @Test
    @Order(34)
    void test34_createNew_dtoWithAvailableNameWithNullCode() {
        dto = NAME1_NULL_CODE;

        result = nomenclatureService.createNew(dto);

        assertTrue(Objects.nonNull(result));
        assertTrue(result.isNotEmpty());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
        assertEquals(Strings.EMPTY, result.getCode());
        assertEquals(0, result.getAmount());
        assertEquals(0, e.size());
    }

    @Test
    @Order(35)
    void test35_createNew_dtoWithAvailableNameWithEmptyCode() {
        dto = NAME1_EMPTY_CODE;

        result = nomenclatureService.createNew(dto);

        assertTrue(Objects.nonNull(result));
        assertTrue(result.isNotEmpty());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
        assertEquals(Strings.EMPTY, result.getCode());
        assertEquals(0, result.getAmount());
        assertEquals(0, e.size());
    }

    @Test
    @Order(36)
    void test36_createNew_dtoWithAvailableNameWithBlankCode() {
        dto = NAME1_BLANK_CODE;

        result = nomenclatureService.createNew(dto);

        assertTrue(Objects.nonNull(result));
        assertTrue(result.isNotEmpty());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName(), result.getName());
        assertEquals(Strings.EMPTY, result.getCode());
        assertEquals(0, result.getAmount());
        assertEquals(0, e.size());
    }

    @Test
    @Order(37)
    void test37_createNew_dtoWithAvailableNameWithAlreadyTakenCode() {
        dto.setName("name");
        dto.setCode("code");
        String name = (dto.getName() + "ent").toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(dto.getCode());

        dto.setCode(nomenclatureRepository.save(entity).getCode());

        e = assertThrows(NomenclatureException.class,
                () -> nomenclatureService.createNew(dto));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }

    @Test
    @Order(38)
    void test38_createNew_dtoWithBlankNameWithAvailableCode() {
        dto.setName(null);
        dto.setCode("code1");

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));

        dto.setName(Strings.EMPTY);
        dto.setCode("code2");

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));

        dto.setName("\t");
        dto.setCode("code3");

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(39)
    void test39_createNew_dtoWithAlreadyTakenNameWithAvailableCode() {
        dto.setName("name");
        dto.setCode("code1");

        entity.setName(dto.getName());
        String code = "code2".toUpperCase(Locale.ROOT);
        entity.setCode(code);

        nomenclatureRepository.save(entity);

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
    }

    @Test
    @Order(40)
    void test40_createNew_dtoWithAlreadyTakenNameWithAlreadyTakenCode() {
        dto.setName("name");
        dto.setCode("code");

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());

        nomenclatureRepository.save(entity);

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }

    @Test
    @Order(41)
    void test41_createNew_dtoWithBlankNameWithAlreadyTakenCode() {
        dto.setName("\t");
        dto.setCode("code");

        String name = "name".toUpperCase(Locale.ROOT);
        entity.setName(name);
        entity.setCode(dto.getCode());

        nomenclatureRepository.save(entity);

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(dto);
                });
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(e.getExceptions().get(dto.toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }

    @Test
    @Order(42)
    void test42_createNewFromList_withException() {
        List<NomenclatureDTO> list = new ArrayList<>();
        list.add(new NomenclatureDTO(null, "name1", null, null));//0 ok
        list.add(new NomenclatureDTO(null, "name2", Strings.EMPTY, null));//1 ok
        list.add(new NomenclatureDTO(null, "name3", "\t", null));//2 ok
        list.add(new NomenclatureDTO(null, "name4", "code4", null));//3 ok
        list.add(new NomenclatureDTO(null, "name5", "code4", null));//4 same code
        list.add(new NomenclatureDTO(null, null, null, null));//5 blank name
        list.add(new NomenclatureDTO(null, Strings.EMPTY, null, null));//6 blank name
        list.add(new NomenclatureDTO(null, "\t", null, null));//7 blank name
        list.add(new NomenclatureDTO(null, "name1", "code9", null));//8 same name
        list.add(new NomenclatureDTO(null, "name10", "code10", null));//9 ok
        list.add(new NomenclatureDTO(null, "name4", "code10", null));//10 same name same code
        list.add(new NomenclatureDTO(null, "\t", "code10", null));//11 blank name same code
        list.add(new NomenclatureDTO(null, "name13", "code13", null));//12 ok

        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(list);
                });
        List<NomenclatureDTO> distinctList = list.stream().distinct().toList();
        List<NomenclatureDTO> accepted = e.getAccepted();
        Map<String, String> exceptions = e.getExceptions();

        assertNotNull(accepted);
        assertNotNull(exceptions);
        assertEquals(6, e.countAccepted());
        assertEquals(distinctList.size() - e.countAccepted(), e.size());
        int[] ok = new int[]{0, 1, 2, 3, 9, 12};
        for (int i = 0; i < ok.length; i++) {
            assertNotNull(accepted.get(i));
            assertTrue(accepted.get(i).isNotEmpty());
            assertTrue(accepted.get(i).getId() > 0);
            assertEquals(list.get(ok[i]).getName(), accepted.get(i).getName());
            assertEquals(list.get(ok[i]).getCode(), accepted.get(i).getCode());
            assertEquals(0, accepted.get(i).getAmount());
        }
        assertTrue(exceptions.get(list.get(4).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(5).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(6).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(7).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(8).toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(10).toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(10).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(11).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(11).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }

    @Test
    @Order(43)
    void test43_createNewFromList_correct() {
        List<NomenclatureDTO> list = new ArrayList<>();
        list.add(new NomenclatureDTO(null, "name1", null, null));//0 ok
        list.add(new NomenclatureDTO(null, "name2", Strings.EMPTY, null));//1 ok
        list.add(new NomenclatureDTO(null, "name3", "\t", null));//2 ok
        list.add(new NomenclatureDTO(null, "name4", "code4", null));//3 ok
        list.add(new NomenclatureDTO(null, "name10", "code10", null));//4 ok
        list.add(new NomenclatureDTO(null, "name13", "code13", null));//5 ok

        List<NomenclatureDTO> result = nomenclatureService.createNew(list);
        assertNotNull(result);
        assertEquals(list.size(), result.size());
        for (int i = 0; i < list.size(); i++) {
            assertNotNull(result.get(i));
            assertTrue(result.get(i).isNotEmpty());
            assertTrue(result.get(i).getId() > 0);
            assertEquals(list.get(i).getName(), result.get(i).getName());
            assertEquals(list.get(i).getCode(), result.get(i).getCode());
            assertEquals(0, result.get(i).getAmount());
        }
    }

    @Test
    @Order(44)
    void test44_createNewFromArray_withException() {
        NomenclatureDTO[] array = new NomenclatureDTO[13];
        array[0] = (new NomenclatureDTO(null, "name1", null, null));//0 ok
        array[1] = (new NomenclatureDTO(null, "name2", Strings.EMPTY, null));//1 ok
        array[2] = (new NomenclatureDTO(null, "name3", "\t", null));//2 ok
        array[3] = (new NomenclatureDTO(null, "name4", "code4", null));//3 ok
        array[4] = (new NomenclatureDTO(null, "name5", "code4", null));//4 same code
        array[5] = (new NomenclatureDTO(null, null, null, null));//5 blank name
        array[6] = (new NomenclatureDTO(null, Strings.EMPTY, null, null));//6 blank name
        array[7] = (new NomenclatureDTO(null, "\t", null, null));//7 blank name
        array[8] = (new NomenclatureDTO(null, "name1", "code9", null));//8 same name
        array[9] = (new NomenclatureDTO(null, "name10", "code10", null));//9 ok
        array[10] = (new NomenclatureDTO(null, "name4", "code10", null));//10 same name same code
        array[11] = (new NomenclatureDTO(null, "\t", "code10", null));//11 blank name same code
        array[12] = (new NomenclatureDTO(null, "name13", "code13", null));//12 ok

        List<NomenclatureDTO> list = Arrays.asList(array);
        e = assertThrows(NomenclatureException.class,
                () -> {
                    nomenclatureService.createNew(array);
                });
        List<NomenclatureDTO> distinctList = list.stream().distinct().toList();
        List<NomenclatureDTO> accepted = e.getAccepted();
        Map<String, String> exceptions = e.getExceptions();

        assertNotNull(list);
        assertNotNull(accepted);
        assertNotNull(exceptions);
        assertEquals(6, e.countAccepted());
        assertEquals(distinctList.size() - e.countAccepted(), e.size());
        int[] ok = new int[]{0, 1, 2, 3, 9, 12};
        for (int i = 0; i < ok.length; i++) {
            assertNotNull(accepted.get(i));
            assertTrue(accepted.get(i).isNotEmpty());
            assertTrue(accepted.get(i).getId() > 0);
            assertEquals(list.get(ok[i]).getName(), accepted.get(i).getName());
            assertEquals(list.get(ok[i]).getCode(), accepted.get(i).getCode());
            assertEquals(0, accepted.get(i).getAmount());
        }
        assertTrue(exceptions.get(list.get(4).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(5).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(6).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(7).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(8).toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(10).toString())
                .contains(NomenclatureAlreadyTakenNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(10).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(11).toString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
        assertTrue(exceptions.get(list.get(11).toString())
                .contains(NomenclatureAlreadyTakenCodeException.class.getSimpleName()));
    }

    @Test
    @Order(45)
    void test45_createNewFromArray_correct() {
        NomenclatureDTO[] array = new NomenclatureDTO[6];
        array[0] = (new NomenclatureDTO(null, "name1", null, null));//0 ok
        array[1] = (new NomenclatureDTO(null, "name2", Strings.EMPTY, null));//1 ok
        array[2] = (new NomenclatureDTO(null, "name3", "\t", null));//2 ok
        array[3] = (new NomenclatureDTO(null, "name4", "code4", null));//3 ok
        array[4] = (new NomenclatureDTO(null, "name10", "code10", null));//4 ok
        array[5] = (new NomenclatureDTO(null, "name13", "code13", null));//5 ok

        List<NomenclatureDTO> list = Arrays.asList(array);
        List<NomenclatureDTO> result = nomenclatureService.createNew(list);

        assertNotNull(result);
        assertEquals(array.length, result.size());
        for (int i = 0; i < array.length; i++) {
            assertNotNull(result.get(i));
            assertTrue(result.get(i).isNotEmpty());
            assertTrue(result.get(i).getId() > 0);
            assertEquals(array[i].getName(), result.get(i).getName());
            assertEquals(array[i].getCode(), result.get(i).getCode());
            assertEquals(0, result.get(i).getAmount());
        }
    }
    //endregion

}