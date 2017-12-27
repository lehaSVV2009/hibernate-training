# Hibernate Associations

## @ManyToOne

* Defines a relation between child entity (Phone) and parent entity (Person)

```
@Data
@Entity
class Phone {

  @Id
  @GeneratedValue
  Long id;

  String num;

  @ManyToOne
  @JoinColumn(name = "person_id")
  Person person
}
```

```
@Entity
@Data
class Person {

  @Id
  @GeneratedValue
  Long id;

  // Custom physical naming strategy is implemented
  private String firstName;
  
  private String lastName;
}
```

```
metadataSource.add(Person.class);
metadataSource.add(Phone.class);
```

```
Person person = new Person();
person.setFirstName("fn1");
person.setLastName("ln1");

Phone phone1 = new Phone();
phone1.setNum("111");
phone1.setPerson(person);

Phone phone2 = new Phone();
phone2.setNum("222");
phone2.setPerson(person);

entityManager.persist(person); // insert into person
entityManager.persist(phone1); // insert into phone
entityManager.persist(phone2); // insert into phone

// Do not use 1st level cache.
entityManager.flush();
entityManager.clear();

phone1 = entityManager.find(Person.class, phone1.getId());
phone1.setPerson(null); // insert into where person_id=NULL
```

Default TYPE: `EAGER`

```
FetchType fetch() default EAGER
```

It executes smth like

```
select phone left outer join person
```

Good practice - lazy initialization.
If you need instance, just call `phone.getPerson()` and it will be loaded from db.

```
@Data
@Entity
class Phone {

  @Id
  @GeneratedValue
  Long id;
  
  String num;
  
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "person_id")
  Person person
}
```

The line below will call `select phone`:

```
phone1 = entityManager.find(Person.class, phone1.getId());
```

The line below will call `select person where id = phone.id`

```
phone1.getPerson();
```

## Unidirectional @OneToMany

Put info to parent entity only.

**Not very effective.**

```
class Person {
 
  @OneToMany
  @JoinColumn(name = "person_id")
  List<Phone> phones = new ArrayList<>();
 
}
```

```
class Phone {

  @Id
  @GeneratedValue
  Long id;
  
  String num;
}
```

```

Person person = new Person();
person.setFirstName("fn1");
person.setLastName("ln1");

Phone phone1 = new Phone();
phone1.setNum("111");
person.getPhones().add(phone1);

Phone phone2 = new Phone();
phone2.setNum("222");
person.getPhones().add(phone2);

entityManager.persist(person); // insert into person
entityManager.persist(phone1); // insert into phone + update phone set person_id = 
entityManager.persist(phone2); // insert into phone + update phone set person_id = 

```

Better not to use Unidirectional @OneToMany (cause of performance issues)

## Bidirectional @OneToMany

It is important to detect who should handle mapping (`mappedBy`).
`mappedBy` defines who should fetch dependencies - Person or Phone. It is important for performance.

```
class Person {
 
  // Delegates mapping to person
  @OneToMany(mappedBy = "person")
  @JoinColumn(name = "person_id")
  List<Phone> phones = new ArrayList<>();

  public Person addPhone(Phone phone) {
    getPhones().add(phone);
    return this;
  }
}
```

```
class Phone {

  @Id
  @GeneratedValue
  Long id;
  
  String num;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "person_id")
  Person person
}
```

It is important to add the following methods for bidirectional `OneToMany`

```
public void addPhone(Phone phone) {
  getPhones.add(phone);
  phone.setPerson(this);
}

public void removePhone(Phone phone) {
  getPhones.remove(phone);
  phone.setPerson(this);
}

```

If it looks strange for you, so you can use hibernate byte code enhancer

## @OneToOne

It also might be *unidirectional* and *bidirectional*

```
class Person {
  @Id
  @GeneratedValue
  Long id;

  @OneToOne(mappedBy = "person")
  PersonDetails personDetails;

  public void addPersonDetails(PersonDetails personDetails) {
    this.personDetails = personDetails;
    this.personDetails.setPerson(this);
  }
}
```


```
class PersonDetails {
  @Id
  Long id;
  
  String detail;
  
  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "id") // will join person details with person by id
  @MapsId // Used for shared primary key. It means that id is the same as id of Person
  Person person;
}
```

