package guldilin.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("City tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CityTest {
    private SessionFactory sessionFactory;
    private Validator validator;

    @BeforeAll
    void setUp() {
        this.sessionFactory = SessionFactoryBuilder.getSessionFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    void testConnect() {
        Session session = sessionFactory.openSession();
        session.createQuery("FROM city").list();
    }

    @Test
    void criteriaTest() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<City> cityCriteria = cb.createQuery(City.class);
        Root<City> cityRoot = cityCriteria.from(City.class);
        cityCriteria.select(cityRoot);
        cityCriteria.where(cb.equal(cityRoot.get("name"), "Super"));
        session.createQuery(cityCriteria)
                .getResultList()
                .forEach(System.out::println);
        session.close();
    }

    @Test
    void save() {

        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        Coordinates coordinates = Coordinates.builder()
                .x(0L)
                .y(0)
                .build();
        em.persist(coordinates);
        em.flush();
        City city = City.builder()
                .climate(Climate.MONSOON)
                .coordinates(coordinates)
                .name("Duper")
                .area(-1)
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Set<ConstraintViolation<City>> constraintViolations = validator.validate( city );
        constraintViolations.stream()
                .map( c -> c.getPropertyPath().toString() + " " + c.getMessage())
                .forEach(System.out::println);
        assertThrows(ValidationException.class, () -> em.persist(city));
    }

    @Test
    void saveOk() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Coordinates coordinates = Coordinates.builder()
                .x(0L)
                .y(0)
                .build();
        session.save(coordinates);
        System.out.println(coordinates.toString());

        City city = City.builder()
                .climate(Climate.MONSOON)
                .coordinates(coordinates)
                .name("Duper")
                .area(2)
                .population(2)
                .build();
        session.save(city);
        transaction.commit();
        System.out.println(city.toString());

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<City> cityCriteria = cb.createQuery(City.class);
        Root<City> cityRoot = cityCriteria.from(City.class);
        cityCriteria.select(cityRoot);
        session.createQuery(cityCriteria)
                .getResultList()
                .forEach(System.out::println);
        session.close();
    }

    @Test
    void delete() {
        Session session = sessionFactory.openSession();

        session.getTransaction().begin();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<City> cityCriteria = cb.createQuery(City.class);
        Root<City> cityRoot = cityCriteria.from(City.class);
        cityCriteria.select(cityRoot);
        cityCriteria.where(cb.equal(cityRoot.get("name"), "Puper"));
        session.createQuery(cityCriteria)
                .getResultList()
                .forEach(session::remove);
        session.getTransaction().commit();
        assertEquals(session.createQuery(cityCriteria).getResultList().size(), 0);
        session.close();

    }
}
