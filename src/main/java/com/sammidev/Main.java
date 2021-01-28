package com.sammidev;

import lombok.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Sammi ALdhi Yanto | 2021
 * JLove | Kotlin Fan and Cat Lover
 * Instagram : _sammidev
 * Mail   : sammidev4@gmail.com
 * Github : github.com/SemmiDev
 */


public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        PersonService personService = applicationContext.getBean("personService", PersonService.class);
        Person personTest = Person.builder()
                .id(UUID.randomUUID())
                .name("C")
                .gender(GENDER.MALE)
                .email("sammidev@mail.com")
                .bankAccount(BankAccount.builder()
                        .id(UUID.randomUUID())
                        .accountNumber(UUID.randomUUID())
                        .balance(new BigDecimal(100000))
                        .build())
                .build();

        Person personTest2 = Person.builder()
                .id(UUID.randomUUID())
                .name("D")
                .gender(GENDER.MALE)
                .email("sammidev@mail.com")
                .bankAccount(BankAccount.builder()
                        .id(UUID.randomUUID())
                        .accountNumber(UUID.randomUUID())
                        .balance(new BigDecimal(200000))
                        .build())
                .build();

        personService.createPerson(personTest);
        personService.createPerson(personTest2);

        System.out.println("------------------- BEFORE");
        System.out.println(personService.getPersonById(personTest.getId()).getBankAccount().getBalance());
        System.out.println(personService.getPersonById(personTest2.getId()).getBankAccount().getBalance());
        System.out.println("------------------- END  BEFORE");
        personService.transferMoney(personTest.getId(), personTest2.getId(), new BigDecimal(200000));
        System.out.println("------------------- AFTER");
        System.out.println(personService.getPersonById(personTest.getId()).getBankAccount().getBalance());
        System.out.println(personService.getPersonById(personTest2.getId()).getBankAccount().getBalance());
        System.out.println("------------------- END  AFTER");

        personService.findAllPerson().forEach(
                (k,v) -> {
                    System.out.println(k + "->" + v );
                }
        );

    }
}

@Configuration
class ApplicationConfig {

    @Bean
    public PersonRepository personRepository() {
        return new PersonRepositoryImpl();
    }

    @Bean
    public PersonService personService(PersonRepository personRepository) {
        var personService = new PersonServiceImpl();
        personService.setPersonRepository(personRepository);
        return personService;
    }
}

@Data
class PersonServiceImpl implements PersonService {

    PersonRepository personRepository;

    @Override
    public Map<UUID, Person> findAllPerson() {
        return personRepository.findAllPerson();
    }

    @Override
    public void createPerson(Person person) {
        personRepository.createPerson(person);
    }

    @Override
    public void transferMoney(UUID personSourceId, UUID personTargetId, BigDecimal balance) {
        Person personSource = personRepository.findPersonById(personSourceId);
        Person personTarget = personRepository.findPersonById(personTargetId);

        if (personTarget == null) {
            throw new PersonTargetNotFoundException("PERSON TARGET NOT FOUND");
        }else if (personSource == null) {
            throw new PersonTargetNotFoundException("PERSON TARGET NOT FOUND");
        }

        if (personSource.getBankAccount().getBalance().compareTo(balance) == -1) {
            throw new BalanceNotNeccesaryException("BalanceNotNeccesary");
        }

        BigDecimal add = personSource.getBankAccount().getBalance().add(balance);
        BigDecimal subtract = personTarget.getBankAccount().getBalance().subtract(balance);
        personSource.getBankAccount().setBalance(add);
        personTarget.getBankAccount().setBalance(subtract);

        personRepository.updatePerson(personSource);
        personRepository.updatePerson(personTarget);
    }

    @Override
    public Person getPersonById(UUID id) {
        return personRepository.findPersonById(id);
    }
}

interface PersonService {
    Map<UUID, Person> findAllPerson();
    void createPerson(Person person);
    void transferMoney(UUID personSourcetId, UUID personTargetId, BigDecimal balance);
    Person getPersonById(UUID id);
}

class PersonRepositoryImpl implements PersonRepository {

    private Map<UUID,Person> persons = new HashMap<>();

    {
        var bankAccountsammi = BankAccount.builder()
                .id(UUID.randomUUID())
                .balance(new BigDecimal(200000000))
                .accountNumber(UUID.randomUUID())
                .type(BANKTYPE.BNI)
                .build();

        var personsammi = Person.builder()
                .id(UUID.randomUUID())
                .name("A")
                .email("A@gmail.com")
                .gender(GENDER.MALE)
                .bankAccount(bankAccountsammi)
                .build();

        var bankAccountadit = BankAccount.builder()
                .id(UUID.randomUUID())
                .balance(new BigDecimal(300000000))
                .accountNumber(UUID.randomUUID())
                .type(BANKTYPE.BRI)
                .build();

        var personadit = Person.builder()
                .id(UUID.randomUUID())
                .name("B")
                .email("B@gmail.com")
                .gender(GENDER.FEMALE)
                .bankAccount(bankAccountadit)
                .build();

        this.persons.put(personsammi.getId(), personsammi);
        this.persons.put(personadit.getId(), personadit);
    }

    @Override
    public Map<UUID, Person> findAllPerson() {
        return this.persons;
    }

    @Override
    public void createPerson(Person person) {
        this.persons.put(person.getId(), person);
    }

    @Override
    public void updatePerson (Person person) {
        this.persons.put(person.getId(), person);
    }

    @Override
    public Person findPersonById(UUID id) {

        var ref = new Object() {
            Person person = new Person();
        };

        persons.forEach((k,v) -> {
            if (k.equals(id)) {
                ref.person = Person.builder()
                        .id(k)
                        .name(v.getName())
                        .email(v.getEmail())
                        .gender(v.getGender())
                        .bankAccount(v.getBankAccount())
                        .build();
            }
        });

        return ref.person;
    }
}

interface PersonRepository {
    Map<UUID, Person>  findAllPerson();
    void createPerson(Person person);
    void updatePerson(Person person);
    Person findPersonById(UUID id);
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Person{
    private UUID id;
    private String name;
    private String email;
    private GENDER gender;
    private BankAccount bankAccount;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class BankAccount {
    private UUID id;
    private UUID accountNumber;
    private BANKTYPE type;
    private BigDecimal balance;
}

@AllArgsConstructor
enum GENDER {
    MALE("LAKI-LAKI"),
    FEMALE("PEREMPUAN");
    private String details;
}

@AllArgsConstructor
enum BANKTYPE {
    MANDIRI("MANDIRI"),
    BCA("BCA"),
    BRI("BRI"),
    BNI("BNI");
    private String details;
}

@Getter
class PersonTargetNotFoundException extends RuntimeException {

    private String message;
    public PersonTargetNotFoundException(String message) {
        this.message = message;
    }
}

@Getter
class PersonSourceNotFoundException extends RuntimeException {

    private String message;
    public PersonSourceNotFoundException(String message) {
        this.message = message;
    }
}

@Getter
class BalanceNotNeccesaryException extends RuntimeException {
    private String message;
    public BalanceNotNeccesaryException(String message) {
        this.message = message;
    }
}