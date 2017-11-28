package kadet.hibernate

import kadet.hibernate.model.Department
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

fun main(args: Array<String>) {
    val metadataSource = MetadataSources(StandardServiceRegistryBuilder().configure().build())

    // There is no way to register all entities from package. Only entity one by one..
    metadataSource.addAnnotatedClass(Department::class.java)
    val sessionFactory = metadataSource.buildMetadata().buildSessionFactory()

    // EntityManager.java is a JPA analogue of hibernate Session.java
    // Session is not thread safe, so DO NOT USE in different threads (or do it accurately)
    val entityManager = sessionFactory.openSession()

    // Transaction is not thread safe as well
    // 1st level cache works within the transaction
    // It is not recommended to create 2 transactions within 1 session
    entityManager.transaction.begin()

    try {
        entityManager.find(Department::class.java, 1)
        // Sql query will be ran only once, 1st level cache in progress
        val department = entityManager.find(Department::class.java, 1)

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