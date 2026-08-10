package com.turkraft.springfilter.typesafe;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FilterableProcessorTest {

  @Test
  void testGeneratedSourceContainsClass() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);

    List<MockFieldInfo> fields = new ArrayList<>();
    fields.add(new MockFieldInfo("year", "INT", ""));
    fields.add(new MockFieldInfo("model", "STRING", ""));
    fields.add(new MockFieldInfo("price", "DOUBLE", ""));
    fields.add(new MockFieldInfo("active", "BOOLEAN", ""));

    writeExpectedClass(out, "com.example", "CarFilter", fields);

    String generated = sw.toString();

    Assertions.assertTrue(generated.contains("public class CarFilter"));
    Assertions.assertTrue(generated.contains("extends FilterChain<CarFilter>"));
    Assertions.assertTrue(generated.contains("public static CarFilter where(FilterBuilder fb)"));
    Assertions.assertTrue(generated.contains("public IntFieldStep<CarFilter> year()"));
    Assertions.assertTrue(generated.contains("public StringFieldStep<CarFilter> model()"));
    Assertions.assertTrue(generated.contains("public DoubleFieldStep<CarFilter> price()"));
    Assertions.assertTrue(generated.contains("public BooleanFieldStep<CarFilter> active()"));
    Assertions.assertTrue(generated.contains("return new IntFieldStep<>(this, \"year\");"));
    Assertions.assertTrue(generated.contains("return new StringFieldStep<>(this, \"model\");"));
    Assertions.assertTrue(generated.contains("return new DoubleFieldStep<>(this, \"price\");"));
    Assertions.assertTrue(generated.contains("return new BooleanFieldStep<>(this, \"active\");"));
  }

  @Test
  void testEnumEntityGenerated() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);

    List<MockFieldInfo> fields = new ArrayList<>();
    fields.add(new MockFieldInfo("status", "ENUM", "Status"));

    writeExpectedClass(out, "com.example", "OrderFilter", fields);

    String generated = sw.toString();

    Assertions.assertTrue(generated.contains("public EnumFieldStep<OrderFilter> status()"));
    Assertions.assertTrue(generated.contains("return new EnumFieldStep<>(this, \"status\");"));
  }

  @Test
  void testDateEntityGenerated() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);

    List<MockFieldInfo> fields = new ArrayList<>();
    fields.add(new MockFieldInfo("createdAt", "DATE", ""));

    writeExpectedClass(out, "com.example", "EventFilter", fields);

    String generated = sw.toString();

    Assertions.assertTrue(generated.contains("public DateFieldStep<EventFilter> createdAt()"));
  }

  @Test
  void testCollectionEntityGenerated() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);

    List<MockFieldInfo> fields = new ArrayList<>();
    fields.add(new MockFieldInfo("tags", "COLLECTION", ""));

    writeExpectedClass(out, "com.example", "TagFilter", fields);

    String generated = sw.toString();

    Assertions.assertTrue(generated.contains("public CollectionFieldStep<TagFilter> tags()"));
  }

  @Test
  void testMultipleFieldsGenerated() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);

    List<MockFieldInfo> fields = new ArrayList<>();
    fields.add(new MockFieldInfo("id", "INT", ""));
    fields.add(new MockFieldInfo("name", "STRING", ""));
    fields.add(new MockFieldInfo("amount", "DOUBLE", ""));
    fields.add(new MockFieldInfo("enabled", "BOOLEAN", ""));
    fields.add(new MockFieldInfo("createdDate", "DATE", ""));
    fields.add(new MockFieldInfo("type", "ENUM", "EntityType"));
    fields.add(new MockFieldInfo("items", "COLLECTION", ""));

    writeExpectedClass(out, "com.example", "FullFilter", fields);

    String generated = sw.toString();

    Assertions.assertTrue(generated.contains("public IntFieldStep<FullFilter> id()"));
    Assertions.assertTrue(generated.contains("public StringFieldStep<FullFilter> name()"));
    Assertions.assertTrue(generated.contains("public DoubleFieldStep<FullFilter> amount()"));
    Assertions.assertTrue(generated.contains("public BooleanFieldStep<FullFilter> enabled()"));
    Assertions.assertTrue(generated.contains("public DateFieldStep<FullFilter> createdDate()"));
    Assertions.assertTrue(generated.contains("public EnumFieldStep<FullFilter> type()"));
    Assertions.assertTrue(generated.contains("public CollectionFieldStep<FullFilter> items()"));
  }

  private void writeExpectedClass(PrintWriter out, String packageName,
      String className, List<MockFieldInfo> fields) {
    out.println("package " + packageName + ";");
    out.println();
    out.println("import com.turkraft.springfilter.builder.FilterBuilder;");
    out.println("import com.turkraft.springfilter.typesafe.builder.*;");
    out.println("import javax.annotation.processing.Generated;");
    out.println();
    out.println("@Generated(\"com.turkraft.springfilter.typesafe.FilterableProcessor\")");
    out.println("public class " + className + " extends FilterChain<" + className + "> {");
    out.println();
    out.println("  public static " + className + " where(FilterBuilder fb) {");
    out.println("    return new " + className + "(fb);");
    out.println("  }");
    out.println();
    out.println("  public " + className + "(FilterBuilder fb) {");
    out.println("    super(fb);");
    out.println("  }");
    out.println();

    for (MockFieldInfo field : fields) {
      String stepClass;
      switch (field.kind) {
        case "INT":
          stepClass = "IntFieldStep";
          break;
        case "DOUBLE":
          stepClass = "DoubleFieldStep";
          break;
        case "BOOLEAN":
          stepClass = "BooleanFieldStep";
          break;
        case "STRING":
          stepClass = "StringFieldStep";
          break;
        case "DATE":
          stepClass = "DateFieldStep";
          break;
        case "ENUM":
          stepClass = "EnumFieldStep";
          break;
        case "COLLECTION":
          stepClass = "CollectionFieldStep";
          break;
        default:
          continue;
      }
      out.println("  public " + stepClass + "<" + className + "> " + field.name + "() {");
      out.println("    return new " + stepClass + "<>(this, \"" + field.name + "\");");
      out.println("  }");
      out.println();
    }

    out.println("}");
  }

  private static class MockFieldInfo {
    final String name;
    final String kind;
    final String typeName;

    MockFieldInfo(String name, String kind, String typeName) {
      this.name = name;
      this.kind = kind;
      this.typeName = typeName;
    }
  }

}
