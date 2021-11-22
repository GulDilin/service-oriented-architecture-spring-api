package guldilin.controller;

import guldilin.dto.AbstractDTO;
import guldilin.dto.EntityListDTO;
import guldilin.entity.AbstractEntity;
import guldilin.errors.*;
import guldilin.repository.interfaces.CrudRepository;
import guldilin.utils.FilterAction;
import guldilin.utils.FilterActionParser;
import guldilin.utils.FilterableField;
import lombok.Data;
import org.hibernate.Session;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class CrudController<T extends AbstractEntity, D extends AbstractDTO> {
    private Class<T> entityClass;
    private Class<D> dtoClass;
    private CrudRepository<T> repository;
    private Supplier<List<FilterableField<?>>> getFields;
    private Function<D, T> mapToEntity;

    public CrudController(
            Class<T> entityClass,
            Class<D> dtoClass,
            CrudRepository<T> repository,
            Supplier<List<FilterableField<?>>> getFields,
            Function<D, T> mapToEntity) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.repository = repository;
        this.getFields = getFields;
        this.mapToEntity = mapToEntity;
    }

    private Predicate parseFilterAction(FilterAction action, CriteriaBuilder cb, Root<?> root) throws FilterTypeNotFound {
        FilterableField<?> field = action.getFilterableField();
        String fieldName = field.getName();
        System.out.println("Filter field " + fieldName + " by value " + action.getValue() + " mode " + action.getFilterType());
        switch (action.getFilterType()) {
            case EQUALS:
                return cb.equal(root.get(fieldName), field.getParser().apply(action.getValue()));
            case IS_NULL:
                return cb.isNull(root.get(fieldName));
            case CONTAINS:
                return cb.like(root.get(fieldName), "%" + field.getParser().apply(action.getValue()) + "%");
            case LESS_THAN:
                return cb.lessThan(root.get(fieldName), (Comparable) field.getTClass().cast(field.getParser().apply(action.getValue())));
            case GREATER_THAN:
                return cb.greaterThan(root.get(fieldName), (Comparable) field.getTClass().cast(field.getParser().apply(action.getValue())));
            case LESS_THAN_OR_EQUALS:
                return cb.lessThanOrEqualTo(root.get(fieldName), (Comparable) field.getTClass().cast(field.getParser().apply(action.getValue())));
            case GREATER_THAN_OR_EQUALS:
                return cb.greaterThanOrEqualTo(root.get(fieldName), (Comparable) field.getTClass().cast(field.getParser().apply(action.getValue())));
        }
        throw new FilterTypeNotFound();
    }

    void applyOrders(String[] orderings, CriteriaBuilder cb, CriteriaQuery<?> criteria, Root<?> root) {
        HashMap<String, Function<Expression<?>, Order>> orderingMap = new HashMap<>();
        Arrays.stream(orderings)
                .map(String::trim)
                .map(e -> !e.startsWith("-") && !e.startsWith("+") ? "+" + e : e)
                .forEach(e -> orderingMap.put(e.substring(1), e.startsWith("-") ? cb::desc : cb::asc));
        List<Order> orders = orderingMap
                .entrySet()
                .stream()
                .map(e -> {
                    try {
                        return e.getValue().apply(root.get(e.getKey()));
                    } catch (IllegalArgumentException ef) {
                        HashMap<String, String> errorsMap = new HashMap<>();
                        errorsMap.put(e.getKey(), ErrorMessage.SORTING_FIELD_ERROR);
                        ef.initCause(new ValidationException(errorsMap));
                        throw ef;
                    }
                })
                .collect(Collectors.toList());
        criteria.orderBy(orders);
    }

    void applyFilters(HttpServletRequest request, CriteriaBuilder cb, CriteriaQuery<?> criteria, Root<?> root)
            throws FilterTypeNotFound, FilterTypeNotSupported {
        List<FilterableField<?>> fields = getFields.get()
                .stream()
                .filter(e -> request.getParameter(e.getName()) != null)
                .collect(Collectors.toList());
        List<Predicate> predicates = new ArrayList<>();
        for (FilterableField<?> field : fields) {
            FilterAction action = FilterActionParser.parse(request.getParameter(field.getName()), field);
            predicates.add(parseFilterAction(action, cb, root));
        }
        criteria.where(cb.and(predicates.toArray(new Predicate[0])));
    }

    private Long getTotalCount(HttpServletRequest request) throws FilterTypeNotFound, FilterTypeNotSupported {
        Session session = repository.openSession();
        CriteriaBuilder cbCount = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaCount = cbCount.createQuery(Long.class);
        Root<T> rootCount = criteriaCount.from(entityClass);
        criteriaCount.select(cbCount.count(rootCount));

        applyFilters(request, cbCount, criteriaCount, rootCount);

        Query queryCount = session.createQuery(criteriaCount);
        Long total = (Long) queryCount.getSingleResult();
        session.close();
        return total;
    }

    public AbstractDTO getById(Integer id) throws IOException, EntryNotFound {
        return repository.findById(id)
                .orElseThrow(EntryNotFound::new)
                .mapToDTO();
    }

    public EntityListDTO getItems(Integer limit, Integer offset, String[] orderings, HttpServletRequest request)
            throws IOException, FilterTypeNotFound, FilterTypeNotSupported, EntryNotFound, ValidationException {
        limit = Optional.ofNullable(limit).orElse(10);
        offset = Optional.ofNullable(offset).orElse(0);
        if (limit < 0 || offset < 0) {
            HashMap<String, String> errors = new HashMap<>();
            if (limit < 0) errors.put("limit", ErrorMessage.MIN_0);
            if (offset < 0) errors.put("offset", ErrorMessage.MIN_0);
            throw new ValidationException(errors);
        }

        Long total = getTotalCount(request);

        Session session = repository.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(entityClass);
        Root<T> root = criteria.from(entityClass);
        criteria.select(root);

        if (orderings != null) applyOrders(orderings, cb, criteria, root);
        applyFilters(request, cb, criteria, root);

        Query query = session.createQuery(criteria);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<T> entries = query.getResultList();

        entries.forEach(System.out::println);
        EntityListDTO listDTO = new EntityListDTO();
        listDTO.setResults(entries.stream().map(T::mapToDTO).collect(Collectors.toList()));
        listDTO.setTotal(total);
        return listDTO;
    }

    public AbstractDTO createItem(D dto) throws IOException {
        T entry = mapToEntity.apply(dto);
        entry.setId(null);
        repository.save(entry);
        return entry.mapToDTO();
    }

     public AbstractDTO replaceItem(Integer id, D dto) throws IOException, EntryNotFound {
        T entry = mapToEntity.apply(dto);
        entry.setId(id);
        entry = repository.update(entry);
        return getById(entry.getId());
    }

    public void deleteItem(Integer id) throws EntryNotFound {
        repository.deleteById(id);
    }
}
