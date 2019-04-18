package kadet.hibernate.example.manyToMany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ManyToManyAuthor {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ManyToManyBook> books = new ArrayList<>();

    public void addBook(ManyToManyBook book) {
        books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBook(ManyToManyBook book) {
        books.remove(book);
        book.getAuthors().remove(this);
    }

    public void remove() {
        for (ManyToManyBook book : new ArrayList<>(books)) {
            removeBook(book);
        }
    }
}
