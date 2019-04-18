package kadet.hibernate.example.oneToOne;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
public class OneToOnePostDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Setter
    @OneToOne
    private OneToOnePost post;
}
