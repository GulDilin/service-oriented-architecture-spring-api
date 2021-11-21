package guldilin.repository.implementation;

import guldilin.entity.City;
import guldilin.repository.interfaces.CrudRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CrudRepositoryImplementation {
    CrudRepository<City> repository;

    @BeforeAll
    void setUp() {
        this.repository = new CrudRepositoryImpl<>(City.class);
    }

    @Test
    void findAll() {
        CriteriaBuilder cb = repository.createEntityManager().getCriteriaBuilder();
        CriteriaQuery<City> cityCriteria = cb.createQuery(City.class);
        Root<City> cityRoot = cityCriteria.from(City.class);
        cityCriteria.select(cityRoot);
        this.repository.findByCriteria(cityCriteria)
                .forEach(System.out::println);
    }
}
