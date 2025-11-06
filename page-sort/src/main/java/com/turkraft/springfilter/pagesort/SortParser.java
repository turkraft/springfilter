package com.turkraft.springfilter.pagesort;

public interface SortParser {

  SortExpression parse(String expression) throws SortParseException;

  SortExpression parse(String expression, int maxFields) throws SortParseException;

}
