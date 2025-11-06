package com.turkraft.springfilter.openapi.documentation;

import com.turkraft.springfilter.definition.FilterPlaceholder;
import com.turkraft.springfilter.definition.FilterPlaceholders;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderDocumentationProvider {

  private final FilterPlaceholders filterPlaceholders;

  public PlaceholderDocumentationProvider(FilterPlaceholders filterPlaceholders) {
    this.filterPlaceholders = filterPlaceholders;
  }

  public List<PlaceholderDoc> getAllPlaceholderDocs() {
    List<PlaceholderDoc> docs = new ArrayList<>();
    for (FilterPlaceholder placeholder : filterPlaceholders.getPlaceholders()) {
      docs.add(createPlaceholderDoc(placeholder));
    }
    return docs;
  }

  private PlaceholderDoc createPlaceholderDoc(FilterPlaceholder placeholder) {
    String name = placeholder.getName();
    String description = placeholder.getDescription();
    String example = placeholder.getExample();
    return new PlaceholderDoc(name, description, example);
  }

  public static class PlaceholderDoc {

    private final String name;
    private final String description;
    private final String example;

    public PlaceholderDoc(String name, @Nullable String description, @Nullable String example) {
      this.name = name;
      this.description = description;
      this.example = example;
    }

    public String getName() {
      return name;
    }

    @Nullable
    public String getDescription() {
      return description;
    }

    @Nullable
    public String getExample() {
      return example;
    }

  }

}
