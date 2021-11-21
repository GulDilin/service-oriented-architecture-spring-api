package guldilin.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Coordinates tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoordinatesTest {
    private SessionFactory sessionFactory;
    private Validator validator;

    @BeforeAll
    void setUp() {
        this.sessionFactory = SessionFactoryBuilder.getSessionFactory();

    }

    @Test
    void testConnect() {
        Session session = sessionFactory.openSession();
        session.createQuery("FROM coordinates ").list();
    }

    @Test
    void criteriaTest() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coordinates> query = cb.createQuery(Coordinates.class);
        Root<Coordinates> root = query.from(Coordinates.class);
        query.select(root);
        query.where(cb.lt(root.get("id"), 50));
        session.createQuery(query)
                .getResultList()
                .forEach(System.out::println);
        session.close();
    }
}
