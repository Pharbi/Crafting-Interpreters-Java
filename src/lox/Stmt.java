package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);

    R visitExpressionStmt(Expression stmt);

    R visitPrintStmt(Print stmt);

    R visitVarStmt(Var stmt);
  }

  abstract <R> R accept(Visitor<R> visitor);

  interface VisitorRPN<R> {
    R visitBlockStmtRPN(Block stmt);

    R visitExpressionStmtRPN(Expression stmt);

    R visitPrintStmtRPN(Print stmt);

    R visitVarStmtRPN(Var stmt);
  }

  abstract <R> R acceptRPN(VisitorRPN<R> visitor);

  static class Block extends Stmt {
    final List<Stmt> statements;

    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitBlockStmtRPN(this);
    }
  }

  static class Expression extends Stmt {
    final Expr expression;

    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitExpressionStmtRPN(this);
    }
  }

  static class Print extends Stmt {
    final Expr expression;

    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitPrintStmtRPN(this);
    }
  }

  static class Var extends Stmt {
    final Token name;
    final Expr initializer;

    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitVarStmtRPN(this);
    }
  }
}
