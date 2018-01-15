# Hibernate cascades, flushing, locking

## Cascades


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

### Orphan Removal

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

## Flushing

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

## Locking

Lock allows to normally store data in concurrent environments
e.g. 2 guys try to reduce money in single bank account amount at the same time

It is possible to do it by Java concurrent, but it's tricky.

### Optimistic locking
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

### Pessimistic locking
Before write - lock this row, and no one can change it.

Other transactions will fail/wait/wait with timeout/etc.

Obtain Lock

* `Session.get()` specifying a `LockMode`
* `Query.setLockMode()`

### Lock Modes

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