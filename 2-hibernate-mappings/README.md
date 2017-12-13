# Description of entity mapping annotations.

## Type

Hibernate has a lot of types which makes mapping between database column type and java type:

### Primitive Types

| Mapping type | Java type                    | ANSI SQL Type        |
|--------------|------------------------------|----------------------|
| integer      | int or java.lang.Integer     | INTEGER              |
| long         | long or java.lang.Long       | BIGINT               |
| short        | short or java.lang.Short     | SMALLINT             |
| float        | float or java.lang.Float     | FLOAT                |
| double       | double or java.lang.Double   | DOUBLE               |
| big_decimal  | java.math.BigDecimal         | NUMERIC              |
| character    | java.lang.String             | CHAR(1)              |
| string       | java.lang.String             | VARCHAR              |
| byte         | byte or java.lang.Byte       | TINYINT              |
| boolean      | boolean or java.lang.Boolean | BIT                  |
| yes/no       | boolean or java.lang.Boolean | CHAR(1) ('Y' or 'N') |
| true/false   | boolean or java.lang.Boolean | CHAR(1) ('T' or 'F') |

### Date and Time Types

| Mapping type  | Java type                            | ANSI SQL Type |
|---------------|--------------------------------------|---------------|
| date          | java.util.Date or java.sql.Date      | DATE          |
| time          | java.util.Date or java.sql.Time      | TIME          |
| timestamp     | java.util.Date or java.sql.Timestamp | TIMESTAMP     |
| calendar      | java.util.Calendar                   | TIMESTAMP     |
| calendar_date | java.util.Calendar                   | DATE          |

### Binary and Large Object Types

| Mapping type | Java type                                           | ANSI SQL Type       |
|--------------|-----------------------------------------------------|---------------------|
| binary       | byte[]                                              | VARBINARY (or BLOB) |
| text         | java.lang.String                                    | CLOB                |
| serializable | any Java class that implements java.io.Serializable | VARBINARY (or BLOB) |
| clob         | java.sql.Clob                                       | CLOB                |
| blob         | java.sql.Blob                                       | BLOB                |

### JDK-related Types

| Mapping type | Java type          | ANSI SQL Type |
|--------------|--------------------|---------------|
| class        | java.lang.Class    | VARCHAR       |
| locale       | java.util.Locale   | VARCHAR       |
| timezone     | java.util.TimeZone | VARCHAR       |
| currency     | java.util.Currency | VARCHAR       |

Hibernate also contains own types, e.g. 'string', 'java.lang.String' or 'org.hibernate.type.StringNVarcharType'

```
class Actor {

  @Type(type = "string")
  String name;

}
```

## Main

```
package kadet.hibernate

import kadet.hibernate.model.DaNetOldType
import kadet.hibernate.model.Department
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

fun main(args: Array<String>) {
    val metadataSource = MetadataSources(StandardServiceRegistryBuilder().configure().build())

    // There is no way to register all entities from package. Only entity one by one..
    metadataSource.addAnnotatedClass(Department::class.java)

    // Apply basic type not by annotation
    // metadataSource.metadataBuilder.applyBasicType(DaNetOldType()).build()

    val sessionFactory = metadataSource.buildMetadata().buildSessionFactory()

    // EntityManager.java is a JPA analogue of hibernate Session.java
    // Session is not thread safe, so DO NOT USE in different threads (or do it accurately)
    val entityManager = sessionFactory.openSession()

    // Transaction is not thread safe as well
    // 1st level cache works within the transaction
    // It is not recommended to create 2 transactions within 1 session
    entityManager.transaction.begin()

    try {
        val department = entityManager.find(Department::class.java, 1)

        // When you change only one value, Hibernate updates all the columns, not just single.
        // Cause of some corner cases JDBC integration

        println(department)

        entityManager.transaction.commit()
    } catch (e: Exception) {
        // Rollback will be slower without next line
        entityManager.transaction.rollback()
    } finally {
        // Session is short-live
        entityManager.close()
    }

    sessionFactory.close()
}

```

## Current User

```
package kadet.hibernate.model;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = CurrentUserGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

```

and

```
package kadet.hibernate.model;

import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

import java.util.UUID;

public class CurrentUserGenerator implements AnnotationValueGeneration<CurrentUser>{

  @Override
  public void initialize(CurrentUser annotation, Class<?> propertyType) {

  }

  @Override
  public GenerationTiming getGenerationTiming() {
    return GenerationTiming.ALWAYS;
  }

  @Override
  public ValueGenerator<String> getValueGenerator() {
    return (session, owner) -> UUID.randomUUID().toString();
  }

  @Override
  public boolean referenceColumnInSql() {
    // it is generated not in database
    return false;
  }

  @Override
  public String getDatabaseGeneratedReferencedColumnValue() {
    return null;
  }
}
```

