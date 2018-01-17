# Hibernate caching

## Caching

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
    </defaultCache>  
  </diskStore>
</ehcache>
```

```
@Cache(usage = READ_ONLY, region="person", include = "all | non-lazy")
class Person {

}
```

### Strategies

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