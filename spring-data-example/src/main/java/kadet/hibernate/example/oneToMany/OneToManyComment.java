package kadet.hibernate.example.oneToMany;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class OneToManyComment {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String text;

    @Setter
    @ManyToOne
    private OneToManyPost post;
}
