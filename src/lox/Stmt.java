package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);

    R visitExpressionStmt(Expression stmt);

    R visitIfStmt(If stmt);

    R visitPrintStmt(Print stmt);

    R visitVarStmt(Var stmt);

    R visitWhileStmt(While stmt);
  }

  abstract <R> R accept(Visitor<R> visitor);

  interface VisitorRPN<R> {
    R visitBlockStmtRPN(Block stmt);

    R visitExpressionStmtRPN(Expression stmt);

    R visitIfStmtRPN(If stmt);

    R visitPrintStmtRPN(Print stmt);

    R visitVarStmtRPN(Var stmt);

    R visitWhileStmtRPN(While stmt);
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

  static class If extends Stmt {
    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;

    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitIfStmtRPN(this);
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

  static class While extends Stmt {
    final Expr condition;
    final Stmt body;

    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitWhileStmtRPN(this);
    }
  }
}
