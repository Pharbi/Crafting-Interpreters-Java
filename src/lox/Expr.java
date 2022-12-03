package lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);

    R visitBinaryExpr(Binary expr);

    R visitGroupingExpr(Grouping expr);

    R visitLiteralExpr(Literal expr);

    R visitUnaryExpr(Unary expr);

    R visitVariableExpr(Variable expr);
  }

  abstract <R> R accept(Visitor<R> visitor);

  interface VisitorRPN<R> {
    R visitAssignExprRPN(Assign expr);

    R visitBinaryExprRPN(Binary expr);

    R visitGroupingExprRPN(Grouping expr);

    R visitLiteralExprRPN(Literal expr);

    R visitUnaryExprRPN(Unary expr);

    R visitVariableExprRPN(Variable expr);
  }

  abstract <R> R acceptRPN(VisitorRPN<R> visitor);

  static class Assign extends Expr {
    final Token name;
    final Expr value;

    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitAssignExprRPN(this);
    }
  }

  static class Binary extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;

    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitBinaryExprRPN(this);
    }
  }

  static class Grouping extends Expr {
    final Expr expression;

    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitGroupingExprRPN(this);
    }
  }

  static class Literal extends Expr {
    final Object value;

    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitLiteralExprRPN(this);
    }
  }

  static class Unary extends Expr {
    final Token operator;
    final Expr right;

    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitUnaryExprRPN(this);
    }
  }

  static class Variable extends Expr {
    final Token name;

    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitVariableExprRPN(this);
    }
  }
}
