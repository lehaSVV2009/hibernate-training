# Hibernate lazy loading

## Lazy Loading

* With Hibernate
`Hibernate.isInitialized(phone.getPerson())`

* With JPA
`Persistence.getPersistenceUtil().isLoaded(phone.getPerson())`

### How to fetch lazy association

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

### Fetching strategies: SELECT

```
@Fetch(SELECT)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Here there is `n+1 select issue`
Fetch 100 person and fetch 100 + 1 phones

```
List<Person> people = entityManager.createQuery("from Person", Person.class);
people.forEach(person -> person.getPhones().size());
```

### Fetching strategies: JOIN
```
@Fetch(FetchMode.JOIN)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Here there is `n+1 select issue` as well

```
List<Person> people = entityManager.createQuery("from Person p left join fetch p.phones", Person.class);
people.forEach(person -> person.getPhones().size());
```

* Does not work with queries

### Fetching strategies: SUBSELECT

```
@Fetch(SUBSELECT)
@OneToMany
Set<Phone> phones = new HashSet<>();
```

Select all child entities at once.

```
List<Person> people = entityManager.createQuery("from Person p", Person.class);
people.forEach(person -> person.getPhones().size());
```


### Fetching strategies: BACTHSIZE

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