package com.fullrest.demoinflearnrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 어디에 붙일 수 있는 애노테이션인가
@Retention(RetentionPolicy.SOURCE) // 얼마나 오래 가져갈 것인가
public @interface TestDescription {
    String value();
}
