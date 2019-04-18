package kadet.hibernate.example.oneToMany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OneToManyPostRepository extends JpaRepository<OneToManyPost, Long> {

}
