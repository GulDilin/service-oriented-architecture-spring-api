package guldilin.repository.interfaces;

import guldilin.entity.Mappable;
import guldilin.errors.EntryNotFound;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T extends Mappable> {
    List<T> findByCriteria(CriteriaQuery<T> criteriaQuery);

    Optional<T> findById(Integer id);

    T update(T entry) throws EntryNotFound;

    void save(T entry);

    void deleteById(Integer id) throws EntryNotFound;

    EntityManager createEntityManager();

    Session openSession();
}