```
Person person = new Person();
person.setFirstName("fn");

PersonDetails personDetails = new PersonDetails();
personDetails.setDetails("detail1");
person.addPersonDetails(personDetails);

entityManager.persist(person);
entityManager.persist(personDetails);

entityManager.flush();
entityManager.clear();

// Fetch person details without person
PersonDetails details = entityManager.find(PersonDetails.class, 17L);
// Fetch person
details.getPerson();

```

Child entity often more effective for `mappedBy`.

***p.s. DDL auto is a risky thing in OneToOne mappings. It will create new columns..***

One more note - parent object fetches child even if fetch type is LAZY. (Just not be null).

## @ManyToMany

Lazy by default

```
class Person {
  @Id
  @GeneratedValue
  Long id;

  @ManyToMany
  @JoinTable(
    name = "person_bank_account",
    joinColumns = @JoinColumn(name = "person_id"),
    inverseJoinColumn = @JoinColumn(name = "bank_account_id")
  )
  Set<BankAccount> bankAccounts = new HashSet<>();

  public void addBankAccount(BankAccount account) {
    bankAccounts.add(account);
    account.getOwners().add(this);
  }

  public void removeBankAccount(BankAccount bankAccount) {
    bankAccounts.remove(bankAccount);
    bankAccount.getOwners().remove(this);
  }
}
```

```
class BankAccount {
  @Id
  @GeneratedValue
  Long id;

  String num;
  
  BigDecimal amount;

  @ManyToMany(mappedBy = "bankAccounts")
  Set<Person> owners = new HashSet<>();
}
```

```
// Delete old rows
entityManager.createQuery("delete from Point").executeUpdate();
entityManager.createQuery("delete from BankAccount").executeUpdate();

Person person1 = new Person();
person1.setFirstName("fn1");

Person person2 = new Person();
person2.setFirstName("fn1");

BankAccount account1 = new BankAccount();
account1.setNum("1111");
account1.setAmount(new BigDecimal(12.12));

person1.addBankAccount(bankAccount1);
person2.addBankAccount(bankAccount1);

BankAccount account2 = new BankAccount();
account2.setNum("1111");
account2.setAmount(new BigDecimal(12.12));

person2.addBankAccount(bankAccount2);

entityManager.persist(person1);
entityManager.persist(person2);

entityManager.persist(bankAccount1);
entityManager.persist(bankAccount2);

entityManager.flush();
entityManager.clear();

person1 = entityManager.find(Person.class, person1.getId());
bankAccount1 = entityManager.find(BankAccount.class, bankAccount1.getId());

person1.removeBankAccount(bankAccount1);
```

***p.s. DO NOT USE float or double for money! Use BidDecimal or 2 Integers***

It is recommended to initialize collections and use sets (especially in ManyToMany).

Cause for example, remove in List 

```
Set<Person> owners = new HashSet<>();
```

Hibernate has his own list implementations. It helps for scenarios like sorting. (`@Order`)


## @ManyToMany link entity

class Person {

  @OneToMany
  List<PersonBankAccount> personBankAccounts
}

class BankAccount {

  @OneToMany
  List<PersonBankAccount> personBankAccounts
}

class PersonBankAccount {

  @Id
  @ManyToOne
  @JoinColumn(name = "person_id")
  Person person

  @Id
  @ManyToOne
  @JoinColumn(name = "bank_account_id")
  BankAccount bankAccount
}

Advantages:

List are effectively working with link entity approach
Might be more effectively for importing
Sometimes link table can contain additional fields

Disadvantages:

Java heap consumption

1000 people + 1000 bank accounts VS 1000 people + 1000 bank accounts + 1000*1000 links

## FAQ

### Can I call persist for one entity only in many-to-many?

Yes. See future chapters. (Probably 8th or 9th).

### Is it possible to create PersonBankAccount entity in many-to-many? Is it a good practice?

Yes. See [@ManyToMany link entity](#manytomany-link-entity).

### What is a good practice to choose entity that should have `mappedBy` field and entity that should implement mapping

For `one-to-one` or `many-to-one` or `one-to-many` child entity is more effective to work with queries, so use `mappedBy` in parent entity.
For `many-to-many` it doesn't matter.