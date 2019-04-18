package kadet.hibernate.example.oneToMany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
public class OneToManyComment {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String text;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private OneToManyPost post;
}
