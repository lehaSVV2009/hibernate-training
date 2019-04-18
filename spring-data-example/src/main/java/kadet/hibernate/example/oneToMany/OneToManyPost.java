package kadet.hibernate.example.oneToMany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class OneToManyPost {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OneToManyComment> comments = new ArrayList<>();

    public void addComment(OneToManyComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(OneToManyComment comment) {
        comment.setPost(null);
        comments.remove(comment);
    }
}
