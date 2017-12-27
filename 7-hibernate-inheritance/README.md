# Hibernate inheritance

## Mapped Superclass

This approach (`mapped superclass`) is just a shortcut, not real inheritance.
Try not to do it like this :smile:

Database tables:

```
CREATE TABLE car (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  PRIMARY KEY(id)
)
```

```
CREATE TABLE truck (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  cargo_capacity int,
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@MappedSuperclass
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

And let's create and get vehicles.

```
// Do In Transaction

Car car = new Car();
car.setVinCode("123123123");
car.setProdDate(LocalDateTime.now());
car.setPassengers(4);

Truck truck = new Truck();
truck.setVinCode("123123123");
truck.setProdDate(LocalDateTime.now());
truck.setCargoCapacity(45);

entityManager.persist(car);
entityManager.persist(truck);

entityManager.flush();
entityManager.clear();

List<Car> cars = entityManager.createQuery("from Car", Car.class).getResultList();
List<Truck> trucks = entityManager.createQuery("from Truck", Truck.class).getResultList();
```

## Table per class

More useful than `Mapped Superclass`, but not really.
There are 3 tables (Vehicles, cars and trucks).
It adds `clazz` column to vehicle column and set 1 (for car) or 2 (for track) value.
It uses `union` when query is called for cars or trucks or vehicles.

Database tables:

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  PRIMARY KEY(id)
)

CREATE TABLE car (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  PRIMARY KEY(id)
)

CREATE TABLE truck (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  cargo_capacity int,
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = ..?) // TODO find what was the strategy
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

## Single table

One table for all sub types.

Database tables:

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  passengers int,
  cargo_capacity int,  
  type varchar(20),
  PRIMARY KEY(id)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type") // Gets vehicle type from type column
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
@DiscrimantorValue("c")
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
@DiscrimantorValue("t")
class Truck extends Vehicle {
  Integer passengers;
}
```

## Join Tables

Most popular. Use it :smile:

Alias - table per subclass

It uses inner join for a single sub-type (`from truck inner join on vehicle`) and outer join for all types.

```
CREATE TABLE vehicle (
  id int,
  vin_code varchar(20),
  prod_date timestamp,
  PRIMARY KEY(id)
)

CREATE TABLE car (
  id int,
  passengers int,
  PRIMARY KEY(id),
  CONSTRAINT `car_vehicle_id` FOREIGN KEY (`id`) REFERENCES `vehicle` (`id`)
)

CREATE TABLE truck (
  id int,
  cargo_capacity int,
  PRIMARY KEY(id),
  CONSTRAINT `truck_vehicle_id` FOREIGN KEY (`id`) REFERENCES `vehicle` (`id`)
)
```

Entities with inheritance:

```
@Entity
@Inheritance(strategy = JOINED)
class Vehicle {

  @Id
  @GeneratedValue
  Long id;

  String vinCode;
  LocalDateTime prodDate;
}

@Entity
class Car extends Vehicle {
  Integer cargoCapacity;
}

@Entity
class Truck extends Vehicle {
  Integer passengers;
}
```

## FAQ

### Which type of inheritance should I use?

Think a lot if you really need inheritance. (Sometime inheritance is a child of evil).
But if you need it, use `Join Tables`.