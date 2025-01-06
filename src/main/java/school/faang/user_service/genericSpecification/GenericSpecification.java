//package com.faang.specification.genericSpecification;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Expression;
//import jakarta.persistence.criteria.From;
//import jakarta.persistence.criteria.Join;
//import jakarta.persistence.criteria.JoinType;
//import jakarta.persistence.criteria.Path;
//import jakarta.persistence.criteria.Predicate;
//import jakarta.persistence.criteria.Root;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RequiredArgsConstructor
//public class GenericSpecification<T> implements Specification<T> {
//
//    private final Class<T> rootClass;
//    private final FilterGroup rootGroup;
//
//    private final Map<String, Join<?, ?>> joinCache = new HashMap<>();
//
//    @Override
//    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//        return buildGroupPredicate(rootGroup, root, cb);
//    }
//
//    private Predicate buildGroupPredicate(FilterGroup group, From<?, ?> from, CriteriaBuilder cb) {
//        if (group == null) {
//            return cb.conjunction();
//        }
//
//        List<Predicate> filterPredicates = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(group.getFilters())) {
//            for (FilterRequest filter : group.getFilters()) {
//                Predicate p = buildFilterPredicate(filter, from, cb);
//                if (p != null) {
//                    filterPredicates.add(p);
//                }
//            }
//        }
//
//        List<Predicate> groupPredicates = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(group.getChildren())) {
//            for (FilterGroup childGroup : group.getChildren()) {
//                Predicate childPredicate = buildGroupPredicate(childGroup, from, cb);
//                if (childPredicate != null) {
//                    groupPredicates.add(childPredicate);
//                }
//            }
//        }
//
//        List<Predicate> allPredicates = new ArrayList<>();
//        allPredicates.addAll(filterPredicates);
//        allPredicates.addAll(groupPredicates);
//
//        if (allPredicates.isEmpty()) {
//            return cb.conjunction();
//        }
//
//        String operator = group.getGroupOperator() == null ? "AND" : group.getGroupOperator().toUpperCase();
//        return switch (operator) {
//            case "OR" -> cb.or(allPredicates.toArray(new Predicate[0]));
//            default -> cb.and(allPredicates.toArray(new Predicate[0]));
//        };
//    }
//
//    private Predicate buildFilterPredicate(FilterRequest filter, From<?, ?> from, CriteriaBuilder cb) {
//        if (!StringUtils.hasText(filter.getField()) || filter.getOperation() == null) {
//            return null;
//        }
//        Path<?> path = resolvePathAndJoin(from, filter);
//
//        Class<?> fieldType = getFieldType(rootClass, filter.getField());
//        if (fieldType == null) {
//            return null;
//        }
//        Object typedValue = convertValue(filter.getValue(), fieldType);
//        Object typedValueTo = convertValue(filter.getValueTo(), fieldType);
//
//        return buildPredicate(cb, path, filter.getOperation(), typedValue, typedValueTo);
//    }
//
//    private Class<?> getFieldType(Class<?> startClass, String dotPath) {
//        try {
//            Class<?> currentClass = startClass;
//            String[] parts = dotPath.split("\\.");
//            for (int i = 0; i < parts.length; i++) {
//                String fieldName = parts[i];
//                Field field = getDeclaredField(currentClass, fieldName);
//
//                if (field == null) {
//                    return null;
//                }
//                if (i < parts.length - 1) {
//                    if (Collection.class.isAssignableFrom(field.getType())) {
//                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
//                        currentClass = (Class<?>) pt.getActualTypeArguments()[0];
//                    } else {
//                        currentClass = field.getType();
//                    }
//                } else {
//                    return field.getType();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return null;
//    }
//
//    private Field getDeclaredField(Class<?> clazz, String fieldName) {
//        while (clazz != null && clazz != Object.class) {
//            try {
//                return clazz.getDeclaredField(fieldName);
//            } catch (NoSuchFieldException e) {
//                clazz = clazz.getSuperclass();
//            }
//        }
//        return null;
//    }
//
//    private Object convertValue(Object rawValue, Class<?> targetType) {
//        if (rawValue == null) {
//            return null;
//        }
//        if (targetType.isAssignableFrom(rawValue.getClass())) {
//            return rawValue;
//        }
//        String stringValue = rawValue.toString();
//        if (Number.class.isAssignableFrom(targetType)) {
//            if (targetType.equals(Integer.class)) {
//                return Integer.valueOf(stringValue);
//            } else if (targetType.equals(Long.class)) {
//                return Long.valueOf(stringValue);
//            } else if (targetType.equals(BigDecimal.class)) {
//                return new BigDecimal(stringValue);
//            }
//        } else if (targetType.equals(LocalDate.class)) {
//            return LocalDate.parse(stringValue);
//        } else if (Enum.class.isAssignableFrom(targetType)) {
//            @SuppressWarnings({"unchecked", "rawtypes"})
//            Class<? extends Enum> enumClass = (Class<? extends Enum>) targetType;
//            return Enum.valueOf(enumClass, stringValue);
//        }
//        return stringValue;
//    }
//
//    private Path<?> resolvePathAndJoin(From<?, ?> from, FilterRequest filter) {
//        String[] parts = filter.getField().split("\\.");
//        From<?, ?> current = from;
//        for (int i = 0; i < parts.length - 1; i++) {
//            current = getOrCreateJoin(current, parts[i], filter.getJoinType());
//        }
//        return current.get(parts[parts.length - 1]);
//    }
//
//    private From<?, ?> getOrCreateJoin(From<?, ?> from, String attribute, String joinType) {
//        String key = from.getJavaType().getName() + "." + attribute;
//        if (joinCache.containsKey(key)) {
//            return joinCache.get(key);
//        }
//        JoinType jt = "LEFT".equalsIgnoreCase(joinType) ? JoinType.LEFT : JoinType.INNER;
//        Join<?, ?> join = from.join(attribute, jt);
//        joinCache.put(key, join);
//        return join;
//    }
//
//    private Predicate buildPredicate(CriteriaBuilder cb, Path<?> path,
//                                     FilterOperation op, Object value, Object valueTo) {
//        return switch (op) {
//            case EQUAL -> cb.equal(path, value);
//            case NOT_EQUAL -> cb.notEqual(path, value);
//
//            case MATCH -> {
//                Expression<String> exp = cb.lower(path.as(String.class));
//                yield cb.like(exp, "%" + toLower(value) + "%");
//            }
//            case MATCH_START -> {
//                Expression<String> exp = cb.lower(path.as(String.class));
//                yield cb.like(exp, toLower(value) + "%");
//            }
//            case MATCH_END -> {
//                Expression<String> exp = cb.lower(path.as(String.class));
//                yield cb.like(exp, "%" + toLower(value));
//            }
//
//            case GREATER_THAN -> cb.greaterThan(path.as(Comparable.class), (Comparable) value);
//            case GREATER_THAN_EQUAL -> cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
//            case LESS_THAN -> cb.lessThan(path.as(Comparable.class), (Comparable) value);
//            case LESS_THAN_EQUAL -> cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
//
//            case BETWEEN -> {
//                Expression<? extends Comparable> cPath = path.as(Comparable.class);
//                yield cb.between(cPath, (Comparable) value, (Comparable) valueTo);
//            }
//
//            case IN -> {
//                if (value instanceof Collection<?> col) {
//                    yield cPathIn(cb, path, col);
//                } else {
//                    CriteriaBuilder.In<Object> inClause = cb.in(path);
//                    inClause.value(value);
//                    yield inClause; // make sure it's seen as a Predicate
//                }
//            }
//
//            case NOT_IN -> {
//                if (value instanceof Collection<?> col) {
//                    // cPathIn(...) already returns a Predicate
//                    yield cb.not(cPathIn(cb, path, col));
//                } else {
//                    // If it's a single value, construct the 'in' clause explicitly
//                    CriteriaBuilder.In<Object> inClause = cb.in(path);
//                    inClause.value(value);
//                    // Now cast it to Predicate and apply cb.not(...)
//                    yield cb.not(inClause);
//                }
//            }
//
//            case IS_NULL -> cb.isNull(path);
//            case IS_NOT_NULL -> cb.isNotNull(path);
//        };
//    }
//
//    private String toLower(Object val) {
//        return val == null ? "" : val.toString().toLowerCase();
//    }
//
//    @SuppressWarnings("unchecked")
//    private Predicate cPathIn(CriteriaBuilder cb, Path<?> path, Collection<?> col) {
//        CriteriaBuilder.In<Object> inClause = cb.in(path);
//        col.forEach(inClause::value);
//        return inClause;
//    }
//
//}

