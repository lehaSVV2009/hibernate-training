# Hibernate batching

## Batching

How to add a lot of data

### Issue

```
for (int i = 0: i < 1_000_000; ++i) {
  entityManager.persist(new Person("name_" + i))
}

// OutOfMemoryException
```

`OutOfMemoryException` cause all entities are stored in `Java Memory Heap`

### Solution 1 - clean heap every 100 item

```
for (int i = 0: i < 1_000_000; ++i) {
  entityManager.persist(new Person("name_" + i))
  
  if (i % 100 == 0) {
    entityManager.flush();
    entityManager.clear();
  }
  
}

```

### Solution 2 - JDBC batching hints

It is not really faster than [Solution 1](solution-1)

* `hibernate.jdbc.batch_size`
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