# Description of filters, naming strategies and embedded types.

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

***P.S. it's a good practice to move hibernate configs and transaction wrappers to a separate class (if you don't use Spring)***

## Naming Strategy

Allows to define a name to resolve tables/columns names by any function

`ImplicitNamingStrategy` - applied if column name is implicit

`PhysicalNamingStrategy` - applied if column name is not implicit

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


## Embeddable types

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