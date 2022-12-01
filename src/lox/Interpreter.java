package lox;

class Interpreter implements Expr.Visitor<Object>{

  void interpret(Expr expression){
    try {
      Object value = evaluate(expression);
      System.out.println(stringify(value));
    } catch (RuntimeError err){
      Lox.runtimeError(err);
    }
  }
  @Override
  public Object visitLiteralExpr(Expr.Literal expr){
    return expr.value;
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr){
    return evaluate(expr.expression);
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr){
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG -> {return !isTruthy(right);}
      case MINUS -> {
        checkNumberOperand(expr.operator, right);
        return -(double) right;
      }
    }
    return null;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr){
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type){
      case GREATER -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left > (double) right;}
      case GREATER_EQUAL -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left >= (double) right;}
      case LESS -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left < (double) right;}
      case LESS_EQUAL -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left <= (double) right;}
      case BANG_EQUAL -> {
        checkNumberOperands(expr.operator, left, right);
        return !isEqual(left, right);}
      case EQUAL_EQUAL -> {
        checkNumberOperands(expr.operator, left, right);
        return isEqual(left, right);}
      case MINUS -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;}
      case PLUS -> {
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }
        if (left instanceof String && right instanceof String){
          return (String) left + (String) right;
        }
      }
      case SLASH -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left / (double) right; }
      case STAR -> {
        checkNumberOperands(expr.operator, left, right);
        return (double) left * (double) right; }
    }
    return null;
  }

  private Object evaluate(Expr expr){
    return expr.accept(this);
  }

  private boolean isTruthy(Object object){
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  private boolean isEqual(Object a, Object b){
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }

  private String stringify(Object object){
    if (object == null) return "nil";

    if (object instanceof Double){
      String text = object.toString();

      if (text.endsWith(".0")){
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return object.toString();
  }

  private void checkNumberOperand(Token operator, Object operand){
    if(operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object a, Object b){
    if (a instanceof Double && b instanceof Double) return;
    if (a instanceof String && b instanceof String) return;
    throw new RuntimeError(operator, "Operands must both be of the same type (numbers)");
  }
}