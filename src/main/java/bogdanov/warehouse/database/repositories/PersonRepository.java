package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    public List<PersonEntity> findAllByEmail(String email);

    //TODO Check what it does
    public List<PersonEntity> findAllByEmailContaining(String partialEmail);

    public List<PersonEntity> findAllByPhoneNumber(String phoneNumber);

    //TODO Check what it does
    public List<PersonEntity> findAllByPhoneNumberStartingWith(String startWith);

    public List<PersonEntity> findAllByCompany(String company);

    //TODO Check what it does
    public List<PersonEntity> findAllByCompanyContaining(String partialCompanyName);

    public List<PersonEntity> findAllByFirstname(String firstname);

    public List<PersonEntity> findAllByLastname(String lastname);

    public List<PersonEntity> findAllByPatronymic(String patronymic);

    public List<PersonEntity> findAllByFirstnameAndLastnameAndPatronymic(String firstname,
                                                                         String lastname,
                                                                         String patronymic);

    public List<PersonEntity> findAllByStaffEquals(StaffEntity staff);

    public List<PersonEntity> findAllByStaffNot(StaffEntity staff);

}
