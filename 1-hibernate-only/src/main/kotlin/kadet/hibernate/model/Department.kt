package kadet.hibernate.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Department {

    @Id
    @Column(name = "DEPT_ID")
    val id: Int? = null

    @Column(name = "NAME")
    val name: String? = null

    override fun toString(): String {
        return "Department(id=$id, name=$name)"
    }
}