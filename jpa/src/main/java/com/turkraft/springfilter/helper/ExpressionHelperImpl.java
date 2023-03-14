package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.definition.FilterDefinition;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.OperationNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.parser.node.PriorityNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import com.turkraft.springfilter.transformer.processor.FilterNodeProcessor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.Bindable.BindableType;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
class ExpressionHelperImpl implements PathExpressionHelper, ExistsExpressionHelper {

  private final EntityManager entityManager;

  private final Set<Class<? extends FilterDefinition>> ignoreExistsForDefinitions;

  public ExpressionHelperImpl(EntityManager entityManager,
      @IgnoreExists Set<FilterNodeProcessor<?, ?, ?, ?>> ignoreExistsForProcessors) {
    this.entityManager = entityManager;
    ignoreExistsForDefinitions = ignoreExistsForProcessors.stream().map(
            FilterNodeProcessor::getDefinitionType)
        .collect(
            Collectors.toSet());
  }

  @Override
  public Path<?> getPath(RootContext rootContext, String fieldPath) {

    if (rootContext.getPaths().containsKey(fieldPath)) {
      return rootContext.getPaths().get(fieldPath);
    }

    Path<?> path = rootContext.getRoot();

    String[] fields = fieldPath.split("\\.");

    String chain = null;

    for (int i = 0; i < fields.length; i++) {

      String field = fields[i];

      if (chain == null) {
        chain = field;
      } else {
        chain += "." + field;
      }

      Path<?> nextPath = null;

      if (path instanceof MapJoin) {
        if (field.equals("key") || field.equals("keys")) {
          nextPath = ((MapJoin<?, ?, ?>) path).key();
        } else if (field.equals("value") || field.equals("values")) {
          nextPath = ((MapJoin<?, ?, ?>) path).value();
        }
      }

      if (nextPath == null) {
        nextPath = path.get(field);
      }

      boolean shouldJoin =
          !(path instanceof MapJoin) && path instanceof From && shouldJoin(nextPath);

      if (shouldJoin) {
        if (!rootContext.getPaths().containsKey(chain)) {
          rootContext.getPaths().put(chain, ((From<?, ?>) path).join(field));
        }
        nextPath = rootContext.getPaths().get(chain);
      }

      path = nextPath;

      if (!rootContext.getPaths().containsKey(chain)) {
        rootContext.getPaths().put(chain, path);
      }

    }

    return path;

  }

  @Override
  public boolean requiresExists(FilterExpressionTransformer transformer, FilterNode node) {

    if (node instanceof FieldNode fieldNode) {

      Path<?> from = transformer.getRoot();
      Path<?> path;

      String[] fields = fieldNode.getName().split("\\.");

      StringBuilder chain = null;

      for (String field : fields) {

        path = from.get(field);

        if (chain == null) {
          chain = new StringBuilder(field);
        } else {
          chain.append(".").append(field);
        }

        boolean requireExists = path.getModel().getBindableType() == BindableType.PLURAL_ATTRIBUTE;

        if (requireExists) {
          return true;
        }

        from = path;

      }

      return false;

    }

    if (node instanceof InputNode) {
      return false;
    }

    if (node instanceof PriorityNode) {
      return requiresExists(transformer, ((PriorityNode) node).getNode());
    }

    if (node instanceof PlaceholderNode) {
      return false;
    }

    if (node instanceof FunctionNode functionNode) {

      if (ignoreExistsForDefinitions.contains(functionNode.getFunction().getClass())) {
        transformer.registerIgnoreExists(functionNode);
        return false;
      }

      for (FilterNode argument : functionNode.getArguments()) {
        if (requiresExists(transformer, argument)) {
          return true;
        }
      }

      return false;

    }

    if (node instanceof CollectionNode collectionNode) {
      for (FilterNode items : collectionNode.getItems()) {
        if (requiresExists(transformer, items)) {
          return true;
        }
      }
      return false;
    }

    if (node instanceof OperationNode operationNode) {
      if (ignoreExistsForDefinitions.contains(operationNode.getOperator().getClass())) {
        transformer.registerIgnoreExists(operationNode);
        return false;
      }
      if (node instanceof PrefixOperationNode) {
        return requiresExists(transformer, ((PrefixOperationNode) node).getRight());
      }
      if (node instanceof InfixOperationNode) {
        return requiresExists(transformer, ((InfixOperationNode) node).getLeft()) || requiresExists(
            transformer,
            ((InfixOperationNode) node).getRight());
      }
      if (node instanceof PostfixOperationNode) {
        return requiresExists(transformer, ((PostfixOperationNode) node).getLeft());
      }
    }

    throw new UnsupportedOperationException("Unsupported node " + node);

  }

  private boolean isEntityType(Class<?> klass) {
    for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {
      Class<?> entityClass = entityType.getJavaType();
      if (entityClass.equals(klass)) {
        return true;
      }
    }
    return false;
  }

  private boolean shouldJoin(Path<?> path) {
    return path.getModel().getBindableType() == BindableType.PLURAL_ATTRIBUTE
        || (path.getModel().getBindableType() == BindableType.SINGULAR_ATTRIBUTE
        && isEntityType(path.getModel().getBindableJavaType()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Expression<?> wrapWithExists(FilterExpressionTransformer transformer, FilterNode node) {

    Subquery<Integer> subquery = transformer.getCriteriaQuery().subquery(Integer.class);

    Root<?> subroot = subquery.correlate(transformer.getRoot());

    transformer.registerRootContext(node, new RootContext(subroot));

    Expression<?> expression = transformer.transform(node);

    subquery.select(transformer.getCriteriaBuilder().literal(1));

    if (expression.getJavaType().equals(Boolean.class)) {
      subquery.where((Expression<Boolean>) expression);
    }

    return transformer.getCriteriaBuilder().exists(subquery);

  }

}
