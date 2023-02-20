package com.turkraft.springfilter.definition;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
class FilterPlaceholdersImpl implements FilterPlaceholders {

  private List<FilterPlaceholder> placeholders;

  public FilterPlaceholdersImpl(List<FilterPlaceholder> placeholders) {
    this.placeholders = placeholders;
  }

  @Override
  public List<FilterPlaceholder> getPlaceholders() {
    return placeholders;
  }

  @Override
  public void setPlaceholders(List<FilterPlaceholder> placeholders) {
    this.placeholders = placeholders;
  }

  @Override
  public FilterPlaceholder getPlaceholder(String name) {
    for (FilterPlaceholder placeholder : placeholders) {
      if (placeholder.getName().equals(name)) {
        return placeholder;
      }
    }
    throw new UnsupportedOperationException("Unrecognized placeholder `" + name + "`");
  }

}
