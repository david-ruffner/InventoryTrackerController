package com.davidruffner.inventorytrackercontroller.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
//@Repeatable(OneOfRepeat.class)
public @interface OneOf {
    String group();
}