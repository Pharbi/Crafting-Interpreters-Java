package lox;

class AstPrinter implements Expr.Visitor<String>, Expr.VisitorRPN<String>{


  String print(Expr expr){
    return expr.accept(this);
  }

  String printRPN(Expr expr) {
    return expr.acceptRPN(this);
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    return null;
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return null;
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  private String parenthesizeRPN(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    for (Expr expr : exprs) {
      builder.append(expr.acceptRPN(this));
      builder.append(" ");
    }
    builder.append(name);
    return builder.toString();
  }


  public static void main(String[] args) {
    Expr expression = new Expr.Binary(
      new Expr.Binary(
        new Expr.Literal(1),
        new Token(TokenType.PLUS, "+", null, 1),
        new Expr.Literal(2)),
      new Token(TokenType.STAR, "*", null, 1),
      new Expr.Binary(
        new Expr.Literal(4),
        new Token(TokenType.MINUS, "-", null, 1),
        new Expr.Literal(3)
      ));
    System.out.println("Conventional way");
    System.out.println(new AstPrinter().print(expression));
    System.out.println("RPN way");
    System.out.println(new AstPrinter().printRPN(expression));
  }

  @Override
  public String visitAssignExprRPN(Expr.Assign expr) {
    return null;
  }

  @Override
  public String visitBinaryExprRPN(Expr.Binary expr) {
    return parenthesizeRPN(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitGroupingExprRPN(Expr.Grouping expr) {
    return parenthesizeRPN("group", expr.expression);
  }

  @Override
  public String visitLiteralExprRPN(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExprRPN(Expr.Unary expr) {
    return parenthesizeRPN(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExprRPN(Expr.Variable expr) {
    return null;
  }
}
