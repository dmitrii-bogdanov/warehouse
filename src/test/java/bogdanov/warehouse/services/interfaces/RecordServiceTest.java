package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

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
        person.setBirth(LocalDate.now());
        person.setFirstname("STAFF");
        person.setLastname("STAFF");
        person = personService.add(person);

        UserAccountWithPasswordDTO accountWithPasswordDTO = new UserAccountWithPasswordDTO();
        accountWithPasswordDTO.setPersonId(person.getId());
        accountWithPasswordDTO.setPassword("password");
        accountWithPasswordDTO.setUsername("user_staff");
        accountWithPasswordDTO.setRoles(new String[]{"ROLE_STAFF", "ROLE_USER"});
        UserAccountDTO accountDTO =  userAccountService.add(accountWithPasswordDTO);
        userEntity = userRepository.getById(accountDTO.getId());

        person = new PersonDTO();
        person.setBirth(LocalDate.now().minusYears(20));
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


}

















