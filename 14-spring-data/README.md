# Spring Data with hibernate

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