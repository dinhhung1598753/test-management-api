package com.demo.app.config.mapper;

import com.demo.app.model.Gender;
import com.demo.app.model.Question;
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
                context -> switch (context.getSource()) {
                    case "male", "Male", "MALE" -> Gender.MALE;
                    case "female", "Female", "FEMALE" -> Gender.FEMALE;
                    default -> null;
                });
    }

    private void convertLevel(ModelMapper mapper) {
        mapper.createTypeMap(String.class, Question.Level.class).setConverter(
                context -> switch (context.getSource()) {
                    case "easy", "Easy", "EASY" -> Question.Level.EASY;
                    case "medium", "Medium", "MEDIUM" -> Question.Level.MEDIUM;
                    case "hard", "Hard", "HARD" -> Question.Level.HARD;
                    default -> null;
                });
    }

    private void convertBoolean(ModelMapper mapper) {
        mapper.createTypeMap(String.class, Boolean.class)
                .setConverter(context -> switch (context.getSource()) {
                    case "True", "TRUE", "true", "1", "Correct" -> true;
                    case "False", "FALSE", "false", "0", "Incorrect" -> false;
                    default -> null;
                });
        mapper.createTypeMap(Boolean.class, String.class)
                .setConverter(context -> context.getSource() == null
                        ? null
                        : context.getSource() ? "true" : "false");
    }
}
