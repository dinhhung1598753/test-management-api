package com.demo.app.config.mapper;

import com.demo.app.model.Gender;
import com.demo.app.model.Question;
import com.demo.app.specification.QueryOperator;
import org.modelmapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        convertLocalDate(mapper);
        convertGender(mapper);
        convertLevel(mapper);
        convertBoolean(mapper);
        convertOperator(mapper);
        return mapper;
    }

    private void convertLocalDate(ModelMapper mapper) {
        var localDateProvider = new AbstractProvider<LocalDate>() {
            @Override
            protected LocalDate get() {
                return LocalDate.now();
            }
        };
        var converter = new AbstractConverter<String, LocalDate>() {
            @Override
            protected LocalDate convert(String source) {
                var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(source, formatter);
            }
        };
        mapper.createTypeMap(String.class, LocalDate.class);
        mapper.addConverter(converter);
        mapper.getTypeMap(String.class, LocalDate.class)
                .setProvider(localDateProvider);
    }

    private void convertGender(ModelMapper mapper) {
        mapper.createTypeMap(String.class, Gender.class).setConverter(
                context -> context.getSource() == null ? null :
                        switch (context.getSource().toUpperCase()) {
                            case "MALE" -> Gender.MALE;
                            case "FEMALE" -> Gender.FEMALE;
                            default -> null;
                        });
    }

    private void convertLevel(ModelMapper mapper) {
        mapper.createTypeMap(String.class, Question.Level.class)
                .setConverter(context -> context.getSource() == null ? null :
                        switch (context.getSource().toUpperCase()) {
                            case "EASY" -> Question.Level.EASY;
                            case "MEDIUM" -> Question.Level.MEDIUM;
                            case "HARD" -> Question.Level.HARD;
                            default -> null;
                        });
    }

    private void convertBoolean(ModelMapper mapper) {
        mapper.createTypeMap(String.class, Boolean.class)
                .setConverter(context -> context.getSource() == null ? null :
                        switch (context.getSource().toUpperCase()) {
                            case "TRUE", "1" -> true;
                            case "FALSE", "0" -> false;
                            default -> null;
                        });
        mapper.createTypeMap(Boolean.class, String.class)
                .setConverter(context -> context.getSource() == null
                        ? null
                        : context.getSource() ? "true" : "false");
    }

    private void convertOperator(ModelMapper mapper) {
        mapper.createTypeMap(String.class, QueryOperator.class)
                .setConverter(context -> context.getSource() == null ? null :
                        switch (context.getSource().toUpperCase()) {
                            case "EQUALS" -> QueryOperator.EQUALS;
                            case "IN" -> QueryOperator.IN;
                            case "LIKE" -> QueryOperator.LIKE;
                            case "GREATER_THAN" -> QueryOperator.GREATER_THAN;
                            default -> null;
                        });
    }
}
