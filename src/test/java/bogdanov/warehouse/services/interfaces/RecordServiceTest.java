package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.*;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class RecordServiceTest {

    @Autowired
    private static NomenclatureRepository nomenclatureRepository;
    @Autowired
    private static PersonService personService;
    @Autowired
    private static UserRepository userRepository;
    @Autowired
    private static UserAccountService userAccountService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ReverseRecordRepository reverseRecordRepository;
    @Autowired
    private RecordService recordService;
    @Autowired
    private RecordTypeService recordTypeService;

    private static NomenclatureEntity nomenclatureEntity;
    private static NomenclatureEntity anotherNomenclatureEntity;

    private static UserEntity userEntity;
    private static UserEntity anotherUserEntity;

    @BeforeAll
    private void initializeNomenclatureAndUser() {
        nomenclatureEntity = new NomenclatureEntity();
        nomenclatureEntity.setName("NOMENCLATURE_ITEM_NAME");
        nomenclatureEntity.setCode("NOMENCLATURE_ITEM_CODE");
        nomenclatureEntity = nomenclatureRepository.save(nomenclatureEntity);

        anotherNomenclatureEntity = new NomenclatureEntity();
        anotherNomenclatureEntity.setName("ANOTHER_NOMENCLATURE_ITEM_NAME");
        anotherNomenclatureEntity.setCode("ANOTHER_NOMENCLATURE_ITEM_CODE");
        anotherNomenclatureEntity = nomenclatureRepository.save(anotherNomenclatureEntity);

        PersonDTO person = new PersonDTO();
        person.setBirth(LocalDate.now().minusYears(20));
        person.setFirstname("STAFF");
        person.setLastname("STAFF");
        person = personService.add(person);

        UserAccountWithPasswordDTO accountWithPasswordDTO = new UserAccountWithPasswordDTO();
        accountWithPasswordDTO.setPersonId(person.getId());
        accountWithPasswordDTO.setPassword("password");
        accountWithPasswordDTO.setUsername("user_staff");
        accountWithPasswordDTO.setRoles(new String[]{"ROLE_STAFF", "ROLE_USER"});
        UserAccountDTO accountDTO = userAccountService.add(accountWithPasswordDTO);
        userEntity = userRepository.getById(accountDTO.getId());

        person = new PersonDTO();
        person.setBirth(LocalDate.now());
        person.setFirstname("USER");
        person.setLastname("USER");
        person = personService.add(person);

        accountWithPasswordDTO = new UserAccountWithPasswordDTO();
        accountWithPasswordDTO.setPersonId(person.getId());
        accountWithPasswordDTO.setPassword("password");
        accountWithPasswordDTO.setUsername("user_user");
        accountWithPasswordDTO.setRoles(new String[]{"ROLE_USER"});
        accountDTO = userAccountService.add(accountWithPasswordDTO);
        anotherUserEntity = userRepository.getById(accountDTO.getId());

    }

    @BeforeEach
    private void clearRecordsAndRefreshNomenclatureAmounts() {
        recordRepository.deleteAll();
        reverseRecordRepository.deleteAll();
        nomenclatureEntity.setAmount(0L);
        nomenclatureEntity = nomenclatureRepository.save(nomenclatureEntity);
        anotherNomenclatureEntity.setAmount(0L);
        anotherNomenclatureEntity = nomenclatureRepository.save(anotherNomenclatureEntity);
    }

    private RecordDTO dto;
    private RecordInputDTO input;
    private RecordDTO result;
    private RecordEntity entity;
    private ReverseRecordEntity reverseEntity;

    private final String RECEPTION = "RECEPTION";
    private final String RELEASE = "RELEASE";

    @BeforeEach
    private void initializeVariables() {
        dto = new RecordOutputDTO();
        input = new RecordInputDTO();
        result = null;
        entity = new RecordEntity();
        reverseEntity = null;
    }

    private NomenclatureEntity addToAmount(Long amount, NomenclatureEntity nomenclatureEntity) {
        nomenclatureEntity = nomenclatureRepository.getById(nomenclatureEntity.getId());
        nomenclatureEntity.add(amount);
        return nomenclatureRepository.save(nomenclatureEntity);
    }

    private NomenclatureEntity subtractFromAmountOrSetToZero(Long amount, NomenclatureEntity nomenclatureEntity) {
        nomenclatureEntity = nomenclatureRepository.getById(nomenclatureEntity.getId());
        if (nomenclatureEntity.getAmount() < amount) {
            nomenclatureEntity.setAmount(0L);
        } else {
            nomenclatureEntity.take(amount);
        }
        return nomenclatureRepository.save(nomenclatureEntity);
    }

    private RecordDTO addRecord(Long amount, String type, NomenclatureEntity nomenclatureEntity, UserEntity userEntity) {
        RecordInputDTO inputDTO = new RecordInputDTO();
        inputDTO.setNomenclatureId(nomenclatureEntity.getId());
        inputDTO.setAmount(amount);
        inputDTO.setType(type);
        return recordService.add(inputDTO, userEntity.getUsername());
    }

    private RecordDTO addReverseRecord(NomenclatureEntity nomenclatureEntity, UserEntity userEntity) {
        RecordInputDTO inputDTO = new RecordInputDTO();
        inputDTO.setNomenclatureId(nomenclatureEntity.getId());
        inputDTO.setAmount(5L);
        inputDTO.setType("RECEPTION");
        RecordDTO outputDTO = recordService.add(inputDTO, userEntity.getUsername());
        return recordService.revert(outputDTO.getId(), userEntity.getUsername());
    }

    private RecordInputDTO getInputDTO(Long amount, String type, NomenclatureEntity nomenclatureEntity) {
        if (amount < 0) {
            throw new RuntimeException("Negative amount in TEST getReceptionInputDTO()");
        }
        RecordInputDTO input = new RecordInputDTO();
        input.setType(type);
        input.setAmount(amount);
        input.setNomenclatureId(nomenclatureEntity.getId());
        return input;
    }

    @Test
    void addDto_CorrectData_Reception() {
        Long receptionAmount = 11L;
        input = getInputDTO(receptionAmount, RECEPTION, nomenclatureEntity);

        result = recordService.add(input, userEntity.getUsername());

        assertNotNull(result);

        RecordOutputDTO output = (RecordOutputDTO) result;

        assertEquals(userEntity.getUsername(), output.getUsername());
        assertEquals(receptionAmount, output.getAmount());
        assertEquals(nomenclatureEntity.getId(), output.getNomenclatureId());
        assertNotNull(output.getId());
        assertTrue(output.getId() > 0);
        assertEquals(RECEPTION, output.getType());

        nomenclatureEntity = nomenclatureRepository.getById(nomenclatureEntity.getId());

        assertEquals(receptionAmount, nomenclatureEntity.getAmount());

    }

    @Test
    void addDto_CorrectData_Release() {
        Long initialAmount = 11L;
        addToAmount(initialAmount, nomenclatureEntity);
        Long releaseAmount = 9L;
        input = getInputDTO(releaseAmount, RELEASE, nomenclatureEntity);

        result = recordService.add(input, userEntity.getUsername());

        assertNotNull(result);

        RecordOutputDTO output = (RecordOutputDTO) result;

        nomenclatureEntity = nomenclatureRepository.getById(nomenclatureEntity.getId());
        assertEquals(initialAmount - releaseAmount, nomenclatureEntity.getAmount());
        assertEquals(userEntity.getUsername(), output.getUsername());
        assertEquals(releaseAmount, output.getAmount());
        assertEquals(nomenclatureEntity.getId(), output.getNomenclatureId());
        assertEquals(RELEASE, output.getType());
        assertNotNull(output.getId());
        assertTrue(output.getId() > 0);

    }

}

















