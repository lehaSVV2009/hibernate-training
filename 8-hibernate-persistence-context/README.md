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

```
// Entity is in TRANSIENT state
Person person = new Person();

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
