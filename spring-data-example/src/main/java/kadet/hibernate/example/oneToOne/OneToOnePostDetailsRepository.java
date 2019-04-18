package kadet.hibernate.example.oneToOne;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OneToOnePostDetailsRepository extends JpaRepository<OneToOnePostDetails, Long> {
}
