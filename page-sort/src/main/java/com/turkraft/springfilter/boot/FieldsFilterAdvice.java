package com.turkraft.springfilter.boot;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.turkraft.springfilter.pagesort.AntPathFilterMixin;
import com.turkraft.springfilter.pagesort.AntPathPropertyFilter;
import com.turkraft.springfilter.pagesort.FieldsExpression;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
public class FieldsFilterAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {

    if (returnType.getMethod() == null) {
      return false;
    }

    Fields fieldsAnnotation = returnType.getMethodAnnotation(Fields.class);
    if (fieldsAnnotation == null) {
      return false;
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return false;
    }

    HttpServletRequest request = attributes.getRequest();
    String parameterName = fieldsAnnotation.parameter();
    String fieldsValue = request.getParameter(parameterName);

    if (fieldsValue == null || fieldsValue.trim().isEmpty()) {
      String defaultValue = fieldsAnnotation.defaultValue();
      if (defaultValue != null && !defaultValue.isEmpty()) {
        return true;
      }
      if (fieldsAnnotation.required()) {
        return true;
      }
      return false;
    }

    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {

    if (returnType.getMethod() == null) {
      return body;
    }

    Fields fieldsAnnotation = returnType.getMethodAnnotation(Fields.class);
    if (fieldsAnnotation == null) {
      return body;
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return body;
    }

    HttpServletRequest servletRequest = attributes.getRequest();
    String parameterName = fieldsAnnotation.parameter();
    String fieldsValue = servletRequest.getParameter(parameterName);

    if (fieldsValue == null || fieldsValue.trim().isEmpty()) {
      if (fieldsAnnotation.required()) {
        throw new IllegalArgumentException(
            "Required fields parameter '" + parameterName + "' is missing");
      }

      String defaultValue = fieldsAnnotation.defaultValue();
      if (defaultValue != null && !defaultValue.isEmpty()) {
        fieldsValue = defaultValue;
      } else {
        return body;
      }
    }

    FieldsExpression fields = new FieldsExpression(fieldsValue);
    if (fields.isEmpty()) {
      return body;
    }

    MappingJacksonValue wrapper;
    if (body instanceof MappingJacksonValue) {
      wrapper = (MappingJacksonValue) body;
    } else {
      wrapper = new MappingJacksonValue(body);
    }

    FilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter(AntPathFilterMixin.FILTER, new AntPathPropertyFilter(fields))
        .setFailOnUnknownId(false);

    wrapper.setFilters(filterProvider);
    return wrapper;

  }

}
