package com.turkraft.springfilter.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import java.io.Serializable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class StringCustomUUIDConverter implements Converter<String, CustomUUID> {

  @Override
  public CustomUUID convert(String source) {
    return new CustomUUID(source);
  }

  public static class CustomUUID implements Serializable {

    @JsonProperty("$uuid")
    private String value;

    public CustomUUID(String id) {
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
