package kadet.hibernate.example.oneToMany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OneToManyCommentRepository extends JpaRepository<OneToManyComment, Long> {

}
