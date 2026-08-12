package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class JsonTextFunction extends FilterFunction {

  public JsonTextFunction() {
    super("jsonText");
  }

  @Override
  public String getDescription() {
    return "Extract string value from JSON/JSONB field by key path";
  }

  @Override
  public String getExample() {
    return "jsonText(metadata, 'address', 'city') : 'New York'";
  }

}