package school.faang.user_service.genericSpecification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.request.FilterGroupRequest;
import school.faang.user_service.dto.request.FilterRequest;
import school.faang.user_service.dto.request.SortRequest;
import school.faang.user_service.enums.FilterOperation;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GenericSpecification<T> implements Specification<T> {

    private final Class<T> rootClass;
    private final FilterGroupRequest rootGroup;
    private final SortRequest sortRequest;

    // Cache to avoid re-joining the same attribute multiple times
    private final Map<String, Join<?, ?>> joinCache = new HashMap<>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate basePredicate = buildGroupPredicate(rootGroup, root, cb);
        if (sortRequest != null && sortRequest.getKey() != null) {
            Order order = sortRequest.getDirection().build(root, cb, sortRequest);
            query.orderBy(order);
        }
        return basePredicate;
    }

    /**
     * Recursively builds a Predicate from a FilterGroup (which may contain child groups).
     */
    private Predicate buildGroupPredicate(FilterGroupRequest group, From<?, ?> from, CriteriaBuilder cb) {
        if (group == null) {
            // No filters => return "true" so it won't exclude anything
            return cb.conjunction();
        }

        // Build predicates for each filter in this group
        List<Predicate> filterPredicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(group.getFilters())) {
            for (FilterRequest filter : group.getFilters()) {
                Predicate p = buildFilterPredicate(filter, from, cb);
                if (p != null) {
                    filterPredicates.add(p);
                }
            }
        }

        // Build predicates for each child group in this group
        List<Predicate> groupPredicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(group.getChildren())) {
            for (FilterGroupRequest childGroup : group.getChildren()) {
                Predicate childPredicate = buildGroupPredicate(childGroup, from, cb);
                if (childPredicate != null) {
                    groupPredicates.add(childPredicate);
                }
            }
        }

        // Combine all predicates in this group
        List<Predicate> allPredicates = new ArrayList<>(filterPredicates);
        allPredicates.addAll(groupPredicates);

        if (allPredicates.isEmpty()) {
            return cb.conjunction();
        }

        // The groupOperator can be "AND" or "OR" (default to "AND")
        String operator = (group.getGroupOperator() == null)
                ? "AND"
                : group.getGroupOperator().toUpperCase();

        switch (operator) {
            case "OR":
                return cb.or(allPredicates.toArray(new Predicate[0]));
            default:
                // "AND"
                return cb.and(allPredicates.toArray(new Predicate[0]));
        }
    }

    /**
     * Build a single Predicate from a FilterRequest.
     */
    private Predicate buildFilterPredicate(FilterRequest filter, From<?, ?> from, CriteriaBuilder cb) {
        if (!StringUtils.hasText(filter.getField()) || filter.getOperation() == null) {
            return null;
        }
        // 1) Navigate dot-notation and create/reuse joins to get the final Path
        Path<?> path = resolvePathAndJoin(from, filter);

        // 2) Determine the Java type of this field via reflection
        Class<?> fieldType = getFieldType(rootClass, filter.getField());
        if (fieldType == null) {
            return null; // or throw an exception
        }

        // 3) Convert the raw value(s) into the correct Java type
        Object typedValue = convertValue(filter.getValue(), fieldType);
        Object typedValueTo = convertValue(filter.getValueTo(), fieldType);

        // 4) Build the appropriate predicate
        return buildPredicate(cb, path, fieldType, filter.getOperation(), typedValue, typedValueTo);
    }

    /**
     * Recursively parse "dotPath" to find or create the correct join and return the final Path.
     */
    private Path<?> resolvePathAndJoin(From<?, ?> from, FilterRequest filter) {
        String[] parts = filter.getField().split("\\.");
        From<?, ?> current = from;
        for (int i = 0; i < parts.length - 1; i++) {
            current = getOrCreateJoin(current, parts[i], filter.getJoinType());
        }
        return current.get(parts[parts.length - 1]);
    }

    /**
     * Create or reuse a join on a relationship field (INNER or LEFT).
     */
    private From<?, ?> getOrCreateJoin(From<?, ?> from, String attribute, String joinType) {
        String key = from.getJavaType().getName() + "." + attribute;
        if (joinCache.containsKey(key)) {
            return joinCache.get(key);
        }
        JoinType jt = "LEFT".equalsIgnoreCase(joinType) ? JoinType.LEFT : JoinType.INNER;
        Join<?, ?> join = from.join(attribute, jt);
        joinCache.put(key, join);
        return join;
    }

    /**
     * Using reflection, find the Java type for the given dot-path (e.g. "cars.engine.horsepower" => Integer.class).
     */
    private Class<?> getFieldType(Class<?> startClass, String dotPath) {
        try {
            Class<?> currentClass = startClass;
            String[] parts = dotPath.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                String fieldName = parts[i];
                java.lang.reflect.Field field = getDeclaredField(currentClass, fieldName);

                if (field == null) {
                    return null;
                }

                if (i < parts.length - 1) {
                    // move to next class in chain
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        // e.g. Set<Car> => Car
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        currentClass = (Class<?>) pt.getActualTypeArguments()[0];
                    } else {
                        currentClass = field.getType();
                    }
                } else {
                    // last part => final field
                    return field.getType();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private java.lang.reflect.Field getDeclaredField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Convert user-provided value (often a String) into the actual Java type for the field.
     */
    private Object convertValue(Object rawValue, Class<?> targetType) {
        if (rawValue == null) {
            return null;
        }
        // If it's already the correct type, return it
        if (targetType.isAssignableFrom(rawValue.getClass())) {
            return rawValue;
        }

        String stringValue = rawValue.toString();
        // Numeric conversions
        if (Number.class.isAssignableFrom(targetType)) {
            if (targetType.equals(Integer.class)) {
                return Integer.valueOf(stringValue);
            } else if (targetType.equals(Long.class)) {
                return Long.valueOf(stringValue);
            } else if (targetType.equals(BigDecimal.class)) {
                return new BigDecimal(stringValue);
            }
        }
        // Date conversions
        else if (targetType.equals(LocalDate.class)) {
            return LocalDate.parse(stringValue);
        }
        // Enum conversions
        else if (Enum.class.isAssignableFrom(targetType)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends Enum> enumClass = (Class<? extends Enum>) targetType;
            return Enum.valueOf(enumClass, stringValue);
        }

        // Default fallback => treat as String
        return stringValue;
    }

    /**
     * Build the final Predicate for the given operation (EQUAL, GREATER_THAN, etc.).
     * We specifically handle numeric/date comparisons with typed helper methods.
     */
    private Predicate buildPredicate(CriteriaBuilder cb,
                                     Path<?> path,
                                     Class<?> fieldType,
                                     FilterOperation op,
                                     Object value,
                                     Object valueTo) {
        switch (op) {
            case EQUAL:
                return cb.equal(path, value);

            case NOT_EQUAL:
                return cb.notEqual(path, value);

            case MATCH: {
                Expression<String> exp = cb.lower(path.as(String.class));
                return cb.like(exp, "%" + toLower(value) + "%");
            }
            case MATCH_START: {
                Expression<String> exp = cb.lower(path.as(String.class));
                return cb.like(exp, toLower(value) + "%");
            }
            case MATCH_END: {
                Expression<String> exp = cb.lower(path.as(String.class));
                return cb.like(exp, "%" + toLower(value));
            }

            case GREATER_THAN:
                return buildGreaterThan(cb, path, fieldType, value);

            case GREATER_THAN_EQUAL:
                return buildGreaterThanOrEqual(cb, path, fieldType, value);

            case LESS_THAN:
                return buildLessThan(cb, path, fieldType, value);

            case LESS_THAN_EQUAL:
                return buildLessThanOrEqual(cb, path, fieldType, value);

            case BETWEEN:
                return buildBetween(cb, path, fieldType, value, valueTo);

            case IN: {
                if (value instanceof Collection<?> col) {
                    return cPathIn(cb, path, col);
                } else {
                    CriteriaBuilder.In<Object> inClause = cb.in(path);
                    inClause.value(value);
                    return inClause; // "in" with a single value
                }
            }
            case NOT_IN: {
                if (value instanceof Collection<?> col) {
                    return cb.not(cPathIn(cb, path, col));
                } else {
                    CriteriaBuilder.In<Object> inClause = cb.in(path);
                    inClause.value(value);
                    return cb.not(inClause);
                }
            }

            case IS_NULL:
                return cb.isNull(path);

            case IS_NOT_NULL:
                return cb.isNotNull(path);
        }
        // If we somehow get an unknown operation:
        return null;
    }

    /**
     * Convert the given object to lower-case string (for MATCH ops).
     */
    private String toLower(Object val) {
        return (val == null) ? "" : val.toString().toLowerCase();
    }

    /**
     * Helper for "IN ( ... )" with a collection of values.
     */
    @SuppressWarnings("unchecked")
    private Predicate cPathIn(CriteriaBuilder cb, Path<?> path, Collection<?> col) {
        CriteriaBuilder.In<Object> inClause = cb.in(path);
        col.forEach(inClause::value);
        return inClause;
    }

    /* ====================
       Helper methods to handle numeric/date comparisons
       (avoiding raw Comparable.class to satisfy Hibernate 6).
       ==================== */

    private Predicate buildGreaterThan(CriteriaBuilder cb, Path<?> path,
                                       Class<?> fieldType, Object value) {
        if (fieldType.equals(Integer.class)) {
            Expression<Integer> intPath = path.as(Integer.class);
            return cb.greaterThan(intPath, (Integer) value);
        } else if (fieldType.equals(Long.class)) {
            Expression<Long> longPath = path.as(Long.class);
            return cb.greaterThan(longPath, (Long) value);
        } else if (fieldType.equals(BigDecimal.class)) {
            Expression<BigDecimal> bdPath = path.as(BigDecimal.class);
            return cb.greaterThan(bdPath, (BigDecimal) value);
        } else if (fieldType.equals(LocalDate.class)) {
            Expression<LocalDate> datePath = path.as(LocalDate.class);
            return cb.greaterThan(datePath, (LocalDate) value);
        }
        throw new UnsupportedOperationException("GREATER_THAN not supported for type: " + fieldType);
    }

    private Predicate buildGreaterThanOrEqual(CriteriaBuilder cb, Path<?> path,
                                              Class<?> fieldType, Object value) {
        if (fieldType.equals(Integer.class)) {
            Expression<Integer> intPath = path.as(Integer.class);
            return cb.greaterThanOrEqualTo(intPath, (Integer) value);
        } else if (fieldType.equals(Long.class)) {
            Expression<Long> longPath = path.as(Long.class);
            return cb.greaterThanOrEqualTo(longPath, (Long) value);
        } else if (fieldType.equals(BigDecimal.class)) {
            Expression<BigDecimal> bdPath = path.as(BigDecimal.class);
            return cb.greaterThanOrEqualTo(bdPath, (BigDecimal) value);
        } else if (fieldType.equals(LocalDate.class)) {
            Expression<LocalDate> datePath = path.as(LocalDate.class);
            return cb.greaterThanOrEqualTo(datePath, (LocalDate) value);
        }
        throw new UnsupportedOperationException("GREATER_THAN_EQUAL not supported for type: " + fieldType);
    }

    private Predicate buildLessThan(CriteriaBuilder cb, Path<?> path,
                                    Class<?> fieldType, Object value) {
        if (fieldType.equals(Integer.class)) {
            Expression<Integer> intPath = path.as(Integer.class);
            return cb.lessThan(intPath, (Integer) value);
        } else if (fieldType.equals(Long.class)) {
            Expression<Long> longPath = path.as(Long.class);
            return cb.lessThan(longPath, (Long) value);
        } else if (fieldType.equals(BigDecimal.class)) {
            Expression<BigDecimal> bdPath = path.as(BigDecimal.class);
            return cb.lessThan(bdPath, (BigDecimal) value);
        } else if (fieldType.equals(LocalDate.class)) {
            Expression<LocalDate> datePath = path.as(LocalDate.class);
            return cb.lessThan(datePath, (LocalDate) value);
        }
        throw new UnsupportedOperationException("LESS_THAN not supported for type: " + fieldType);
    }

    private Predicate buildLessThanOrEqual(CriteriaBuilder cb, Path<?> path,
                                           Class<?> fieldType, Object value) {
        if (fieldType.equals(Integer.class)) {
            Expression<Integer> intPath = path.as(Integer.class);
            return cb.lessThanOrEqualTo(intPath, (Integer) value);
        } else if (fieldType.equals(Long.class)) {
            Expression<Long> longPath = path.as(Long.class);
            return cb.lessThanOrEqualTo(longPath, (Long) value);
        } else if (fieldType.equals(BigDecimal.class)) {
            Expression<BigDecimal> bdPath = path.as(BigDecimal.class);
            return cb.lessThanOrEqualTo(bdPath, (BigDecimal) value);
        } else if (fieldType.equals(LocalDate.class)) {
            Expression<LocalDate> datePath = path.as(LocalDate.class);
            return cb.lessThanOrEqualTo(datePath, (LocalDate) value);
        }
        throw new UnsupportedOperationException("LESS_THAN_EQUAL not supported for type: " + fieldType);
    }

    private Predicate buildBetween(CriteriaBuilder cb, Path<?> path,
                                   Class<?> fieldType,
                                   Object value1,
                                   Object value2) {
        if (value1 == null || value2 == null) {
            // You might return null or throw an exception for incomplete "BETWEEN"
            return null;
        }

        if (fieldType.equals(Integer.class)) {
            Expression<Integer> intPath = path.as(Integer.class);
            return cb.between(intPath, (Integer) value1, (Integer) value2);
        } else if (fieldType.equals(Long.class)) {
            Expression<Long> longPath = path.as(Long.class);
            return cb.between(longPath, (Long) value1, (Long) value2);
        } else if (fieldType.equals(BigDecimal.class)) {
            Expression<BigDecimal> bdPath = path.as(BigDecimal.class);
            return cb.between(bdPath, (BigDecimal) value1, (BigDecimal) value2);
        } else if (fieldType.equals(LocalDate.class)) {
            Expression<LocalDate> datePath = path.as(LocalDate.class);
            return cb.between(datePath, (LocalDate) value1, (LocalDate) value2);
        }
        throw new UnsupportedOperationException("BETWEEN not supported for type: " + fieldType);
    }

}
