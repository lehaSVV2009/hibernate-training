package kadet.hibernate.example.oneToOne;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OneToOnePostRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OneToOnePostRepository oneToOnePostRepository;

    @Autowired
    private OneToOnePostDetailsRepository oneToOnePostDetailsRepository;

    @Before
    public void setup() {
        oneToOnePostDetailsRepository.deleteAll();
        oneToOnePostRepository.deleteAll();
    }

    @Test
    public void should_save_single_post_without_details() {
        // given
        OneToOnePost post = new OneToOnePost();
        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToOnePost> result = oneToOnePostRepository.findAll();

        // then
        assertNotNull(result.get(0).getId());
        assertNull(result.get(0).getDetails());
    }

    @Test
    public void should_save_single_post_with_details() {
        // given
        OneToOnePost post = new OneToOnePost();
        post.setName("123");

        OneToOnePostDetails details = new OneToOnePostDetails();
        post.addDetails(details);

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToOnePost> result = oneToOnePostRepository.findAll();

        // then
        assertNotNull(result.get(0).getId());
        assertNotNull(result.get(0).getDetails());
        assertNotNull(result.get(0).getDetails().getCreationDate());
        assertNotNull(result.get(0).getDetails().getPost());
    }

    @Test
    public void should_remove_single_post_with_details() {
        // given
        OneToOnePost post = new OneToOnePost();
        post.setName("123");

        OneToOnePostDetails details = new OneToOnePostDetails();
        post.addDetails(details);

        entityManager.persist(post);
        entityManager.flush();
        entityManager.remove(post);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToOnePost> posts = oneToOnePostRepository.findAll();
        List<OneToOnePostDetails> postDetails = oneToOnePostDetailsRepository.findAll();

        // then
        assertEquals(0, posts.size());
        assertEquals(0, postDetails.size());
    }

}
