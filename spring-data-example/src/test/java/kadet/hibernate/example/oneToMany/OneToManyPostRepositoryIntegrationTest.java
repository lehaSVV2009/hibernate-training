package kadet.hibernate.example.oneToMany;

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
public class OneToManyPostRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OneToManyPostRepository oneToManyPostRepository;

    @Autowired
    private OneToManyCommentRepository oneToManyCommentRepository;

    @Before
    public void setup() {
        oneToManyCommentRepository.deleteAll();
        oneToManyPostRepository.deleteAll();
    }

    @Test
    public void should_save_single_post_without_comments() {
        // given
        OneToManyPost post = new OneToManyPost();
        post.setName("Bla");

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToManyPost> result = oneToManyPostRepository.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals("Bla", result.get(0).getName());
    }

    @Test
    public void should_save_single_post_with_comments() {
        // given
        OneToManyPost post = new OneToManyPost();
        post.setName("Bla");

        OneToManyComment comment1 = new OneToManyComment();
        comment1.setText("foo");

        OneToManyComment comment2 = new OneToManyComment();
        comment2.setText("bar");

        post.addComment(comment1);
        post.addComment(comment2);

        entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToManyPost> result = oneToManyPostRepository.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals("Bla", result.get(0).getName());
        assertTrue(result.get(0).getComments().stream().allMatch(comment -> comment.getId() != null));
        assertTrue(result.get(0).getComments().stream().anyMatch(comment -> "foo".equals(comment.getText())));
        assertTrue(result.get(0).getComments().stream().anyMatch(comment -> "bar".equals(comment.getText())));
    }

    @Test
    public void should_remove_posts_with_comments() {
        // given
        OneToManyPost post = new OneToManyPost();
        post.setName("Bla");

        OneToManyComment comment1 = new OneToManyComment();
        comment1.setText("foo");

        OneToManyComment comment2 = new OneToManyComment();
        comment2.setText("bar");

        post.addComment(comment1);
        post.addComment(comment2);

        OneToManyPost createdPost = entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        OneToManyPost postToRemove = entityManager.find(OneToManyPost.class, createdPost.getId());
        entityManager.remove(postToRemove);
        entityManager.flush();

        // when
        List<OneToManyPost> posts = oneToManyPostRepository.findAll();
        List<OneToManyComment> comments = oneToManyCommentRepository.findAll();

        // then
        assertEquals(0, posts.size());
        assertEquals(0, comments.size());
    }

    @Test
    public void should_use_orphan_removal_for_comment() {
        // given
        OneToManyPost post = new OneToManyPost();
        post.setName("Bla");

        OneToManyComment comment1 = new OneToManyComment();
        comment1.setText("foo");

        post.addComment(comment1);

        OneToManyPost createdPost = entityManager.persist(post);
        entityManager.flush();
        entityManager.clear();

        createdPost = entityManager.merge(createdPost);
        createdPost.removeComment(createdPost.getComments().get(0));
        entityManager.flush();
        entityManager.clear();

        // when
        List<OneToManyPost> posts = oneToManyPostRepository.findAll();
        List<OneToManyComment> comments = oneToManyCommentRepository.findAll();

        // then
        assertEquals(1, posts.size());
        assertEquals(0, comments.size());
    }

}
