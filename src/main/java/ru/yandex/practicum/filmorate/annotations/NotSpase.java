package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotSpaseValidator.class})
public @interface NotSpase {
    String message() default "must not contains spase";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
