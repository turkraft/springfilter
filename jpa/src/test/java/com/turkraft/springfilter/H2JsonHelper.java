package com.turkraft.springfilter;

public class H2JsonHelper {

  public static String jsonbExtractPathText(String json, String key) {
    if (json == null || key == null) {
      return null;
    }
    String searchKey = "\"" + key + "\"";
    int keyIndex = json.indexOf(searchKey);
    if (keyIndex < 0) {
      return null;
    }
    return extractValue(json, keyIndex + searchKey.length());
  }

  public static String jsonbExtractPathText(String json, String key1, String key2) {
    String inner = jsonbExtractPathText(json, key1);
    if (inner == null) {
      return null;
    }
    return jsonbExtractPathText(inner, key2);
  }

  public static String jsonbExtractPathText(String json, String key1, String key2,
      String key3) {
    String inner = jsonbExtractPathText(json, key1, key2);
    if (inner == null) {
      return null;
    }
    return jsonbExtractPathText(inner, key3);
  }

  private static String extractValue(String rest, int start) {
    rest = rest.substring(start).trim();
    if (rest.isEmpty()) {
      return null;
    }
    char first = rest.charAt(0);
    if (first == ':') {
      rest = rest.substring(1).trim();
      first = rest.isEmpty() ? 0 : rest.charAt(0);
    }
    if (first == '"') {
      int closingQuote = rest.indexOf("\"", 1);
      if (closingQuote > 0) {
        return rest.substring(1, closingQuote);
      }
      return null;
    }
    if (first == '{') {
      int depth = 0;
      for (int i = 0; i < rest.length(); i++) {
        if (rest.charAt(i) == '{') {
          depth++;
        } else if (rest.charAt(i) == '}') {
          depth--;
          if (depth == 0) {
            return rest.substring(1, i);
          }
        }
      }
      return null;
    }
    int commaIdx = rest.indexOf(",");
    int braceIdx = rest.indexOf("}");
    int endIdx = commaIdx < 0 ? braceIdx : (braceIdx < 0 ? commaIdx : Math.min(commaIdx, braceIdx));
    if (endIdx > 0) {
      rest = rest.substring(0, endIdx);
    }
    return rest.trim();
  }

}
