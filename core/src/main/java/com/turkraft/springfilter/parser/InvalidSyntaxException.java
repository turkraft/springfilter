package com.turkraft.springfilter.parser;

import java.io.Serial;
import org.springframework.lang.Nullable;

public class InvalidSyntaxException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final int line;

  private final int charPositionInLine;

  @Nullable
  private final Object offendingSymbol;

  @Nullable
  private String input;

  @Deprecated
  public InvalidSyntaxException(String message) {
    this(message, 0, 0, null, null);
  }

  public InvalidSyntaxException(String message, int line, int charPositionInLine,
      @Nullable Object offendingSymbol, @Nullable Throwable cause) {
    super(message, cause);
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.offendingSymbol = offendingSymbol;
  }

  public int getLine() {
    return line;
  }

  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Nullable
  public Object getOffendingSymbol() {
    return offendingSymbol;
  }

  @Nullable
  public String getInput() {
    return input;
  }

  void setInput(@Nullable String input) {
    this.input = input;
  }

}
