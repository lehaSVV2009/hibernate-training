package kadet.hibernate.example.manyToMany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManyToManyAuthorRepository extends JpaRepository<ManyToManyAuthor, Long> {
}
