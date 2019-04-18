package kadet.hibernate.example.oneToOne;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OneToOnePostRepository extends JpaRepository<OneToOnePost, Long> {
}
