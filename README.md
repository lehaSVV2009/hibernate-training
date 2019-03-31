# Hibernate / Spring Data training

[Hibernate](http://hibernate.org/orm/documentation) ORM enables developers to more easily write applications whose data outlives the application process. 
[Spring Data’s](https://docs.spring.io/spring-data/jpa/docs/current/reference/html) mission is to provide a familiar and consistent, Spring-based programming model for data access while still retaining the special traits of the underlying data store. 

* [Hello World](#hello-world)
* [Data Types](#data-types)
* [Simple Annotations (@Id, @Column, @Type, @Basic, @Generated, @Where, @Filter, @Transient)](#simple-annotations)
* [@Entity](#entity)
* [@Id (types, equals+hashcode, uuid vs long)](#id)
* [@GeneratedValue](#generatedvalue)
* [Custom Annotations (@MyCurrentUser)](#custom-annotations)
* [Enums](#enums)
* [Naming Strategies](#naming-strategies)
* [Embeddable types](#embeddable-types)
* [Entity relationship](#entity-relationship)
* [Inheritance](#inheritance)
* [Entity states](#entity-states)
* [Cascades](#cascades)
* [Flushing](#flushing)
* [Locking](#locking)
* [Lazy Loading](#lazy-loading)
* [Batching](#batching)
* [Sessions](#sessions)
* [Logging](#logging)
* [Caching](#caching)
* [Spring Integration](#spring-integration)
* [Spring Data](#spring-data)
* [References](#references)

# Hello World

Go to this [link](/hibernate-only-example/src/main/kotlin/kadet/hibernate/Application.kt) to see the example of hibernate only application and READ COMMENTS THERE!

# Data Types

Hibernate has a lot of types which makes mapping between database column type and java type:

## Primitive Types

| Mapping type | Java type                    | ANSI SQL Type        |
|--------------|------------------------------|----------------------|
| integer      | int or java.lang.Integer     | INTEGER              |
| long         | long or java.lang.Long       | BIGINT               |
| short        | short or java.lang.Short     | SMALLINT             |
| float        | float or java.lang.Float     | FLOAT                |
| double       | double or java.lang.Double   | DOUBLE               |
| big_decimal  | java.math.BigDecimal         | NUMERIC              |
| character    | java.lang.String             | CHAR(1)              |
| string       | java.lang.String             | VARCHAR              |
| byte         | byte or java.lang.Byte       | TINYINT              |
| boolean      | boolean or java.lang.Boolean | BIT                  |
| yes/no       | boolean or java.lang.Boolean | CHAR(1) ('Y' or 'N') |
| true/false   | boolean or java.lang.Boolean | CHAR(1) ('T' or 'F') |

## Date and Time Types

| Mapping type  | Java type                            | ANSI SQL Type |
|---------------|--------------------------------------|---------------|
| date          | java.util.Date or java.sql.Date      | DATE          |
| time          | java.util.Date or java.sql.Time      | TIME          |
| timestamp     | java.util.Date or java.sql.Timestamp | TIMESTAMP     |
| calendar      | java.util.Calendar                   | TIMESTAMP     |
| calendar_date | java.util.Calendar                   | DATE          |

## Binary and Large Object Types

| Mapping type | Java type                                           | ANSI SQL Type       |
|--------------|-----------------------------------------------------|---------------------|
| binary       | byte[]                                              | VARBINARY (or BLOB) |
| text         | java.lang.String                                    | CLOB                |
| serializable | any Java class that implements java.io.Serializable | VARBINARY (or BLOB) |
| clob         | java.sql.Clob                                       | CLOB                |
| blob         | java.sql.Blob                                       | BLOB                |

## JDK-related Types

| Mapping type | Java type          | ANSI SQL Type |
|--------------|--------------------|---------------|
| class        | java.lang.Class    | VARCHAR       |
| locale       | java.util.Locale   | VARCHAR       |
| timezone     | java.util.TimeZone | VARCHAR       |
| currency     | java.util.Currency | VARCHAR       |

Hibernate also contains own types, e.g. 'string', 'java.lang.String' or 'org.hibernate.type.StringNVarcharType'

```
class Actor {

  @Type(type = "string")
  String name;

}
```

# Simple Annotations

See comments in code of the following annotations:
* `@Id`
* `@Column`
* `@Type`
* `@Basic`
* `@Generated`

```
package kadet.hibernate.model

import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.Temporal

@Entity
// @TypeDef(name = "danet", typeClass = DaNetOldType::class)
class Department {

    @Id
    @Column(name = "DEPT_ID") // It's a good practice not to detect name attribute, just @Column
    var id: Int? = null

    // hibernate build-in type like java.lang.String or org.hibernate.type.StringNVarcharType
    @Type(type = "string")
    @Column(name = "NAME")
    var name: String? = null

    // Best practice is not to use @Basic, hibernate does valid fields by default
    // @Basic Tells hibernate that type is basic. Enabled by default for all fields
    // Has @Basic(optional = true) by default, which means that field is not required.
    // If optional = false, and null is saved, it will fail with hibernate
    @Basic(fetch = FetchType.EAGER, optional = true)
    @Column(name = "BASIC")
    var basic: String? = null

    // Best practice is handle nullable in database, not in hibernate
    @Column(name = "COLUMN", nullable = false)
    var column: String? = null

    // Old approach
    // @Temporal(...)
    // val date: java.util.Date? = null

    //
    // @Generated
    // Tells hibernate that this column is auto-generated by Database
    // @Generated(GenerationTime.NEVER) by default
    //
    // Hibernate always update value when
    // @Generated(GenerationTime.ALWAYS)


    @Column(name = "lastUpdate", updatable = false)
    @Generated(GenerationTime.ALWAYS)
    val lastUpdate: ZonedDateTime? = null

    val date: LocalDate? = null

    val time: LocalTime? = null

    val datetime: LocalDateTime? = null

    // when noone knows rules of legacy column with (Manda - 134, Alex - 24)

    @CurrentUser
    @Column(name = "updated_by")
    val updatedBy: String? = null

    override fun toString(): String {
        return "Department(id=$id, name=$name)"
    }
}
```

## @Where

`@Where` - annotation adds expression to all the db queries for particular table

```
@Where("active = true")
class Country {

  @Column
  Boolean active;

}
```

## @Filter

`@Filter` - more flexible analogue of `@Where` because it can be disabled or enabled

But has not obvious arguments..


```

@FilterDef(name = "active", parameters = @ParamDef(name = "activeValue", type = "boolean"))
@Filter(name = "active", condition = "active: activeValue")
class Actor {

  @Column
  Boolean activeValue;

}

```

And configurations to enable/disable are here:

```

Filter activeActorsFilter = ((Session) entityManager).enableFilter("active")
activeActorsFilter.setParameter("activeValue", true);

```

## @Transient

If you want column not to be mapped as database column, use `@Transient` annotation

```
class Actor {

  // Mapped to database column
  String firstName;
  
  // Not mapped to database column
  @Transient
  String notInDatabase;
}
```

***P.S. it's a good practice to move hibernate configs and transaction wrappers to a separate class (if you don't use Spring)***

# @Entity

Describes mapping between domain model object and table row

Requirements:
* Annotated with `@Entity`
* Must have public or protected **no-argument** constructor
* Top-level class (not inner class)
* Not final class, no final methods or persistent instance variables
* Getters/setters for entity state properties
* Provide identifier attribute (**annotated with @Id**)

```
// name argument can be used in hibernate queries
@Entity(name = "actor")
class Actor {
 
  @Id
  Long id;

}
```

## @Table

This annotation is not required for entity :smile:

* Name of the entity define

```
@Table(name = "table_name", schema = "schema_name", )
```

# @Id

* Uniquely identify each specific entity
* Not required to be the same as table primary key
* Should map to column that can uniquely identify each row
* Identifier is **immutable**

## Identity types

### Assigned ID

Save operation fails if id is not set

```
class Actor {
  @Id
  Long id;
}
```

```
Actor actor = new Actor();
entityManager.persists(actor);
// IdentifierGenerationException

actor.setId(123L);
entityManager.persists(actor);
// Successfully saved

```

### Generated ID

Id is generated by database:

```
class Actor {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  Long id;
}
```

```
Actor actor = new Actor();
// Successfully saved
entityManager.persists(actor);
```

## Single or composite ids

### Simple (single column) ID

```
class Actor {
  @Id
  Long id;
}
```

### Composite (multiple columns) ID

== composite primary key in database

```
@Entity
class FilmActor {

  @EmbeddedId
  PK id

  // Serializable is required for Hibernate 2-level cache
  @Embeddable
  static class PK implements Serializable {
    Long filmId;
    Long actorId;
  }

}
```

```
  FilmActor actor = entityManager.find(FilmActor.class, new FilmActor.PK(20L, 10L));
```

OR

```
@Entity
@IdClass(FilmActorId.class)
class FilmActor {

  @Id
  Long filmId;

  @Id
  Long actorId;

  @Embeddable
  static class PK implements Serializable {
    Long filmId;
    Long actorId;
  }

}
```

```
  FilmActor actor = entityManager.find(FilmActor.class, new FilmActor.PK(20L, 10L));
```

OR

```
@Entity
@IdClass(FilmActorId.class)
class FilmActor implements Serializable {

  @Id
  Long filmId;

  @Id
  Long actorId;
  
  public FilmActor(Long filmId, Long actorId) {
    this.filmId = filmId;
    this.actorId = actorId;
  }
}
```

```
  FilmActor actor = entityManager.find(FilmActor.class, new FilmActor(20L, 10L));
```

But good is:

```
@Entity
class FilmActor implements Serializable {

  // One to one annotiations
  Film film;

  // One to one annotiations
  Actor actor;  
}
```

Equals and hashCode are required to be overridden.

But should equals check for id only?

```
class Actor {
  
  
  public boolean equals(Object o) {
    if (this == 0) return true;
    if (o == null || getClass() )
  }
  
  public int hashCode () {
    
  }
}
```

Case 1

```
Actor actor1 = entityManager.find(Actor.class, 17L);

Actor actor2 = entityManager.find(Actor.class, 17L);

actor1 == actor2

```

Case 2

```
Actor actor1 = entityManager.find(Actor.class, 17L);

// Clear 1st level cache
entityManager.clear();
Actor actor2 = entityManager.find(Actor.class, 17L);

actor1 == actor2

```

Case 3 - check by id only. trouble one. 

```
new Actor().equals(new Actor());
```

Case 4 - check by id only. trouble two.

If we use HashMap or HashSet, hash code should be defined by immutable fields!!!

```
Actor actor = new Actor();
actor3.setName("a");

Set<Actor> actorSet = new HashSet<>();
actorSet.add(actor3);

actorSet.contains(actor3); // returns TRUE!

entityManager.persist(actor3);
// Emulate ...?
entityManager.flush();

actorSet.contains(actor3); // returns FALSE!

```

See article - [Don't let hibernate steal your identity](http://www.onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html)

## Identity rules

Equals and hashCde should be overridden to solve the identity problem

* Use primary key (not the best solution)

When id is assigned after entry is persisted, it breaks equals/hashCode immutability contract for HashSet and HashMap

* Use business keys when possible

Unique value within business keys

* Use manually assigned identifiers (Use only assigned primary key)

```
class Actor {

  UUID id;
  
  public Actor(UUID id) {
    this.id = id;
  }
}
```

## UUID vs Long for ID

UUID takes more memory
UUID is a globally unique - which is good for databases in development
UUID is not good for indexes.
There is a very low probability to have the same uuid which makes it risky.

## Access strategies

Hibernate uses Reflection for entities.

* Field-based access.

```
class Actor {
  @Id
  Long id;
}
```

* Property-based access.

```
class Actor {
  Long id;

  @Id
  public void setId(Long id) {
    this.id = id;
  }
}
```

There is an annotation `@Access()` where you can detect access type.

# @GeneratedValue

Id is generated by database:

```
class Actor {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  Long id;
}
```

See [Identity types](#identity-types)

## Generator types

* AUTO (default) - Provider should choose a strategy
* IDENTITY - db generates id by itself (MYSQL, if batching is required - use sql batching)
* SEQUENCE (default for Oracle and PostrgresQL) - db sequence should be used for obtaining primary key values. Native for db.
* TABLE - db table should be used. (Not really good, YOU should not use it. Only for demo)
 

### IDENTITY generator type

DB generates ID. (Default for MYSQL).
Not good for batches

```
  for (int i = 0: i < 10; ++i) {
    Actor actor = new Actor();
    entityManager.persist(actor);
    // Identity is generated only when I create 
  }

```

### TABLE generator type

Not good to use at all. (But it can work with batch update).

Hibernate requires `hibernate_sequence` table by default for id generation. It contains only `nextVal` column.

The following line will create this table for us.

```
<property name="hibernate.hbm2ddl.auto">update</property>
```

Example of batch update.

```
  for (int i = 0: i < 10; ++i) {
    Actor actor = new Actor();
    entityManager.persist(actor);
    // Hibernate doesn't go to database for id generation 
  }

```

### Custom generator type

Custom sequence:

```
CREATE TABLE hibernate_sequences(sequence_name VARCHAR(40), next_val )
```

```
@GeneratedValue(strategy = TABLE, generator = "actor_gen")
@TableGenerator(name = "actor_gen", initialValue = 2000, allocationSize = 50)
```


# Custom Annotations

Example of custom `@CurrentUser` annotation implementation

```
package kadet.hibernate.model;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = CurrentUserGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

```

and

```
package kadet.hibernate.model;

import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

import java.util.UUID;

public class CurrentUserGenerator implements AnnotationValueGeneration<CurrentUser>{

  @Override
  public void initialize(CurrentUser annotation, Class<?> propertyType) {

  }

  @Override
  public GenerationTiming getGenerationTiming() {
    return GenerationTiming.ALWAYS;
  }

  @Override
  public ValueGenerator<String> getValueGenerator() {
    return (session, owner) -> UUID.randomUUID().toString();
  }

  @Override
  public boolean referenceColumnInSql() {
    // it is generated not in database
    return false;
  }

  @Override
  public String getDatabaseGeneratedReferencedColumnValue() {
    return null;
  }
}
```

and

```
package kadet.hibernate

import kadet.hibernate.model.Department
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

fun main(args: Array<String>) {
    val metadataSource = MetadataSources(StandardServiceRegistryBuilder().configure().build())

    // There is no way to register all entities from package. Only entity one by one..
    metadataSource.addAnnotatedClass(Department::class.java)


    val sessionFactory = metadataSource.buildMetadata().buildSessionFactory()

    // EntityManager.java is a JPA analogue of hibernate Session.java
    // Session is not thread safe, so DO NOT USE in different threads (or do it accurately)
    val entityManager = sessionFactory.openSession()

    // Transaction is not thread safe as well
    // 1st level cache works within the transaction
    // It is not recommended to create 2 transactions within 1 session
    entityManager.transaction.begin()

    try {
        val department = entityManager.find(Department::class.java, 1)

        // When you change only one value, Hibernate updates all the columns, not just single.
        // Cause of some corner cases JDBC integration

        println(department)

        entityManager.transaction.commit()
    } catch (e: Exception) {
        // Rollback will be slower without next line
        entityManager.transaction.rollback()
    } finally {
        // Session is short-live
        entityManager.close()
    }

    sessionFactory.close()
}

```

# Enums

## Simple enums

```
@Entity
class Department {
    ...

    @Enumerated(EnumType.ORDINAL) - 0, 1, 2
    val type: Type? = Type.BLA

    // WARNING! Typical mistake using String enums -
    // @Enumerated(EnumType.STRING) - 'male', 'female'
    // val type: Type? = Type.BLA
}
```

## Custom enums with converters

```
@Entity
class Department {
    ...

    @Convert(converter = DaNetConverter::class)
    val active: DaNet? = DaNet.NET
}
```

```
package kadet.hibernate.model

import javax.persistence.AttributeConverter

class DaNetConverter : AttributeConverter<DaNet, Boolean> {
    override fun convertToDatabaseColumn(attribute: DaNet): Boolean? {
        return DaNet.DA == attribute
    }

    override fun convertToEntityAttribute(dbData: Boolean?): DaNet {
        return if (dbData!!) DaNet.DA else DaNet.NET
    }
}
```

## Custom enums with types

```
@Entity
@TypeDef(name = "danet", typeClass = DaNetOldType::class)
class Department {
    ...

    @Type(type = "kadet.hibernate.DaNetOldType")
    val active: DaNet? = DaNet.NET
}
```

```
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType

import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types
import java.util.Objects

class DaNetOldType : UserType {
    override fun sqlTypes(): IntArray {
        // For 0-1 value
        return intArrayOf(Types.BIT)
    }

    override fun returnedClass(): Class<*> {
        return DaNet::class.java
    }

    @Throws(HibernateException::class)
    override fun equals(x: Any, y: Any): Boolean {
        return x == y
    }

    @Throws(HibernateException::class)
    override fun hashCode(x: Any): Int {
        return Objects.hashCode(x)
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeGet(rs: ResultSet, names: Array<String>, session: SharedSessionContractImplementor, owner: Any): Any {
        // names is an array cause you can map Java type to many database columns
        val active = rs.getBoolean(names[0])
        return if (active) DaNet.DA else DaNet.NET
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SharedSessionContractImplementor) {
        st.setBoolean(index, DaNet.DA == value)
    }

    @Throws(HibernateException::class)
    override fun deepCopy(value: Any): Any? {
        return null
    }

    override fun isMutable(): Boolean {
        //
        return true
    }

    @Throws(HibernateException::class)
    override fun disassemble(value: Any): Serializable {
        return value as Serializable
    }

    @Throws(HibernateException::class)
    override fun assemble(cached: Serializable, owner: Any): Any {
        return owner
    }

    @Throws(HibernateException::class)
    override fun replace(original: Any, target: Any, owner: Any): Any? {
        return null
    }
}

```

# Naming Strategies

Allows to define a name to resolve tables/columns names by any function

There are 2 default strategies

`ImplicitNamingStrategy` - applied if column name is not added

```
class Actor {

  // Hibernate uses property name for column name ('firstName' column)
  String firstName;

}
```

`PhysicalNamingStrategy` - applied if column name is not implicit

```
class Actor {

  // You can add some customizations to name
  @Column(name = "my_project_first_name")
  String firstName;

}
```


The following code allows not to add @Column with underscore name everywhere:

```

public class CamelCaseToUnderscoreNamingStrategy extends PhysicalNamingStrategyStandardImpl {

  public Identifier toPhysicalTableName (Identifier name, JdbcEnvironment context) {
    return new Identifier(LOWER_CAMEL.to(LOWER_UNDERSCORE, name.getText()), name.isQuoted())
  }

  public Identifier toPhysicalColumnName (Identifier name, JdbcEnvironment context) {
    return new Identifier(LOWER_CAMEL.to(LOWER_UNDERSCORE, name.getText()), name.isQuoted())
  }

}

```

***Nice Google Guava is used here***

And add this to hibernate configuration.

```
metadataBuild.applyPhysicalNamingStrategy(new CamelCaseToUnderscoreNamingStrategy());
```

And now forget about underscored name in @Column annotation :)

```
public class Actor {
  @Id
  @Column(name = "actorId")
  private Integer id;

  private String firstName;

  private String lastName;
}

```

***P.S. Guys usually do not use hibernate alone. Spring allows some better customization for such cases.***


# Embeddable types

Code will tell you everything:

```

class Actor {

  @Embedded
  Name name;

  @Embeddable
  class Name {
    String firstName;
    String lastName;
  }
}

```

Or with some customizations:

```

class Actor {

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "giveName", column = @Column(name = "first_name")),
    @AttributeOverride(name = "surName", column = @Column(name = "last_name"))
  })
  Name name;

  @Embeddable
  class Name {
    String givenName;
    String lastName;
  }
}

```

# Entity Relationship

Hibernate associations

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

Advantages:

* List are effectively working with link entity approach
* Might be more effectively and so risky for data insert
* Sometimes link table can contain additional fields

Disadvantages:

* Java heap consumption. 1000 people + 1000 bank accounts VS 1000 people + 1000 bank accounts + n * 1000 links

```
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
```

## FAQ

### Can I call persist for one entity only in many-to-many?

Yes. See future chapters. (Probably 8th or 9th).

### Is it possible to create PersonBankAccount entity in many-to-many? Is it a good practice?

Yes. See [@ManyToMany link entity](#manytomany-link-entity).

### What is a good practice to choose entity that should have `mappedBy` field and entity that should implement mapping

For `one-to-one` or `many-to-one` or `one-to-many` child entity is more effective to work with queries, so use `mappedBy` in parent entity.
For `many-to-many` it doesn't matter.


# Inheritance

## Mapped Superclass

This approach (`mapped superclass`) is just a shortcut, not real inheritance.
Try not to do it like this :smile:

Database tables:

```
CREATE TABLE car (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  PRIMARY KEY(id)
)
```

```
CREATE TABLE truck (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  cargo_capacity int,
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@MappedSuperclass
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

And let's create and get vehicles.

```
// Do In Transaction

Car car = new Car();
car.setVinCode("123123123");
car.setProdDate(LocalDateTime.now());
car.setPassengers(4);

Truck truck = new Truck();
truck.setVinCode("123123123");
truck.setProdDate(LocalDateTime.now());
truck.setCargoCapacity(45);

entityManager.persist(car);
entityManager.persist(truck);

entityManager.flush();
entityManager.clear();

List<Car> cars = entityManager.createQuery("from Car", Car.class).getResultList();
List<Truck> trucks = entityManager.createQuery("from Truck", Truck.class).getResultList();
```

## Table per class

More useful than `Mapped Superclass`, but not really.
There are 3 tables (Vehicles, cars and trucks).
It adds `clazz` column to vehicle column and set 1 (for car) or 2 (for track) value.
It uses `union` when query is called for cars or trucks or vehicles.

Database tables:

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  PRIMARY KEY(id)
)

CREATE TABLE car (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  PRIMARY KEY(id)
)

CREATE TABLE truck (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  cargo_capacity int,
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = ..?) // TODO find what was the strategy
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

## Single table

One table for all sub types.

Database tables:

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  cargo_capacity int,  
  type varchar(20),
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type") // Gets vehicle type from type column
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
@DiscrimantorValue("c")
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
@DiscrimantorValue("t")
class Truck extends Vehicle {
  Integer passengers;
}
```

## Join Tables

Most popular. Use it :smile:

Alias - table per subclass

It uses inner join for a single sub-type (`from truck inner join on vehicle`) and outer join for all types.

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  PRIMARY KEY(id)
)

CREATE TABLE car (
  id int,
  passengers int,
  PRIMARY KEY(id),
  CONSTRAINT `car_vehicle_id` FOREIGN KEY (`id`) REFERENCES `vehicle` (`id`)
)

CREATE TABLE truck (
  id int,
  cargo_capacity int,
  PRIMARY KEY(id),
  CONSTRAINT `truck_vehicle_id` FOREIGN KEY (`id`) REFERENCES `vehicle` (`id`)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = JOINED)
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

## FAQ

### Which type of inheritance should I use?

Think a lot if you really need inheritance. (Sometimes inheritance is a child of evil).
But if you need it, use `Join Tables`.


# Entity states

**Transient <-> Persistent <-> Detached**

Persistent - entity is context
Transient, Detached - entity is not in context

## Transient

* Entity object is just created
* It has no correspond table column
* Id is null (if id is not assigned)
* Hibernate does not manage transient entities

```
// Entity is in TRANSIENT state
Person person = new Person();

```

```
// Entity is not save in DB after the following line
Person person = new Person("123", "456");
```

## Make Transient entity Persistence

```
// Session#save generates id. (100%)
// Request to database is not right now - maybe after transaction commit
Serializable id = session.save(person);
```

or 

```
// Session#persist doesn't guarantee that id is generated.
session.persist(person);
```

***There are no save/update methods in JPA.***

It is recommended to use `persist`

```
Person person = new Person();

// Generates id
session.save(person);
session.flush();
session.clear();

// Geneates id again (cause person id is still null)
session.save(person);
```

```
Person person = new Person();

// Generates id
entityManager.persist(person);
entityManager.flush();
entityManager.clear();

// Geneates id again (cause person id is still null)
entityManager.persist(person);
```

## Persistent (managed) state
* Entity is in 1st level cache


```
Person person = new Person();
person.setLastName("fn");

entityManager.persist(person);
entityManager.flush();
entityManager.clear();

Peron person = entityManager.find(Person.class, person.getId());
// Next line sends UPDATE to DB
person.setLastName("fn1");
```

## Detached state
* Entity has associated identifier;
* It was in persistent state;
* It has no longer associated with persistent context

```
1. session.close();
2. session.clear();
3. session.evict(person);
```

## Removed state

Always associated with transient

```
Person person = new Person();
person.setLastName("fn");

entityManager.persist(person);
entityManager.flush();
entityManager.clear();

Peron person = entityManager.find(Person.class, person.getId());
((Session) entityManager).delete(person);

// Changes will not go to DB
person.setLastName("ln1");
```

## saveOrUpdate

Usually it is not used. It is not comfortable for development cause of some tricky things.

```
Person person = new Person("ln");

entityManager.flush();
entityManager.clear();

// insert
((Session) entityManager).saveOrUpdate(person);
// update
person.setLastName("ln1");
```

```
Person person1 = new Person("ln");

// insert
entityManager.persist(person1);
entityManager.flush();
// Make persons detached
entityManager.clear();

// select
Person person2 = entityManager.find(Person.class, person1.getId());

((Session) entityManager).saveOrUpdate(person2);
// Throws exception - hibernate can have only one persistent entity in heap
```

*Hibernate always make additional select for `saveOrUpdate` method*

Best practices for assigned ID (`@Version`)

```
Person{
  @Id
  // @GeneratedValue
  Long id;
  
  @Version
  Long version;
}
```

DB

```
Add version column to database
It's a specific column for hibernate only, do not set it (it will through exception)
In that case hibernate will not call 
```

If version is null - entity is transient.

Recommended way for merging detached entity

```
Person person = new Person();

// person is persist
entityManager.persist(person);

entityManager.flush();

// person is detached
entityManager.clear();

// person is still detached, persistentPerson is persistent
Person persistentPerson = entityManager.merge(person);

persistentPerson.setLastName("Bla");
```

## How merge works

* If entity is persistent - does nothing
* If entity is detached, `select from person where id=` and make persistent
* It copies state to persistent entity
* Returns persistent entities

## Spring Data states

```
void save() {
  if (Utils.isNew(entity)) {
    entityManager.persist(entity);
  } else {
    ((Session) entityManager).merge(entity);
  }
}
```

Good article [JPA persist and merge](https://vladmihalcea.com/jpa-persist-and-merge/).

### There are 5 methods:

* .save
* .persist
* .saveOrUpdate
* .update - Make detached object persistent
* .merge



# Cascades

No phones in DB:

```
Person person = new Person("ln");

phone1 = new Phone();
person.addPhone(phone1);

phone2 = new Phone();
person.addPhone(phone2);

entityManager.persist(person);
```

Phones in DB:

```
Person person = new Person("ln");

phone1 = new Phone();
person.addPhone(phone1);

phone2 = new Phone();
person.addPhone(phone2);

entityManager.persist(person);
entityManager.persist(phone1);
entityManager.persist(phone2);
```

But why do we need to save phone if person is saved???

Cascading :smile:

```
class Person {

  @OneToMany(mappedBy = "person", cascade = CascadeType.PERSIST)
  Set<Phone> phones = new HashSet();

}
```

Cascade means that if Person is in persist state, phones should be in persist as well


Cascade.PERSIST

```
class Person {

  @OneToMany(mappedBy = "person", cascade = CascadeType.PERSIST)
  Set<Phone> phones = new HashSet();

}

// Person and phones were in transcient state
entityManager.persist(person);
// Now they are in persistent state
```

Cascade.MERGE

```
class Person {

  @OneToMany(mappedBy = "person", cascade = CascadeType.MERGE)
  Set<Phone> phones = new HashSet();

}

// Person and phones were in detached state
entityManager.persist(person);
// Now they are in persistent state
```

Cascade.REMOVE

```
class Person {

  @OneToMany(mappedBy = "person", cascade = CascadeType.DELETE)
  Set<Phone> phones = new HashSet();

}

// Person and phones were in detached state
entityManager.persist(person);
// Now person and phones are in persistent state

entityManager.persist(person);

entityManager.remove(person);
// Attempt to delete person and phones
// Probably failed cause of DB constraints
```


```
class Person {

  @OneToMany(mappedBy = "person", cascade = CascadeType.DELETE)
  Set<Phone> phones = new HashSet();

}

// Person and phones were in detached state
entityManager.persist(person);
// Now person and phones are in persistent state

entityManager.persist(person);

entityManager.remove(person);
// Attempt to delete person and phones
// Probably failed cause of DB constraints
```

Cascade.REFRESH

Specific operation 

```
session.refresh(person); // 
```

Cascade.DETACHED

```
entityManager.evict(person);
```

In 90% cases - @OneToMany(cascade = CascadeType.ALL)

In 90% cases - @OneToOne(cascade = CascadeType.ALL)

In most cases - @ManyToMany(cascade = CascadeType.PERSIST, CascadeType.MERGE)
If person is saved and bank account should be saved as well

If many-to-many with a link entity
Person {
  @OneToMany(cascade = ALL)
}

BankAccount {
  @OneToMany(cascade = ALL)
}

PersonBankAccount {
  @ManyToOne // no cascade here
  @JoinColumn
  Person person
  
  @ManyToOne // no cascade here
  @JoinColumn
  BankAccount bankAccount
}

## Orphan Removal

@OneToMany(orphanRemoval = true)

If phone is removed from person and not assigned to another person - it is automatically removed

```
entityManager.persist(person);
entityManager.flush();

Phone nonPhoneOrphan = person.getPhones().iterator().next();
person.getPhones().remove(nonPhoneOrphan)
nonPhoneOrphan.setPerson(null);

// Non-linked phone is still in DB
```


```
Person {
 
  @OneToMany(orphanRemoval = true)
  Set<Phones> phones
}

entityManager.persist(person);
entityManager.flush();

Phone phoneOrphan = person.getPhones().iterator().next();
person.getPhones().remove(phoneOrphan)

// No phone record in DB
```

# Flushing

Flushing the session forces Hibernate to synchronize the in-memory state of the Session with the database (i.e. to write changes to the database)

Flushing Types:
* ALWAYS - session if flushed before every query. Never do it :smile:
* AUTO (default) - flushed session only when it is necessary (manual or commit)
* COMMIT - flushed only when correspondent transaction is commited
* MANUAL - session is flushed only when `session.flush()` is called explicitly

Auto flash performs:
* before transaction is committed
* auto flush with JPQL/HQL
* auto flush with native SQL (by default it is not done)
```
session.createNativeQuery("select count(*) from person").getSingleResult()
.addSynchronizedEntityClass(Person.class).getSingleResult()
```

Native Query is not evil :smile: Sometimes performance is more important

Order:
1. OrphanRemovalAction
2. EntityInsertAction
3. EntityUpdateAction
4. CollectionRemoveAction
5. CollectionRecreateAction
6. CollectionRecreateAction
7. EntityDeleteAction

## get vs find vs load

There were some troubles in Hibernate namings...

* `entityManager.find` is the same as `session.get` - loads entity by id. 
* `entityManager.getReference` is the same as `session.load` - returns entity reference without initializing data.

```
// Sends DB select
Person person = entityManager.find(Person.class, 10L)

// Doesn't send DB select
Person person = entityManager.getReference(Person.class, 10L)
// Person is a proxy
```

`entityManager.getReference` can be useful to set instances without query

e.g.
```
Person fakePerson = entityManager.getReference(Person.class, 1L) // no queries
fakePerson.getId() // no queries

Phone phone = entityManager.find(Phone.class, 20L) // query

phone.setPerson(fakePerson) // update phone set person_id=1L
```

# Locking

Lock allows to normally store data in concurrent environments
e.g. 2 guys try to reduce money in single bank account amount at the same time

It is possible to do it by Java concurrent, but it's tricky.

## Optimistic locking
Makes operation, if ok - good, if not ok - rollback transaction

It uses `version` column

1st transaction
```
update amount=90 version = 2 where version=1 and id=15
```

2nd parallel transaction
```
update amount=80 version = 2 where version=1 and id=15
// will through exception
```

Example: 

```
class BankAccount {
  Long id;

  @Version
  Integer version
}

Thread thread1 = new Thread(() -> {
  doInTransaction((entityManager) -> {
    BankAccount bankAccount = entityManager.find(BankAccount.class, 10L);
    bankAccount.setAmount(100);
   
    sleep(2000L);
  }
});

thread1.start();

Thread thread2 = new Thread(() -> {
  doInTransaction((entityManager) -> {
    BankAccount bankAccount = entityManager.find(BankAccount.class, 10L);
    bankAccount.setAmount(90);
  }
});

thread2.start();

// throws OptimisticLockException
```

*It's a good practice to add version property for most classes*

## Pessimistic locking
Before write - lock this row, and no one can change it.

Other transactions will fail/wait/wait with timeout/etc.

Obtain Lock

* `Session.get()` specifying a `LockMode`
* `Query.setLockMode()`

## Lock Modes

* UPGRADE (PESSIMISTIC_WRITE) - Pessimistic lock. Performs `SELECT FOR UPDATE`.
* UPGRADE_NOWAIT - Fail if the row is locked. Performs `SELECT FOR UPDATE NOWAIT`.
* UPGRADE_SKIPLOCKED - For update it will skip locked rows when update multiple. Performs `SELECT FOR UPDATE SKIP LOCKED`.

It is possible to have `DEAD LOCK` issues with pessimistic locks.
*It's not a good practice to use pessimistic locking for big apps*

*Do not use thread handling in Java, it's better to handle it in DB*

```
Thread thread1 = new Thread(() -> {
  doInTransaction((entityManager) -> {
    BankAccount bankAccount = entityManager.find(BankAccount.class, 10L, LockModeType.PESSIMISTIC_WRITE);
    bankAccount.setAmount(100);

    sleep(2000L);
  }
});

thread1.start();

Thread thread2 = new Thread(() -> {
  doInTransaction((entityManager) -> {
    BankAccount bankAccount = entityManager.find(BankAccount.class, 10L, LockModeType.PESSIMISTIC_WRITE);
    bankAccount.setAmount(90);
  }
});

thread2.start();

// 2nd thread will wait until locked row is updated
```

*`entityManager.lock(entity, LockModeType.NONE)` doesn't work sometimes*


# Lazy Loading

* With Hibernate
`Hibernate.isInitialized(phone.getPerson())`

* With JPA
`Persistence.getPersistenceUtil().isLoaded(phone.getPerson())`

## How to fetch lazy association

* call `Hibernate.initialize(phone.getPerson())`

* execute phone.getPerson().getId()

```
Person person = entityManager.find(Person.class, 10L);

enittyManager.unwrap(Session.class).evict(person)

// Followin code doesn't throw LazyInitializationExcetpion
if (Hibernate.isInitialized(person.getPhones())) {
  person.getPhones().size();
}

```

## Fetching strategies: SELECT

```
@Fetch(SELECT)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Here there is `n+1 select issue`
e.g. It fetches 100 person and fetch 100 + 1 phones

```
List<Person> people = entityManager.createQuery("from Person", Person.class);
people.forEach(person -> person.getPhones().size());
```

## Fetching strategies: JOIN
```
@Fetch(FetchMode.JOIN)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Here there is `n+1 select issue` as well, so left joint can help

```
List<Person> people = entityManager.createQuery("from Person p left join fetch p.phones", Person.class);
people.forEach(person -> person.getPhones().size());
```

* Does not work with queries

## Fetching strategies: SUBSELECT

```
@Fetch(SUBSELECT)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Select all entities at once and then select all child entities. (2 big selects)

```
List<Person> people = entityManager.createQuery("from Person p", Person.class);
people.forEach(person -> person.getPhones().size());
```


## Fetching strategies: BATCHSIZE

It select n child entities with parent entities at once

```
@BatchSize(5)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

```
List<Person> people = entityManager.createQuery("from Person p", Person.class);
// select phones where phones.person_id in(123, 345, 234, 124, 344)
// select phones where phones.person_id in(890, 567, 456, 356, 145)

people.get(0).getPhones().size(); // no select
people.get(4).getPhones().size(); // no select
people.get(5).getPhones().size(); // select phones where phones.person_id in(546, 346, 766, 345, 233)
```

## JPA entity graph

JPA API to use lazy initializations (probably :smile:)

# Batching

How to add a lot of data

## Issue

```
for (int i = 0: i < 1_000_000; ++i) {
  entityManager.persist(new Person("name_" + i))
}

// OutOfMemoryException
```

`OutOfMemoryException` cause all entities are stored in `Java Memory Heap`

## Solution 1 - clean heap every 100 item

```
for (int i = 0: i < 1_000_000; ++i) {
  entityManager.persist(new Person("name_" + i))
  
  if (i % 100 == 0) {
    entityManager.flush();
    entityManager.clear();
  }
  
}

```

## Solution 2 - JDBC batching hints

It is not really faster than [Solution 1](solution-1)

* `hibernate.jdbc.batch_size` tells JDBC driver to group inserts
* `hibernate.jdbc.batch_versioned_data`
* `hibernate.jdbc.order_inserts, order_updates`

```
// hibernate.cfg
hibernate.jdbc.batch_size=50
```

For MySQL `mysql://localhost:3306/my_db?rewriteBatchedStatements=true`

```

```

*It can't be seen at hibernate log, because it is performed by jdbc driver*


```
for (int i = 0: i < 1_000_000; ++i) {
  entityManager.persist(new Person("name_" + i))
  
  if (i % 100 == 0) {
    entityManager.flush();
    entityManager.clear();
  }
  
}
```

```
insert into person values (1, name_1), (2, name_2), ... (50)
insert into person values (51, name_51), (52, name_52), ... (50)
...
```

# Sessions

## Session scroll

* scroll can be used to retrieve data using cursors
* it can be used efficiently to update date in batches

```
try (ScrollablResults scrollablResults = session.createQuery("from Person", Per.class)
.setCachMode(CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY)) {
  while(scrollableResults.next()) {
    Person person = (Person) scrollableResults.get(0)
  }
}
```

## Stateless session

It doesn't use cache. Used rarely.

```
StatelessSession statelessSession = entityManagerFactory.unwrap(SessionFactory.class).openStatelessSession()

// No cache
statelessSession.insert(person);

```

# Logging

## Default logger

Hibernate uses PreparedStatement, that's why using

`show_sql`

shows `?` in logs.

```
insert into person(first_name, last_name, id) values (?, ?, ?)
```

## Log4J

Use `slf4j` libraries and put `log4j.properties` to classpath

The following lines tells Hibernate to show values that are going to be inserted in logs
```
log4j.logger.org.hibernate=DEBUG
log4j.logger.org.hibernate.type=TRACE
```

It logs values in prepared statement

## P6spy (Recommended)

Spy for driver (MySQL driver, etc.).
Can be used as a logger for all db queries.

Application -> DataSource -> P6SpyDriver -> Driver -> DB

```
compile 'p6spy:p6spy:3.6.0'
```

Add `spy.properties` in classpath (have some conflicts if you use it with log4j.properties)

```
driverList=com.mysql.cj.jdbc.Driver
dateFormat=yyyy-MM-dd HH:mm:ss
appender=com.p6spy.spy.appender.StdoutLogger
logMessageFormat=com.p6spy.engine.spy.appender.MultiLineFormat
```

and change driver in `hibernate.cfg.xml`:

```
<property name="connection.driver_class">com.p6spy....</property>
<property name="connection.url">jdbc:p6spy:mysql://localhost:3306/my_db</property>
```

Has a lot settings, can collect statistics.

# Caching

Types:
* First-level cache
* Second-level cache
* Query cache

## First-level cache

* `write-behind cache`. Duplicated load by id will not call `select .. where id=..` twice
* Cache lives until session is open

## Second-level cache

It is not actually popular now cause of distributed systems.

There are some nice distributed caches (e.g. `Distributed EhCache`, `Apache Ignite`, `Hazelcast`).
But the functionality is complex, so it might have issues with consistency.

* 2nd level cache is bound to the SessionFactory life-cycle
* 3rd party implementation should be used (hibernate doesn't provide 2nd level cache)


```
# Allows 2-nd level cache
hibernate.cache.use_second_level_cache=true

# Add factory of 3rd level library as a default session factory
hibernate...factory_class=org.hibernate.EhCacheSessionFactory

# Link to cache configuration file
hibernate... ??? (location to ehcache.xml)
```

```
# ehcache.xml
<ehcache>
  <diskStore path=..>
    <defaultCache
      maxEntriesLocalHeap=10000
      ...
    >
    </defaultCache>  
  </diskStore>
</ehcache>
```

```
@Cache(usage = READ_ONLY, region="person", include = "all | non-lazy")
class Person {

}
```

## Strategies

* READ_ONLY - exception is thrown in modification
* NONSTRICT_READ_WRITE - only occasionally needs to update 
* READ_WRITE - application needs to update data
* TRANSACTIONAL - with transaction

*Usually `READ_ONLY` and `READ_WRITE` are used*

```
// will not execute DB calls in case of separate transactions:

doInTransaction(em -> em.find(Person.class, 15));

doInTransaction(em -> em.find(Person.class, 15));

```

```
Data Access Login <-> Entity Manager <-> Cache <-> DB

```

Data Access Login - EM - Cache (if no) -> DB 

Advantages:
* Reduce CPU/memory usage..
* Nice for read-only data

Disadvantages:
* Data is stored in separate places
* Changes made directly in database are not known by cache
* Possible inconsistency (cache node and database; different cache nodes);
* Additional complexity (tricky to find appropriate config)
* `Biggest` Non constant time of access (it takes longer after eviction). Sometimes lugging, sometimes not.

2nd level cache stores result sets, not entities.

By default 2nd level cache for collections is not enabled.
To enable 2nd level cache for lists:

```
@Cache()
Set<Phone> phones = new HashSet<>()
```

## Query cache

It allows to cache query results


The following query will load entities from DB twice:

```
entityManager.createQuery("select p from Person p where p.id > 15", Person.class).getResultsList()
entityManager.createQuery("select p from Person p where p.id > 15", Person.class).getResultsList()
```

The following query will load entities from DB once:

```
entityManager.createQuery("select p from Person p where p.id > 15 (:ids)", Person.class)
  .setParameter("ids", asList(4870))
  .setHint("org.hibernate.cacheable", "true")
  .getResultsList()
```

But has some difficulties and corner cases to use

## FAQ

*Should I use 2nd level cache or query cache?*

Only if you have special need for it. It might bring more issues.

*Any Spring integration cache advice?*

You could try to use Spring Cache if you use Spring instead of hibernate 2nd level cache.

# Spring integration

## Quick start

Gradle dependencies:

```
// build.gradle:

compile 'org.hibernate:hibernate-core:5.2.12.Final'
compile 'org.springframework:spring-context:5.0.2.RELEASE'
compile 'org.springframework:spring-orm:5.0.2.RELEASE'
```

Spring configuration:

```
@Configuration
class AppConfig {

  @Bean
  DataSource dataSource() {
    return new DriverManagerDataSource(
      "jdbc:mysql://localhost:3306/my-db",
      "root",
      "password"
    )
  }
  
}

```

Usage:

```
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)

DataSource dataSource = context.getBean(DataSource.class)

ResultSet resultSet = dataSource.createsStatement().executeQuery("select count(id) from person")
resultSet.next().getInt(1)

context.close()

```

*`DriverManagerDataSource` should not be used in production - it always create new connections, no pool connections*

Use `HikariCP` or `Apache DBCP2 BasicDataSource` instead.

Initialize Session factory

```
@Bean
LocalSessionFactoryBean sessionFactoryBean() {
  LocalSessionFactoryBean bean = new LocalSessionFactoryBean()
  bean.setDataSource(dataSource())
  bean.setPackagesToScan("*")
  bean.setPhysicalNamingStrategy(new CamelCaseToUnderscoreNamingStrategy())
  return bean
}
```

```
SessionFactory sessionFactory = context.getBean(SessionFactory.class)
Session session = sessionFactory.openSession()

session.beginTransaction()
Person person = session.get(Person.class, 140L)
```

Initialize transaction manager

```
@EnableTransactionManagement
class TransactionConfig {

  @Bean
  PlatformTransactionManagaer transactionManager(SessionFactory sessionFactory) {
    new HibernateTransactionManager(sessionFactory)
  }

}
```

```
@Service
@Transactional
class MyServiceImpl implements MyService {

  @Autowited
  SessionFactory sessionFactory

  Person findById(Long id) {
    // Stored automatically in ThreadLocal
    Session currentSession = sessionFactory.getCurrentSession()

    return currentSession.get(Person.class, id)
  }
}
```

```
PersonService personService = context.getBean(PersonService.class)
personService.findById(id)
```

*Do not use `session.openSession()` in methods cause it will not use current session - it will create new*

*Note! If throws exception, transaction will not be rollback without rollbackOn*

```
@Transactional(rollbackOn=MyException.class)
class X {

  method() throws MyException {
    // Something that throws MyException
  }

}
```

For JPA

```
@Bean
PlatformTransactionManagaer transactionManager(SessionFactory sessionFactory) {
  new JpaTransactionManager(sessionFactory)
}

```

## Disadvantages

Lack of methods in `LocalSessionFactoryBean`

## FAQ

* Should I use `@Transactional` for class? or for methods?

Up to you, but it's a good practice to put @Transactional on class, it will be good for get operations performance.

# Spring Data

## Spring Data init

Use [Spring Initializer](https://start.spring.io/)

It will generate working project with all settings/required configuration beans

```
compile 'org.springframework.boot:spring-boot-started-data-jpa'
compile 'org.springframework.boot:spring-boot-started-web'
compile 'org.mysql:mysql-connector'
compile 'lombok'
compile 'org.springframework.boot:spring-boot-starter-test'
```

```
@SpringBootApplication
class Application {
  main() {

  }
}
```

application.yml

```
spring.datasource.url=jdbc:mysql://localhost:3306/my-db
spring.datasource.username=root
spring.datasource.password=password
```


```
@Service
class PersonService {

  @PersistenceContext
  EntityManager entityManager

  Person findById(Long id) {
    return entityManager.find(Person.class, id)
  }
}
```

The following code will start when Spring is started

```
@Service
class AppRunner implements CommandLineRunner {

  @Autowired
  PersonService personService

  run (String... args) throws Exception {
    personService.findById(14L)
  }
}
```

## Repository

[Spring Repository docs](https://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html)
It moved the idea of Java DB apps to another way :smile:

```
class PersonRepository implementds CrudRepository<Person, Long> {
}
```

```
@Service
class AppRunner implements CommandLineRunner {

  @Autowired
  PersonRepository personRepository

  run (String... args) throws Exception {
    personRepository.findById(14L)
  }
}
```

## Query methods

```
class PersonRepository implementds CrudRepository<Person, Long> {
  List<Person> findTop50ByName()
}

personRepository.findTop50ByName()
```

## Custom Hql

```
class PersonRepository implementds CrudRepository<Person, Long> {
  @Query("select p from Person p left join fetch p.phones")
  List<Person> findCustom()
}
```

## FAQ

What does `@PersistenceContext` mean?

# References

* Hibernate Documentation - [http://hibernate.org/orm/documentation](http://hibernate.org/orm/documentation)
* Spring Data Documentation - [https://docs.spring.io/spring-data/jpa/docs/current/reference/html](https://docs.spring.io/spring-data/jpa/docs/current/reference/html)
