package com.turkraft.springfilter.pagesort;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter(AntPathFilterMixin.FILTER)
public class AntPathFilterMixin {

  public static final String FILTER = "antPathFilter";

}
