package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void findByItemId_success() {
        User owner1 = newUser("owner1@mail.ru");
        em.persist(owner1);

        User owner2 = newUser("owner2@mail.ru");
        em.persist(owner2);

        User user1 = newUser("user1@mail.ru");
        em.persist(user1);

        User user2 = newUser("user2@mail.ru");
        em.persist(user2);

        Item item1 = newItem(owner1, "Вещь 1", "Комментарий к вещи 1");
        em.persist(item1);

        Item item2 = newItem(owner2, "Вещь 2", "Комментарий к вещи 2");
        em.persist(item2);

        em.persist(newComment(user1, item1, "Первый"));
        em.persist(newComment(user1, item2, "Второй"));
        em.persist(newComment(user2, item2, "Третий"));

        List<Comment> comments = commentRepository.findByItemId(item2.getId());
        assertEquals(2, comments.size());
    }

    private User newUser(String email) {
        User user = new User();
        user.setName("Юзер");
        user.setEmail(email);
        return user;
    }

    private Item newItem(User owner, String name, String description) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setRequestId(null);
        return item;
    }

    private Comment newComment(User author, Item item, String text) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

}
