package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.util.*;

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
    private ObjectMapper objectMapper;

    private final String NOMENCLATURE = "Nomenclature";
    private final String NAME = "name";
    private final String ID = "id";
    private final String CODE = "code";

    private List<NomenclatureDTO> dto;

    @BeforeEach
    private void clear() {
        recordRepository.deleteAll();
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

        return nomenclatureService.createNew(dto);
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
        long amount = 1L;
        for (NomenclatureDTO n : dto) {
            n.setName("Something " + n.getId());
            n.setCode("Something " + n.getId());
            n.setAmount(n.getAmount() + n.getId() + 2);
            NomenclatureEntity e = nomenclatureRepository.getById(n.getId());
            e.setAmount(amount);
            nomenclatureRepository.save(e);
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
            assertEquals(amount, result.get(i).getAmount());
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
        dto.remove(0);
        dto.remove(2);
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

        dto.get(1).setCode(REPEATING_CODE);
        dto.get(2).setCode(REPEATING_CODE);
        dto.remove(0);

        ArgumentException e = assertThrows(ArgumentException.class, () -> nomenclatureService.update(dto));
        assertEquals(ExceptionType.LIST_CONTAINS_REPEATING_VALUES, e.getExceptionType());
        assertTrue(nomenclatureService.getAll().isEmpty());
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
        dto.remove(0);
        dto.remove(2);

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
        dto.remove(0);
        dto.remove(2);

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
        long amount = 4L;
        NomenclatureEntity e = nomenclatureRepository.getById(dtoAdd.getId());
        e.setAmount(amount);
        nomenclatureRepository.save(e);

        NomenclatureDTO result = nomenclatureService.addAmount(dtoAdd);

        assertNotNull(result);
        assertEquals(dtoAdd.getId(), result.getId());
        assertEquals(result.getName().toUpperCase(Locale.ROOT), result.getName().toUpperCase(Locale.ROOT));
        assertEquals(toUpperCase(dtoAdd.getCode()), toUpperCase(result.getName()));
        assertEquals(amount + dtoAdd.getAmount(), result.getAmount());
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
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());
        dtoAdd.setAmount(4L);
        nomenclatureService.addAmount(dtoAdd);

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
        NomenclatureDTO dto1 = dto.get(1);
        NomenclatureDTO dtoAdd = new NomenclatureDTO(dto1.getId(), dto1.getName(), dto1.getCode(), dto1.getAmount());
        dtoAdd.setAmount(4L);
        nomenclatureService.addAmount(dtoAdd);

        dtoAdd.setAmount(0L);

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


}


















