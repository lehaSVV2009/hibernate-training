# Hibernate identities and logging

## Identity of entities

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

## Logging

### Default logger

Hibernate uses PreparedStatement, that's why using

`show_sql`

shows `?` in logs.

```
insert into person(first_name, last_name, id) values (?, ?, ?)
```

### Log4J

Use `slf4j` libraries and put `log4j.properties` to classpath

The following lines tells Hibernate to show values that are going to be inserted in logs
```
log4j.logger.org.hibernate=DEBUG
log4j.logger.org.hibernate.type=TRACE
```

It logs values in prepared statement

### P6spy (Recommended)

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