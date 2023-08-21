package com.turkraft.springfilter.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import java.io.Serializable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class StringCustomObjectIdConverter implements Converter<String, CustomObjectId> {

  @Override
  public CustomObjectId convert(String source) {
    return new CustomObjectId(source);
  }

  public static class CustomObjectId implements Serializable {

    @JsonProperty("$oid")
    private String value;

    public CustomObjectId(String id) {
      this.value = id;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

  }

}