## Enums

```
package kadet.hibernate.model

import javax.persistence.AttributeConverter

class DaNetConverter : AttributeConverter<DaNet, Boolean> {
    override fun convertToDatabaseColumn(attribute: DaNet): Boolean? {
        return DaNet.DA == attribute
    }

    override fun convertToEntityAttribute(dbData: Boolean?): DaNet {
        return if (dbData!!) DaNet.DA else DaNet.NET
    }
}
```

or 

```
package kadet.hibernate.model

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType

import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types
import java.util.Objects

class DaNetOldType : UserType {
    override fun sqlTypes(): IntArray {
        // For 0-1 value
        return intArrayOf(Types.BIT)
    }

    override fun returnedClass(): Class<*> {
        return DaNet::class.java
    }

    @Throws(HibernateException::class)
    override fun equals(x: Any, y: Any): Boolean {
        return x == y
    }

    @Throws(HibernateException::class)
    override fun hashCode(x: Any): Int {
        return Objects.hashCode(x)
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeGet(rs: ResultSet, names: Array<String>, session: SharedSessionContractImplementor, owner: Any): Any {
        // names is an array cause you can map Java type to many database columns
        val active = rs.getBoolean(names[0])
        return if (active) DaNet.DA else DaNet.NET
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SharedSessionContractImplementor) {
        st.setBoolean(index, DaNet.DA == value)
    }

    @Throws(HibernateException::class)
    override fun deepCopy(value: Any): Any? {
        return null
    }

    override fun isMutable(): Boolean {
        //
        return true
    }

    @Throws(HibernateException::class)
    override fun disassemble(value: Any): Serializable {
        return value as Serializable
    }

    @Throws(HibernateException::class)
    override fun assemble(cached: Serializable, owner: Any): Any {
        return owner
    }

    @Throws(HibernateException::class)
    override fun replace(original: Any, target: Any, owner: Any): Any? {
        return null
    }
}

```

## Annotations

```
package kadet.hibernate.model

import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.Temporal

@Entity
// @TypeDef(name = "danet", typeClass = DaNetOldType::class)
class Department {

    @Id
    @Column(name = "DEPT_ID") // It's a good practice not to detect name attribute, just @Column
    var id: Int? = null

    // hibernate build-in type like java.lang.String or org.hibernate.type.StringNVarcharType
    @Type(type = "string")
    @Column(name = "NAME")
    var name: String? = null

    // Best practice is not to use @Basic, hibernate does valid fields by default
    // @Basic Tells hibernate that type is basic. Enabled by default for all fields
    // Has @Basic(optional = true) by default, which means that field is not required.
    // If optional = false, and null is saved, it will fail with hibernate
    @Basic(fetch = FetchType.EAGER, optional = true)
    @Column(name = "BASIC")
    var basic: String? = null

    // Best practice is handle nullable in database, not in hibernate
    @Column(name = "COLUMN", nullable = false)
    var column: String? = null

    // Typical mistake using String enums -

    // @Enumerated(EnumType.STRING) - 'male', 'female'
    // val type: Type? = Type.BLA

    // @Enumerated(EnumType.ORDINAL) - 0, 1, 2
    // val type: Type? = Type.BLA

    // @Convert()

    //    @Type(type = "kadet.hibernate.DaNetOldType")
    @Convert(converter = DaNetConverter::class)
    val active: DaNet? = DaNet.NET

    // Old approach
    // @Temporal(...)
    // val date: java.util.Date? = null

    //
    // @Generated
    // Tells hibernate that this column is auto-generated by Database
    // @Generated(GenerationTime.NEVER) by default
    //
    // Hibernate always update value when
    // @Generated(GenerationTime.ALWAYS)


    @Column(name = "lastUpdate", updatable = false)
    @Generated(GenerationTime.ALWAYS)
    val lastUpdate: ZonedDateTime? = null

    val date: LocalDate? = null

    val time: LocalTime? = null

    val datetime: LocalDateTime? = null

    // when noone knows rules of legacy column with (Manda - 134, Alex - 24)

    @CurrentUser
    @Column(name = "updated_by")
    val updatedBy: String? = null

    override fun toString(): String {
        return "Department(id=$id, name=$name)"
    }
}
```