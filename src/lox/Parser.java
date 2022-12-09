package lox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lox.TokenType.*;

class Parser {
  private static class ParseError extends RuntimeException {}
  private final List<Token> tokens;

  // looking for a way to consolidate all the extra functions that repeat logic
  private final List<TokenType> binaryTokenTypes = Arrays.asList(
    BANG_EQUAL,
    EQUAL,
    GREATER,
    GREATER_EQUAL,
    LESS,
    LESS_EQUAL,
    MINUS,
    PLUS
  );
  private final List<TokenType> operatorTokenTypes = Arrays.asList(
    SLASH,
    STAR,
    BANG
  );
  private int current = 0;

  Parser(List<Token> tokens){
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Expr expression() {
    return assignment();
  }

  private Stmt declaration(){
    try {
      if (match(CLASS)) return classDeclaration();
      if (match(FUN)) return function("function");
      if (match(VAR)) return varDeclaration();
      if (peek().type == NUMBER || peek().type == STRING) return expressionStatement();
      return statement();
    } catch (ParseError err){
      synchronize();
      return null;
    }
  }

  private Stmt classDeclaration(){
    Token name = consume(IDENTIFIER, "Expect class name");
    consume(LEFT_BRACE, "Expect '{' before class body.");

    List<Stmt.Function> methods = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()){
      methods.add(function("method"));
    }

    consume(RIGHT_BRACE, "Expect '}' after class body.");

    return new Stmt.Class(name, methods);
  }
  private Stmt statement(){
    if (match(FOR)) return forStatement();
    if (match(IF)) return ifStatement();
//    if (match(BREAK)) return breakStatement();
    if (match(PRINT)) return printStatement();
    if (match(RETURN)) return returnStatement();
    if (match(WHILE)) return whileStatement();
    if (match(LEFT_BRACE)) return new Stmt.Block(block());

    return expressionStatement();
  }

  private Stmt ifStatement(){
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr cond = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;

    if (match(ELSE)){
      elseBranch = statement();
    }

    return new Stmt.If(cond, thenBranch, elseBranch);
  }

//  private Stmt breakStatement(){
//    Token prev = previous();
//    consume(SEMICOLON, "Expected ';' after break.");
//
//    return new Stmt.Break(prev, null);
//  }

  private Stmt whileStatement(){
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr cond = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition");
    Stmt body = statement();

    return new Stmt.While(cond, body);
  }

  private Stmt forStatement(){
    consume(LEFT_PAREN, "Expect '(' after 'for'");
    Stmt initializer;

    if (match(SEMICOLON)){
      initializer = null;
    } else if (match(VAR)){
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr cond = null;
    if (!check(SEMICOLON)){
      cond = expression();
    }
    consume(SEMICOLON, "Expect ';' after loop condition");

    Expr increment = null;
    if (!check(RIGHT_PAREN)){
      increment = expression();
    }
    consume(RIGHT_PAREN, "Expect ')' after for clause.");
    Stmt body = statement();

    if (increment != null) {
      body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
    }

    if (cond == null) cond = new Expr.Literal(true);
    body = new Stmt.While(cond, body);

    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }

    return body;
  }

  private Stmt printStatement(){
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt returnStatement(){
    Token keyword = previous();

    Expr val = null;
    if (!check(SEMICOLON)){
      val = expression();
    }
    consume(SEMICOLON, "Expect ';' after return value.");
    return new Stmt.Return(keyword, val);
  }

  private Stmt varDeclaration(){
    Token name = consume(IDENTIFIER, "Expect variable name");

    Expr initializer = null;
    if (match(EQUAL)){
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration");
    return new Stmt.Var(name, initializer);
  }

  private Stmt expressionStatement(){
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Stmt.Function function(String kind){
    Token name = consume(IDENTIFIER, "Expect " + kind + " name.");

    consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<Token> params = new ArrayList<>();
    if (!check(RIGHT_PAREN)){
      do {
        if (params.size() >= 255){
          error(peek(), "Can't have more than 255 parameters");
        }
        params.add(consume(IDENTIFIER, "Expect parameter name"));
      } while (match(COMMA));
    }
    consume(RIGHT_PAREN, "Expect ')' after parameters");

    consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
    List<Stmt> body = block();
    return new Stmt.Function(name, params, body);
  }

  private List<Stmt> block(){
    List<Stmt> statements = new ArrayList<>();
//    int breakIdx = -1;
    while (!check(RIGHT_BRACE) && !isAtEnd()) {
//      if (check(BREAK)){
//        breakIdx = statements.size();
//      }
      statements.add(declaration());
    }
    consume(RIGHT_BRACE, "Expect '}' after block");
//    if (breakIdx > 0){
//      //  Update existing break to last statement
//      Token breakToken = ((Stmt.Break) statements.get(breakIdx)).name;
//      Stmt.Break updatedBreak = new Stmt.Break(breakToken, statements.get(statements.size() - 1));
//      statements.set(breakIdx, updatedBreak);
//    }
    return statements;
  }

  private Expr assignment(){
    Expr expr = or();

    if (match(EQUAL)){
      Token equals = previous();
      Expr value = assignment();

      if (expr  instanceof Expr.Variable){
        Token name = ((Expr.Variable) expr).name;
        return new Expr.Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }
    return expr;
  }

  private Expr or(){
    Expr expr = and();

    if (match(OR)){
      Token op = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, op, right);
    }
    return expr;
  }

  private Expr and(){
    Expr expr = equality();

    if (match(AND)){
      Token op = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, op, right);
    }
    return expr;
  }

  private Expr expressionCrunched(){
    Expr expr = primary();

    while (!isAtEnd()) {
      if (match(binaryTokenTypes.toArray(TokenType.values()))){
        Token op = previous();
        Expr right = expressionCrunched();
        return new Expr.Binary(expr, op, right);
      } else if (match(SLASH, STAR)){
        Token op = previous();
        Expr right = expressionCrunched();
        return new Expr.Binary(expr, op, right);
      } else if (match(BANG, MINUS)){
        Token op = previous();
        Expr right = expressionCrunched();
        return new Expr.Unary(op, right);
      }
    }

    return expr;
  }

  private Expr equality(){
    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)){
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr comparison(){
    Expr expr = term();

    while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS, TERNARY)){
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr factor() {
    Expr expr = unary();

    while (match(SLASH, STAR)){
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr unary(){
    if (match(BANG, MINUS)){
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return call();
  }
  private Expr finishCall(Expr callee){
    List<Expr> args = new ArrayList<>();

    if (!check(RIGHT_PAREN)){
      do {
        args.add(expression());
        if (args.size() >= 255){ error(peek(), "Can't have more than 255 arguments.");}
      } while (match(COMMA));
    }

    Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
    return new Expr.Call(callee, paren, args);
  }
  private Expr call(){
    Expr expr = primary();

    while (true){
      if (match(LEFT_PAREN)){
        expr = finishCall(expr);
      } else {
        break;
      }
    }
    return expr;
  }



  private Expr primary(){
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)){
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression");
  }

  private boolean match(TokenType... types) {
    for (TokenType type: types) {
      if (check(type)){
        advance();
        return true;
      }
    }
    return false;
  }

  private Token consume(TokenType type, String message) {
    if(check(type)) return advance();
    throw error(peek(), message);
  }

  private boolean check(TokenType type){
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance(){
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd(){
    return peek().type == EOF;
  }

  private Token peek(){
    return tokens.get(current);
  }

  private Token previous(){
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  private void synchronize(){
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type){
        case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, BREAK -> {}
        case RETURN -> {return;}
      }
      advance();
    }
  }
}
