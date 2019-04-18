package kadet.hibernate.example.oneToOne;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
public class OneToOnePost {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private OneToOnePostDetails details;

    public void addDetails(OneToOnePostDetails details) {
        this.details = details;
        details.setPost(this);
    }

    public void removeDetails(OneToOnePostDetails details) {
        if (details != null) {
            details.setPost(null);
        }
        this.details = null;
    }
}
