package br.com.les.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

	/** 
	 * (Optional) Define the name to be used in queries for this class,
	 * if is empty use the class name.
	 */
	String value() default "";
}
