# Hibernate spring integration

## Bootstrap

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

### Disadvantages

Lack of methods in `LocalSessionFactoryBean`

## FAQ

* Should I use `@Transactional` for class? or for methods?

Up to you, but it's a good practice to put @Transactional on class, it will be good for get operations performance.