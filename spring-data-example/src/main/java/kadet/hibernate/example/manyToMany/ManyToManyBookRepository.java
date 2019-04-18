package kadet.hibernate.example.manyToMany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManyToManyBookRepository extends JpaRepository<ManyToManyBook, Long> {
}
