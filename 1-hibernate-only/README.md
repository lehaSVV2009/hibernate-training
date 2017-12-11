# Simple demo app with Hibernate only! One entity. No Spring.

Written in Kotlin

Have fun :smile:

## Getting Started

1. Install Java and MySQL


```
brew install mysql
brew cask install java
```

2. Create database

```
mysql -u root -p password

CREATE DATABASE hibernate_lesson
```


3. Change database configuration
```
# Edit in hibernate.cfg.xml
<property name="connection.password">password</property>

```

4. Run app