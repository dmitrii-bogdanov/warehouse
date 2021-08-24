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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NomenclatureServiceImplTest {

    @Autowired
    private NomenclatureService nomenclatureService;

    @Autowired
    private NomenclatureRepository nomenclatureRepository;

    @Autowired
    private Mapper mapper;

    //region NomenclatureDTO constants
    private final NomenclatureDTO NAME1_CODE1 =
            new NomenclatureDTO(null, "name1", "code1", null);

    private final NomenclatureDTO NAME1_NULL_CODE =
            new NomenclatureDTO(null, "name1", null, null);

    private final NomenclatureDTO NAME1_EMPTY_CODE =
            new NomenclatureDTO(null, "name1", "", null);

    private final NomenclatureDTO NAME1_BLANK_CODE =
            new NomenclatureDTO(null, "name1", " ", null);

    private final NomenclatureDTO NAME2_CODE1 =
            new NomenclatureDTO(null, "name2", "code1", null);

    private final NomenclatureDTO NAME2_CODE2 =
            new NomenclatureDTO(null, "name2", "code2", null);

    private final NomenclatureDTO NAME2_NULL_CODE =
            new NomenclatureDTO(null, "name2", null, null);

    private final NomenclatureDTO NAME3_CODE3 =
            new NomenclatureDTO(null, "name3", "code3", null);

    private final NomenclatureDTO NAME3_NULL_CODE =
            new NomenclatureDTO(null, "name3", null, null);

    private final NomenclatureDTO NAME3_CODE1 =
            new NomenclatureDTO(null, "name3", "code1", null);

    private final NomenclatureDTO NAME3_CODE2 =
            new NomenclatureDTO(null, "name3", "code2", null);
    //endregion

    private NomenclatureException e;
    private NomenclatureEntity entity;
    private NomenclatureDTO dto;

    @BeforeEach
    private void clear() {
        nomenclatureService.deleteAll();
    }

    @BeforeEach
    private void initializeVariables() {
        e = new NomenclatureException();
        entity = new NomenclatureEntity();
        dto = new NomenclatureDTO();
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
        entity.setName("name");
        entity.setCode("code");
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertTrue(nomenclatureService.checkId(dto));
    }

    @Test
    @Order(4)
    void test4_checkIdDtoException_dtoWithNullId() {
        dto.setId(null);
        assertFalse(nomenclatureService.checkId(dto, e));
        assertTrue(e.getExceptions().get(dto.toFormattedString()).contains(NullIdException.class.getSimpleName()));
    }

    @Test
    @Order(5)
    void test5_checkIdDtoException_dtoWithNotExistingId() {
        dto.setId(1L);
        assertFalse(nomenclatureService.checkId(dto, e));
        assertTrue(e.getExceptions().get(dto.toFormattedString()).contains(ResourceNotFoundException.class.getSimpleName()));
    }

    @Test
    @Order(6)
    void test6_checkIdDtoException_dtoWithExistingId() {
        entity.setName("name");
        entity.setCode("code");
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
        entity.setName("name");
        entity.setCode("code");
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertEquals(dto, mapper.convert(nomenclatureService.checkIdAndRetrieve(dto)));
    }

    @Test
    @Order(10)
    void test10_checkIdAndRetrieveDtoException_dtoWithNullId() {
        dto.setId(null);
        nomenclatureService.checkIdAndRetrieve(dto, e);
        assertEquals(1, e.size());
        assertTrue(e.getExceptions().get(dto.toFormattedString()).contains(NullIdException.class.getSimpleName()));
    }

    @Test
    @Order(11)
    void test11_checkIdAndRetrieveDtoException_dtoWithNotExistingId() {
        dto.setId(1L);
        nomenclatureService.checkIdAndRetrieve(dto, e);
        assertTrue(e.getExceptions().get(dto.toFormattedString()).contains(ResourceNotFoundException.class.getSimpleName()));
    }

    @Test
    @Order(12)
    void test12_checkIdAndRetrieveDtoException_dtoWithExistingId() {
        entity.setName("name");
        entity.setCode("code");
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
        entity.setName("name");
        entity.setCode(null);
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
        assertTrue(e.getExceptions().get(dto.toFormattedString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(19)
    void test19_checkNameAvailabilityDtoException_dtoWithEmptyName() {
        dto.setName(Strings.EMPTY);
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toFormattedString())
                .contains(NomenclatureBlankNameException.class.getSimpleName()));
    }

    @Test
    @Order(20)
    void test20_checkNameAvailabilityDtoException_dtoWithBlankName() {
        dto.setName("\t");
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toFormattedString())
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
        entity.setName("name");
        entity.setCode(null);
        dto = mapper.convert(nomenclatureRepository.save(entity));
        assertFalse(nomenclatureService.checkNameAvailability(dto, e));
        assertTrue(e.getExceptions().get(dto.toFormattedString())
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
        entity.setName("name");
        entity.setCode("code");
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
    //endregion

    @Test
    void createNewTestCorrectDataWithCode() {
        final NomenclatureDTO dto = NAME1_CODE1;

        final NomenclatureDTO result = nomenclatureService.createNew(dto);

        Assertions.assertTrue(Objects.nonNull(result));
        Assertions.assertTrue(Objects.nonNull(result.getId()));
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertTrue(dto.getName().equals(result.getName()));
        Assertions.assertTrue(dto.getCode().equals(result.getCode()));
        Assertions.assertTrue(Long.valueOf(0).equals(result.getAmount()));


    }

    @Test
    void createNewTestCorrectDataWithNullCode() {
        final NomenclatureDTO dto = NAME1_NULL_CODE;

        final NomenclatureDTO result = nomenclatureService.createNew(dto);

        System.out.println();

        Assertions.assertTrue(Objects.nonNull(result));
        Assertions.assertTrue(Objects.nonNull(result.getId()));
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertTrue(dto.getName().equals(result.getName()));
        Assertions.assertTrue(Strings.EMPTY.equals(result.getCode()));
        Assertions.assertTrue(Long.valueOf(0).equals(result.getAmount()));
    }

    @Test
    void createNewTestCorrectDataWithEmptyCode() {
        final NomenclatureDTO dto = NAME1_EMPTY_CODE;

        final NomenclatureDTO result = nomenclatureService.createNew(dto);

        Assertions.assertTrue(Objects.nonNull(result));
        Assertions.assertTrue(Objects.nonNull(result.getId()));
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertTrue(dto.getName().equals(result.getName()));
        Assertions.assertTrue(Strings.EMPTY.equals(result.getCode()));
        Assertions.assertTrue(Long.valueOf(0).equals(result.getAmount()));
    }

    @Test
    void createNewTestCorrectDataWithBlankCode() {
        final NomenclatureDTO dto = NAME1_BLANK_CODE;

        final NomenclatureDTO result = nomenclatureService.createNew(dto);

        Assertions.assertTrue(Objects.nonNull(result));
        Assertions.assertTrue(Objects.nonNull(result.getId()));
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertTrue(dto.getName().equals(result.getName()));
        Assertions.assertTrue(Strings.EMPTY.equals(result.getCode()));
        Assertions.assertTrue(Long.valueOf(0).equals(result.getAmount()));
    }

    //TODO
//    @Test
//    void createNewTestAlreadyTakenCode() {
//        NomenclatureDTO dto1 = NAME1_CODE1;
//        final NomenclatureDTO dto2 = NAME2_CODE1;
//        NomenclatureException e = null;
//
//        dto1 = nomenclatureService.createNew(dto1);
//
//        final NomenclatureAlreadyTakenCodeException codeException =
//                new NomenclatureAlreadyTakenCodeException(
//                        "Code : " + dto2.getCode() + " belongs to id : " + dto1.getId()
//                );
//
//        boolean wasCaught = false;
//        NomenclatureDTO result = null;
//        try {
//            log.info(dto1.toFormattedString());
//            result = nomenclatureService.createNew(dto2);
//            log.info(result.toFormattedString());
//        } catch (NomenclatureException ex) {
//            wasCaught = true;
//            e = ex;
//        }
//
//        Assertions.assertTrue(Objects.isNull(result));
//        Assertions.assertTrue(wasCaught);
//        Assertions.assertTrue(Objects.nonNull(e));
//        Assertions.assertTrue(Objects.nonNull(e.getExceptions()));
//        Assertions.assertTrue(e.size() == 1);
//        Assertions.assertTrue("NomenclatureException".equals(e.getException()));
//        Assertions.assertTrue(e.getExceptions().containsKey(dto2.toFormattedString()));
//        Assertions.assertTrue(
//                e.getExceptions()
//                        .get(dto2.toFormattedString())
//                        .equals(
//                                codeException.getClass().getSimpleName()
//                                        + " : " + codeException.getMessage()
//                        )
//        );
//    }

}