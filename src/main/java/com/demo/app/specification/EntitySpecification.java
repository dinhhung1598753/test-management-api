package com.demo.app.specification;

import com.demo.app.exception.OperationNotSupportedException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import static org.springframework.data.jpa.domain.Specification.where;

public final class EntitySpecification<T> {

    public Specification<T> withFilters(SearchFilter filter) {
        return where(createSpecification(filter));
    }

    private Specification<T> createSpecification(SearchFilter filter) {
        switch (filter.getOperator()) {
            case EQUALS -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(filter.getField()),
                                castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            }
            case IN -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.in(root.get(filter.getField()))
                                .value(castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            }
            case LIKE -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get(filter.getField()), "%" + filter.getValue() + "%");
            }
            case GREATER_THAN -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.gt(root.get(filter.getField()),
                                (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            }
            case LESS_THAN -> {
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lt(root.get(filter.getField()),
                                (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
            }
            default ->
                    throw new OperationNotSupportedException("This operation not supported !", HttpStatus.BAD_REQUEST);
        }

    }

    private static <T> Object castToRequiredType(Class<T> fieldType, String value) {
        if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (fieldType.isAssignableFrom(String.class)) {
            return String.valueOf(value);
        }
        return null;
    }
}

