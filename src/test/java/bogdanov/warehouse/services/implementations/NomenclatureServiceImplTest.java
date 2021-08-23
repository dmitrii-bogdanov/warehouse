package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.NomenclatureAlreadyTakenCodeException;
import bogdanov.warehouse.exceptions.NomenclatureAlreadyTakenNameException;
import bogdanov.warehouse.exceptions.NomenclatureException;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@Slf4j
@SpringBootTest
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

    @BeforeEach
    private void clear() {
        nomenclatureService.deleteAll();
    }

    @Test
    void checkNameAvailabilityFirstEntry() {
        NomenclatureDTO dto = new NomenclatureDTO();
        dto.setName("Name1");

        Assertions.assertTrue(nomenclatureService.checkNameAvailability(dto));
    }

    @Test
    void checkNameAvailabilityTwoUniqueEntries() {
        NomenclatureEntity entity = new NomenclatureEntity();
        NomenclatureDTO dto = new NomenclatureDTO();
        entity.setName("Name1");
        entity.setCode(Strings.EMPTY);
        dto.setName("Name2");
        nomenclatureRepository.save(entity);

        Assertions.assertTrue(nomenclatureService.checkNameAvailability(dto));
    }

    @Test
    void checkNameAvailabilityTwoIdenticalNames() {
        NomenclatureEntity entity = new NomenclatureEntity();
        NomenclatureDTO dto = new NomenclatureDTO();
        entity.setName("Name1");
        entity.setCode(Strings.EMPTY);
        dto.setName("Name1");
        entity = nomenclatureRepository.save(entity);

        NomenclatureAlreadyTakenNameException e = Assertions.assertThrows(
                NomenclatureAlreadyTakenNameException.class,
                () -> {
                    nomenclatureService.checkNameAvailability(dto);
                },
                "Should have thrown NomenclatureAlreadyTakenNameException"
                );
        Assertions.assertTrue(
                ("Name : " + dto.getName() + " belongs to id : " + entity.getId())
                        .equals(e.getMessage()),
                "Incorrect exception message"
        );
    }

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