package com.turkraft.springfilter.helper;

import java.lang.reflect.Field;

public interface FieldTypeResolver {

  Class<?> resolve(Class<?> klass, String path);

  Field getField(Class<?> klass, String path);

}
