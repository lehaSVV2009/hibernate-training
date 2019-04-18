package kadet.hibernate.example.manyToMany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ManyToManyAuthorIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ManyToManyAuthorRepository authorRepository;

    @Autowired
    private ManyToManyBookRepository bookRepository;

    @Before
    public void setup() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    public void should_create_author_and_book() {
        // given
        ManyToManyBook book = new ManyToManyBook();
        book.setName("Bla");

        ManyToManyAuthor author = new ManyToManyAuthor();
        author.setName("Alex");
        author.addBook(book);

        entityManager.persist(author);
        entityManager.flush();
        entityManager.clear();

        // when
        List<ManyToManyAuthor> authors = authorRepository.findAll();
        List<ManyToManyBook> books = bookRepository.findAll();

        // then
        assertEquals(1, authors.size());
        assertEquals(1, books.size());
    }
}
