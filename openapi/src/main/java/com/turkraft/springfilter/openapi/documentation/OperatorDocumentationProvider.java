package com.turkraft.springfilter.openapi.documentation;

import com.turkraft.springfilter.definition.FilterInfixOperator;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.definition.FilterPostfixOperator;
import com.turkraft.springfilter.definition.FilterPrefixOperator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class OperatorDocumentationProvider {

  private final FilterOperators filterOperators;

  public OperatorDocumentationProvider(FilterOperators filterOperators) {
    this.filterOperators = filterOperators;
  }

  public List<OperatorDoc> getAllOperatorDocs() {
    List<OperatorDoc> docs = new ArrayList<>();
    for (FilterInfixOperator operator : filterOperators.getInfixOperators()) {
      docs.add(createInfixOperatorDoc(operator));
    }
    for (FilterPrefixOperator operator : filterOperators.getPrefixOperators()) {
      docs.add(createPrefixOperatorDoc(operator));
    }
    for (FilterPostfixOperator operator : filterOperators.getPostfixOperators()) {
      docs.add(createPostfixOperatorDoc(operator));
    }
    return docs;
  }

  private OperatorDoc createInfixOperatorDoc(FilterInfixOperator operator) {
    String[] tokens = operator.getTokens();
    String description = operator.getDescription();
    String example = operator.getExample();
    return new OperatorDoc(tokens, description, example, operator.getPriority(), OperatorType.INFIX);
  }

  private OperatorDoc createPrefixOperatorDoc(FilterPrefixOperator operator) {
    String[] tokens = operator.getTokens();
    String description = operator.getDescription();
    String example = operator.getExample();
    return new OperatorDoc(tokens, description, example, operator.getPriority(), OperatorType.PREFIX);
  }

  private OperatorDoc createPostfixOperatorDoc(FilterPostfixOperator operator) {
    String[] tokens = operator.getTokens();
    String description = operator.getDescription();
    String example = operator.getExample();
    return new OperatorDoc(tokens, description, example, operator.getPriority(), OperatorType.POSTFIX);
  }

  public static class OperatorDoc {

    private final String[] tokens;
    private final String description;
    private final String example;
    private final int priority;
    private final OperatorType type;

    public OperatorDoc(String[] tokens, @Nullable String description, @Nullable String example,
        int priority, OperatorType type) {
      this.tokens = tokens;
      this.description = description;
      this.example = example;
      this.priority = priority;
      this.type = type;
    }

    public String[] getTokens() {
      return tokens;
    }

    @Nullable
    public String getDescription() {
      return description;
    }

    @Nullable
    public String getExample() {
      return example;
    }

    public int getPriority() {
      return priority;
    }

    public OperatorType getType() {
      return type;
    }
  }

  public enum OperatorType {
    INFIX, PREFIX, POSTFIX
  }

}
