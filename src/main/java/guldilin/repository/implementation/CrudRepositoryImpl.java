package guldilin.repository.implementation;

import guldilin.entity.AbstractEntity;
import guldilin.entity.SessionFactoryBuilder;
import guldilin.errors.EntryNotFound;
import guldilin.repository.interfaces.CrudRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public class CrudRepositoryImpl<T extends AbstractEntity> implements CrudRepository<T> {
    private final SessionFactory sessionFactory;
    private final Class<T> tClass;

    public CrudRepositoryImpl(Class<T> tClass) {
        this.sessionFactory = SessionFactoryBuilder.getSessionFactory();
        this.tClass = tClass;
    }

    @Override
    public List<T> findByCriteria(CriteriaQuery<T> criteriaQuery) {
        EntityManager em = sessionFactory.createEntityManager();
        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Optional<T> findById(Integer id) {
        EntityManager em = sessionFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(tClass);
        Root<T> root = query.from(tClass);
        query.select(root);
        query.where(cb.equal(root.get("id"), id));
        try {
            return Optional.ofNullable(em.createQuery(query).getSingleResult());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public T update(@Valid T entry) throws EntryNotFound {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        this.findById(entry.getId()).orElseThrow(EntryNotFound::new);
        em.merge(entry);
        em.getTransaction().commit();
        return entry;
    }

    @Override
    public void save(@Valid T entry) {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(entry);
        em.getTransaction().commit();
    }

    @Override
    public void deleteById(Integer id) throws EntryNotFound {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.remove(this.findById(id).orElseThrow(EntryNotFound::new));
        em.getTransaction().commit();
    }

    @Override
    public EntityManager createEntityManager() {
        return sessionFactory.createEntityManager();
    }

    @Override
    public Session openSession() {
        return sessionFactory.openSession();
    }

}
