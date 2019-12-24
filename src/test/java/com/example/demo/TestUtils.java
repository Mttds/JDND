package com.example.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.Object;

public class TestUtils {

	public static void injectObject(Object target, String fieldName, Object toInject) {

		boolean wasPrivate = false;
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			// isAccessible() method is deprecated: field.isAccessible() field.get(instance);
			// by default a field flag with isAccessible is false, so to use this method we
			// negate the checking condition
			if(! field.isAccessible()) {
				field.setAccessible(true);
				wasPrivate = true;
			}

			field.set(target, toInject);

			if(wasPrivate) {
				field.setAccessible(false);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
}