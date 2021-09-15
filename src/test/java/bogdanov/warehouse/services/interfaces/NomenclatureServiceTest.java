package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class NomenclatureServiceTest {

    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private NomenclatureRepository nomenclatureRepository;
    @Autowired
    private RecordService recordService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String NOMENCLATURE = "Nomenclature";
    private final String NAME = "name";
    private final String ID = "id";
    private final String CODE = "code";
    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t  \t\t \t   ";
    private final String RESERVED_NULL_CODE = "null";

    private List<NomenclatureDTO> dto;
    private final long ADD_AMOUNT = 13L;

    @BeforeEach
    private void clear() {
        nomenclatureRepository.deleteAll();
        dto = null;
    }

    private String toUpperCase(String str) {
        return StringUtils.toRootUpperCase(str);
    }

    @Test
    void createNew() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(1L, "Name1", "code1", 34L));//0
        dto.add(new NomenclatureDTO(1L, "Name2", null, 34L));//1
        dto.add(new NomenclatureDTO(1L, "Name3", "code3", 34L));//2
        dto.add(new NomenclatureDTO(null, "Name4", null, null));//3

        List<NomenclatureDTO> result = nomenclatureService.createNew(dto);

        assertNotNull(result);
        assertEquals(dto.size(), result.size());
        for (int i = 0; i < dto.size(); i++) {
            assertNotNull(result.get(i).getId());
            assertTrue(result.get(i).getId() > 0);
            assertEquals(dto.get(i).getName().toUpperCase(Locale.ROOT), result.get(i).getName().toUpperCase(Locale.ROOT));
            assertEquals(toUpperCase(dto.get(i).getCode()), toUpperCase(result.get(i).getCode()));
            assertNotNull(result.get(i).getAmount());
            assertEquals(0L, result.get(i).getAmount());
        }
        assertTrue(nomenclatureService.getAll().containsAll(result));
    }

    @Test
    void createNew_EmptyList() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
    }

    @Test
    void createNew_EmptyDto() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "SomeName", "SomeCode", null));
        dto.add(new NomenclatureDTO());
        dto.add(new NomenclatureDTO(null, "SomeOtherName", "SomeOtherCode", null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.NULL_PROPERTY_WAS_PASSED, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
    }

    @Test
    void createNew_ReservedCodeValue() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "SomeName", "SomeCode", null));
        dto.add(new NomenclatureDTO(null, "name", "NuLl", null));
        dto.add(new NomenclatureDTO(null, "SomeOtherName", "SomeOtherCode", null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.RESERVED_VALUE, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
    }

    @Test
    void createNew_ListContainsRepeatingNames() {
        final String REPEATING_NAME = "SomeName";
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "Name", "SomeCode", null));
        dto.add(new NomenclatureDTO(null, REPEATING_NAME, null, null));
        dto.add(new NomenclatureDTO(null, REPEATING_NAME, "SomeOtherCode", null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.LIST_CONTAINS_REPEATING_VALUES, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
    }

    @Test
    void createNew_ListContainsRepeatingCodes() {
        final String REPEATING_CODE = "SomeCode";
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "Name", "code", null));
        dto.add(new NomenclatureDTO(null, "Name2", REPEATING_CODE, null));
        dto.add(new NomenclatureDTO(null, "SomeOtherName", REPEATING_CODE, null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.LIST_CONTAINS_REPEATING_VALUES, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
    }

    @Test
    void createNew_AlreadyRecordedName() {
        final String RECORDED_NAME = "Recorded Name";
        nomenclatureService.createNew(
                Collections.singletonList(
                        new NomenclatureDTO(null, RECORDED_NAME, "Code", null)));

        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "Name", null, null));
        dto.add(new NomenclatureDTO(null, RECORDED_NAME, null, null));
        dto.add(new NomenclatureDTO(null, "SomeOtherName", null, null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.ALREADY_RECORDED_NAME_OR_CODE, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
    }

    @Test
    void createNew_AlreadyRecordedCode() {
        final String RECORDED_CODE = "Recorded Code";
        nomenclatureService.createNew(
                Collections.singletonList(
                        new NomenclatureDTO(null, "Name0", RECORDED_CODE, null)));

        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(null, "Name1", null, null));
        dto.add(new NomenclatureDTO(null, "Name2", RECORDED_CODE, null));
        dto.add(new NomenclatureDTO(null, "SomeOtherName", null, null));

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.createNew(dto));
        assertEquals(ExceptionType.ALREADY_RECORDED_NAME_OR_CODE, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
    }

    private List<NomenclatureDTO> createSimpleRecords() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        dto.add(new NomenclatureDTO(1L, "Name1", "code1", 34L));//0
        dto.add(new NomenclatureDTO(1L, "Name2", null, 34L));//1
        dto.add(new NomenclatureDTO(1L, "Name3", "code3", 34L));//2

        return new LinkedList<>(nomenclatureService.createNew(dto));
    }

    @Test
    void getAll() {
        List<NomenclatureDTO> result = createSimpleRecords();

        assertTrue(nomenclatureService.getAll().containsAll(result));
    }

    @Test
    void getEntityById() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        for (NomenclatureDTO n : dto) {
            NomenclatureEntity e = nomenclatureService.getEntityById(n.getId());
            assertEquals(n.getId(), e.getId());
            assertEquals(n.getName(), e.getName());
            assertEquals(n.getCode(), e.getCode());
            assertEquals(n.getAmount(), e.getAmount());
        }
    }

    @Test
    void getEntityById_NotRecordedId() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        long id = getNotRecordedId(dto);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityById_NullId() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getEntityById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void getById() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        for (NomenclatureDTO n : dto) {
            assertEquals(n, nomenclatureService.getById(n.getId()));
        }
    }

    private long getNotRecordedId(Collection<NomenclatureDTO> dto) {
        Random generator = new Random(System.nanoTime());
        long id = generator.nextLong();
        while (dto.stream().map(NomenclatureDTO::getId).toList().contains(id)) {
            id = generator.nextLong();
        }
        return id;
    }

    @Test
    void getById_NotRecordedId() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        long id = getNotRecordedId(dto);
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getById_NullId() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        assertFalse(dto.isEmpty());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void update() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        long amount = 2L;
        for (NomenclatureDTO n : dto) {
            n.setAmount(amount + n.getId());
        }
        dto.forEach(nomenclatureService::addAmount);

        for (NomenclatureDTO n : dto) {
            n.setName("Something " + n.getId());
            n.setCode("Something " + n.getId());
            n.setAmount(n.getId() + 34);
            NomenclatureEntity e = nomenclatureRepository.getById(n.getId());
        }

        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> result = nomenclatureService.update(dto);

        assertEquals(size, nomenclatureService.getAll().size());

        assertNotNull(result);
        assertEquals(dto.size(), result.size());
        for (int i = 0; i < dto.size(); i++) {
            assertEquals(dto.get(i).getId(), result.get(i).getId());
            assertEquals(dto.get(i).getName().toUpperCase(Locale.ROOT), result.get(i).getName().toUpperCase(Locale.ROOT));
            assertEquals(toUpperCase(dto.get(i).getCode()), toUpperCase(result.get(i).getCode()));
            assertEquals(amount + dto.get(i).getId(), result.get(i).getAmount());
        }
        assertTrue(nomenclatureService.getAll().containsAll(result));
    }

    @Test
    void update_EmptyList() {
        List<NomenclatureDTO> dto = new LinkedList<>();
        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }
        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_EmptyDto() {
        List<NomenclatureDTO> dto = createSimpleRecords();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }
        dto.get(1).setName(null);
        int size = nomenclatureService.getAll().size();
        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.NULL_PROPERTY_WAS_PASSED, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_ReservedCodeValue() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }
        dto.get(1).setCode("nULl");
        dto.remove(2);
        dto.remove(0);
        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.RESERVED_VALUE, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_ListContainsRepeatingNames() {
        final String REPEATING_NAME = "SomeName";
        List<NomenclatureDTO> dto = createSimpleRecords();
        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }
        dto.get(1).setName(REPEATING_NAME);
        dto.get(2).setName(REPEATING_NAME);
        dto.remove(0);

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.LIST_CONTAINS_REPEATING_VALUES, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_ListContainsRepeatingCodes() {
        final String REPEATING_CODE = "SomeCode";
        List<NomenclatureDTO> dto = createSimpleRecords();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }
        int size = nomenclatureService.getAll().size();

        for (NomenclatureDTO nomenclatureDTO : nomenclatureService.getAll()) {
            log.info(nomenclatureDTO.toString());
        }

        dto.get(1).setCode(REPEATING_CODE);
        dto.get(2).setCode(REPEATING_CODE);
        dto.remove(0);

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.LIST_CONTAINS_REPEATING_VALUES, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_AlreadyRecordedName() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }

        dto.get(1).setName(dto.get(2).getName());
        dto.remove(2);
        dto.remove(0);

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.ALREADY_RECORDED_NAME_OR_CODE, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void update_AlreadyRecordedCode() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        int size = nomenclatureService.getAll().size();

        List<NomenclatureDTO> backup = new LinkedList<>();
        for (NomenclatureDTO n : dto) {
            backup.add(new NomenclatureDTO(n.getId(), n.getName(), n.getCode(), n.getAmount()));
        }

        dto.get(1).setCode(dto.get(2).getCode());
        dto.remove(2);
        dto.remove(0);

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.ALREADY_RECORDED_NAME_OR_CODE, e.getExceptionType());
        assertEquals(size, nomenclatureService.getAll().size());
        assertTrue(nomenclatureService.getAll().containsAll(backup));
    }

    @Test
    void addAmount() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        int size = nomenclatureService.getAll().size();
        NomenclatureDTO dtoAdd = dto.get(1);
        dto.remove(1);
        dtoAdd.setAmount(System.nanoTime() / 2);

        NomenclatureDTO result = nomenclatureService.addAmount(dtoAdd);

        assertNotNull(result);
        assertEquals(dtoAdd.getId(), result.getId());
        assertEquals(result.getName().toUpperCase(Locale.ROOT), result.getName().toUpperCase(Locale.ROOT));
        assertEquals(toUpperCase(dtoAdd.getCode()), toUpperCase(result.getCode()));
        assertEquals(dtoAdd.getAmount(), result.getAmount());
        List<NomenclatureDTO> all = nomenclatureService.getAll();
        assertEquals(size, all.size());
        assertTrue(all.containsAll(dto));
        assertTrue(all.contains(result));
    }

    @Test
    void addAmount_NegativeAmount() {
        dto = createSimpleRecords();
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoAdd.setAmount(4L);
        nomenclatureService.addAmount(dtoAdd);

        dtoAdd.setAmount(-System.nanoTime() / 2);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.addAmount(dtoAdd));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void addAmount_NullAmount() {
        dto = createSimpleRecords();
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());
        dtoAdd.setAmount(4L);
        nomenclatureService.addAmount(dtoAdd);

        dtoAdd.setAmount(null);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.addAmount(dtoAdd));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void addAmount_ZeroAmount() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoAdd.setAmount(0L);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.addAmount(dtoAdd));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void addAmount_NotRecordedId() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoAdd.setAmount(44L);

        dtoAdd.setId(getNotRecordedId(dto));

        dto = nomenclatureService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.addAmount(dtoAdd));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void addAmount_NullId() {
        dto = createSimpleRecords();
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());
        dtoAdd.setAmount(4L);
        nomenclatureService.addAmount(dtoAdd);

        dtoAdd.setAmount(0L);

        dtoAdd.setId(null);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.addAmount(dtoAdd));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    private List<NomenclatureDTO> addAmount(List<NomenclatureDTO> dto) {
        return new LinkedList<>(
                dto
                        .stream()
                        .peek(n -> n.setAmount(n.getAmount() + ADD_AMOUNT))
                        .map(nomenclatureService::addAmount)
                        .toList()
        );
    }

    @Test
    void getAllAvailable() {
        dto = createSimpleRecords();

        List<NomenclatureDTO> result = nomenclatureService.getAllAvailable();
        assertTrue(result.isEmpty());
        result = null;

        dto = addAmount(dto);
        result = nomenclatureService.getAllAvailable();
        assertEquals(dto.size(), result.size());
        assertTrue(result.containsAll(dto));
        result = null;

        NomenclatureDTO dtoZero = new NomenclatureDTO(null, "some name", null, null);
        dtoZero = nomenclatureService.createNew(Collections.singletonList(dtoZero)).get(0);
        result = nomenclatureService.getAllAvailable();
        assertEquals(dto.size(), result.size());
        assertTrue(result.containsAll(dto));
        assertFalse(result.contains(dtoZero));
        assertTrue(nomenclatureService.getAll().containsAll(dto));
        assertTrue(nomenclatureService.getAll().contains(dtoZero));
        result = null;

        NomenclatureDTO deleted = dto.get(1);
        dto.remove(1);
        deleted.setAmount(deleted.getAmount());
        deleted = nomenclatureService.subtractAmount(deleted);
        deleted = nomenclatureService.delete(deleted.getId());

        result = nomenclatureService.getAllAvailable();
        assertTrue(result.containsAll(dto));
        assertFalse(result.contains(deleted));
        assertFalse(result.contains(dtoZero));
        assertTrue(nomenclatureService.getAll().containsAll(dto));
        assertFalse(nomenclatureService.getAll().contains(deleted));
        assertTrue(nomenclatureService.getAll().contains(dtoZero));

    }

    @Test
    void subtractAmount() {
        List<NomenclatureDTO> dto = createSimpleRecords();
        dto = addAmount(dto);
        int size = nomenclatureService.getAll().size();
        NomenclatureDTO dtoSub = dto.get(1);
        dto.remove(1);
        long sub = System.nanoTime() % (ADD_AMOUNT - 1) + 1;
        dtoSub.setAmount(sub);

        NomenclatureDTO result = nomenclatureService.subtractAmount(dtoSub);


        assertNotNull(result);
        assertEquals(dtoSub.getId(), result.getId());
        assertEquals(result.getName().toUpperCase(Locale.ROOT), result.getName().toUpperCase(Locale.ROOT));
        assertEquals(toUpperCase(dtoSub.getCode()), toUpperCase(result.getCode()));
        assertEquals(ADD_AMOUNT - sub, result.getAmount());
        List<NomenclatureDTO> all = nomenclatureService.getAll();
        assertEquals(size, all.size());
        assertTrue(all.containsAll(dto));
        assertTrue(all.contains(result));
    }

    @Test
    void subtractAmount_NegativeAmount() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(-System.nanoTime() / 2);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void subtractAmount_NullAmount() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(null);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void subtractAmount_ZeroAmount() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(0L);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void subtractAmount_NotRecordedId() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(ADD_AMOUNT);

        dtoSub.setId(getNotRecordedId(dto));

        dto = nomenclatureService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void subtractAmount_NullId() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(ADD_AMOUNT);

        dtoSub.setId(null);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void subtractAmount_NotEnoughAmount() {
        dto = createSimpleRecords();
        dto = addAmount(dto);
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoSub = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());

        dtoSub.setAmount(ADD_AMOUNT + 1);

        dto = nomenclatureService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.subtractAmount(dtoSub));
        assertEquals(ExceptionType.NOT_ENOUGH_AMOUNT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void delete() {
        dto = createSimpleRecords();

        NomenclatureDTO deleted = dto.get(1);
        dto.remove(1);

        NomenclatureDTO result = nomenclatureService.delete(deleted.getId());

        List<NomenclatureDTO> all = nomenclatureService.getAll();

        assertNotNull(result);
        assertEquals(deleted, result);
        assertTrue(all.containsAll(dto));
        assertFalse(all.contains(result));
    }

    @Test
    void delete_PositiveAmount() {
        dto = createSimpleRecords();

        NomenclatureDTO deleted = dto.get(1);

        deleted.setAmount(ADD_AMOUNT);
        deleted = nomenclatureService.addAmount(deleted);
        long id = deleted.getId();

        ProhibitedRemovingException e = assertThrows(ProhibitedRemovingException.class,
                () -> nomenclatureService.delete(id));
        assertEquals(ExceptionType.NOMENCLATURE_AMOUNT_IS_POSITIVE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void delete_NullId() {
        dto = createSimpleRecords();

        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.delete(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void delete_NotRegisteredId() {
        dto = createSimpleRecords();

        long id = getNotRecordedId(dto);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.delete(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void delete_ExistingRecord() {
        dto = createSimpleRecords();

        assertFalse(userAccountService.getAll().isEmpty());
        String username = userAccountService.getAll().get(0).getUsername();
        assertNotNull(userAccountService.getByUsername(username));

        NomenclatureDTO deleted = dto.get(1);

        RecordInputDTO record = new RecordInputDTO();
        record.setType(RecordType.RECEPTION.name());
        record.setAmount(2L);
        record.setNomenclatureId(deleted.getId());

        assertNotNull(recordService.add(record, username));

        ProhibitedRemovingException e = assertThrows(ProhibitedRemovingException.class,
                () -> nomenclatureService.delete(deleted.getId()));
        assertEquals(ExceptionType.NOMENCLATURE_HAS_RECORDS, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());

        recordRepository.deleteAll();
    }

    @Test
    void getByCode() {
        dto = addAmount(createSimpleRecords());

        List<NomenclatureDTO> dtoWithoutNullCode = dto.stream().filter(n -> n.getCode() != null).toList();

        for (NomenclatureDTO n : dtoWithoutNullCode) {
            assertEquals(n, nomenclatureService.getByCode(n.getCode().toLowerCase(Locale.ROOT)));
        }

        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_NotRegisteredCode() {
        dto = addAmount(createSimpleRecords());
        String code = dto.get(2).getName().toLowerCase(Locale.ROOT) + "some additional string".toUpperCase(Locale.ROOT);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_NullCode() {
        dto = addAmount(createSimpleRecords());

        String code = NULL_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.BLANK_CODE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_EmptyCode() {
        dto = addAmount(createSimpleRecords());

        String code = EMPTY_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.BLANK_CODE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_SpaceCode() {
        dto = addAmount(createSimpleRecords());

        String code = SPACE_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.BLANK_CODE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_BlankCode() {
        dto = addAmount(createSimpleRecords());

        String code = BLANK_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.BLANK_CODE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByCode_ReservedCode() {
        dto = addAmount(createSimpleRecords());

        String code = RESERVED_NULL_CODE;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByCode(code));
        assertEquals(ExceptionType.RESERVED_VALUE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName() {
        dto = addAmount(createSimpleRecords());

        for (NomenclatureDTO n : dto) {
            assertEquals(n, nomenclatureService.getByName(n.getName().toLowerCase(Locale.ROOT)));
        }

        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName_NotRegisteredName() {
        dto = addAmount(createSimpleRecords());
        String name = dto.get(1).getName().toLowerCase(Locale.ROOT) + "some additional string".toUpperCase(Locale.ROOT);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> nomenclatureService.getByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName_NullName() {
        dto = addAmount(createSimpleRecords());

        String name = NULL_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByName(name));
        assertEquals(ExceptionType.BLANK_NAME, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName_EmptyName() {
        dto = addAmount(createSimpleRecords());

        String name = EMPTY_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByName(name));
        assertEquals(ExceptionType.BLANK_NAME, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName_SpaceName() {
        dto = addAmount(createSimpleRecords());

        String name = SPACE_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByName(name));
        assertEquals(ExceptionType.BLANK_NAME, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void getByName_BlankName() {
        dto = addAmount(createSimpleRecords());

        String name = BLANK_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.getByName(name));
        assertEquals(ExceptionType.BLANK_NAME, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    private List<NomenclatureDTO> createForSearch() {
        dto = new LinkedList<>();
        NomenclatureDTO n;
        int i = 0;
        for (; i < 3; i++) {
            n = new NomenclatureDTO();
            n.setName(NAME + "_" + (i));
            n.setCode(NOMENCLATURE + i);
            dto.add(n);
        }
        n = new NomenclatureDTO();
        n.setName(NAME + "_" + i++);
        n.setCode(null);
        dto.add(n);
        for (; i < 7; i++) {
            n = new NomenclatureDTO();
            n.setName(NOMENCLATURE + (i));
            n.setCode(CODE + "_" + i);
            dto.add(n);
        }
        n = new NomenclatureDTO();
        n.setName(NOMENCLATURE + i);
        n.setCode(null);
        dto.add(n);

        dto = nomenclatureService.createNew(dto);

        i = 0;
        for (NomenclatureDTO d : dto) {
            d.setAmount((long) ++i);
        }
        dto = dto.stream()
                .map(nomenclatureService::addAmount)
                .peek(d -> d.setAmount(1L))
                .map(nomenclatureService::subtractAmount)
                .toList();

        return new LinkedList<>(dto);
    }

    @Test
    void search_AllParametersAreNull() {
        dto = createForSearch();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.search(null, null, null, null));
        assertEquals(ExceptionType.NO_PARAMETER_IS_PRESENT, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    @Test
    void search_MinAmountGreaterThanMaxAmount() {
        dto = createForSearch();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> nomenclatureService.search(null, null, 20L, 10L));
        assertEquals(ExceptionType.INCORRECT_RANGE, e.getExceptionType());
        assertEquals(dto, nomenclatureService.getAll());
    }

    private List<NomenclatureDTO> getCorrectList(List<NomenclatureDTO> dto, String name, String code, Long minAmount, Long maxAmount) {
        return dto.stream()
                .filter(n -> name == null || toUpperCase(n.getName()).contains(toUpperCase(name)))
                .filter(n -> code == null
                        || (!RESERVED_NULL_CODE.equalsIgnoreCase(code) && n.getCode() != null && toUpperCase(n.getCode()).contains(toUpperCase(code)))
                        || (RESERVED_NULL_CODE.equalsIgnoreCase(code) && n.getCode() == null))
                .filter(n -> minAmount == null || (n.getAmount() >= minAmount))
                .filter(n -> maxAmount == null || n.getAmount() <= maxAmount)
                .toList();
    }

    @Test
    void search() {
        dto = createForSearch();

        List<NomenclatureDTO> correct;
        String name;
        String code;
        Long minAmount;
        Long maxAmount;

        name = NAME;
        code = NOMENCLATURE;
        minAmount = 1L;
        maxAmount = 5L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = NAME;
        code = CODE;
        minAmount = 1L;
        maxAmount = 5L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = null;
        code = NOMENCLATURE;
        minAmount = 0L;
        maxAmount = 23L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = NOMENCLATURE;
        code = null;
        minAmount = 1L;
        maxAmount = 5L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = NOMENCLATURE;
        code = RESERVED_NULL_CODE;
        minAmount = 1L;
        maxAmount = 5L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = NAME;
        code = RESERVED_NULL_CODE;
        minAmount = 1L;
        maxAmount = 5L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = null;
        code = RESERVED_NULL_CODE;
        minAmount = null;
        maxAmount = null;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = null;
        code = null;
        minAmount = null;
        maxAmount = 1L;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

        name = null;
        code = null;
        minAmount = 3L;
        maxAmount = null;
        correct = getCorrectList(dto, name, code, minAmount, maxAmount);
        assertEquals(correct, nomenclatureService.search(name, code, minAmount, maxAmount));

    }

}


















