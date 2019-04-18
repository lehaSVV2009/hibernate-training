package kadet.hibernate.example.oneToOne;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Entity
public class OneToOnePostDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "creation_date", updatable = false)
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @Setter
    @OneToOne
    private OneToOnePost post;
}
