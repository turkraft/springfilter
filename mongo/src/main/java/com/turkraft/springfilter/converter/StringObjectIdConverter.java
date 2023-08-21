package com.turkraft.springfilter.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkraft.springfilter.converter.StringObjectIdConverter.CustomObjectId;
import java.io.Serializable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class StringObjectIdConverter implements Converter<String, CustomObjectId> {

  @Override
  public CustomObjectId convert(String source) {
    return new CustomObjectId(source);
  }

  public static class CustomObjectId implements Serializable {

    @JsonProperty("$oid")
    private String id;

    public CustomObjectId(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

  }

}
