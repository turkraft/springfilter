package com.turkraft.springfilter.helper;

import java.lang.reflect.Field;

public interface FieldTypeResolver {

  Field getField(Class<?> klass, String path);

}
