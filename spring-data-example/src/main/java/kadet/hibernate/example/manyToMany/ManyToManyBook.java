package kadet.hibernate.example.manyToMany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ManyToManyBook {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ManyToManyAuthor> authors = new ArrayList<>();
}
