package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);

    R visitExpressionStmt(Expression stmt);

    R visitFunctionStmt(Function stmt);

    R visitIfStmt(If stmt);

    R visitBreakStmt(Break stmt);

    R visitPrintStmt(Print stmt);

    R visitReturnStmt(Return stmt);

    R visitVarStmt(Var stmt);

    R visitWhileStmt(While stmt);
  }

  abstract <R> R accept(Visitor<R> visitor);

  interface VisitorRPN<R> {
    R visitBlockStmtRPN(Block stmt);

    R visitExpressionStmtRPN(Expression stmt);

    R visitFunctionStmtRPN(Function stmt);

    R visitIfStmtRPN(If stmt);

    R visitBreakStmtRPN(Break stmt);

    R visitPrintStmtRPN(Print stmt);

    R visitReturnStmtRPN(Return stmt);

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

  static class Function extends Stmt {
    final Token name;
    final List<Token> params;
    final List<Stmt> body;

    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitFunctionStmtRPN(this);
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

  static class Break extends Stmt {
    final Token name;
    final Stmt skipToStmt;

    Break(Token name, Stmt skipToStmt) {
      this.name = name;
      this.skipToStmt = skipToStmt;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitBreakStmtRPN(this);
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

  static class Return extends Stmt {
    final Token keyword;
    final Expr value;

    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    @Override
    <R> R acceptRPN(VisitorRPN<R> visitor) {
      return visitor.visitReturnStmtRPN(this);
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
