# Associations

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
  @JointColumn(name = "person_id")
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

Default TYPE - 

```
FetchType fetch() default EAGER
```

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
  @JointColumn(name = "person_id")
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

## @OneToMany

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
 
  @OneToMany(mappedBy = "phones")
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
  @JointColumn(name = "person_id")
  Person person
}
```
