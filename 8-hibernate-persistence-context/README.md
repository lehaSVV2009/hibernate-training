# Hibernate persistence context

Hibernate's magic :smile:

## Hibernate entity states

**Transient <-> Persistent <-> Detached**

Persistent - entity is context
Transient, Detached - entity is not in context

### Transient

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

### Make Transient entity Persistence

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

### Persistent (managed) state
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

### Detached state
* Entity has associated identifier;
* It was in persistent state;
* It has no longer associated with persistent context

```
1. session.close();
2. session.clear();
3. session.evict(person);
```

### Removed state

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

### saveOrUpdate

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

### How merge works

* If entity is persistent - does nothing
* If entity is detached, `select from person where id=` and make persistent
* It copies state to persistent entity
* Returns persistent entities

### Spring DATA

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

