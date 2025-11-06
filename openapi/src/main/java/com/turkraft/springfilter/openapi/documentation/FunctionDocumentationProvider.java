package com.turkraft.springfilter.openapi.documentation;

import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.definition.FilterFunctions;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FunctionDocumentationProvider {

  private final FilterFunctions filterFunctions;

  public FunctionDocumentationProvider(FilterFunctions filterFunctions) {
    this.filterFunctions = filterFunctions;
  }

  public List<FunctionDoc> getAllFunctionDocs() {
    List<FunctionDoc> docs = new ArrayList<>();
    for (FilterFunction function : filterFunctions.getFunctions()) {
      docs.add(createFunctionDoc(function));
    }
    return docs;
  }

  private FunctionDoc createFunctionDoc(FilterFunction function) {
    String name = function.getName();
    String description = function.getDescription();
    String example = function.getExample();
    return new FunctionDoc(name, description, example);
  }

  public static class FunctionDoc {

    private final String name;
    private final String description;
    private final String example;

    public FunctionDoc(String name, @Nullable String description, @Nullable String example) {
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
