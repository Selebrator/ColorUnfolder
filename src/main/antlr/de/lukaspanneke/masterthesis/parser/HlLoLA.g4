grammar HlLoLA;

@header{package de.lukaspanneke.masterthesis.parser;}

net:
  ('NET' IDENT)?
  (sort)*
  placeList+
  marking? ';'
  transition*
;

sort: 'SORT' type '=' '[' constant (',' constant)* ']' ';' ;

placeList: 'PLACE' place ':' type (',' place ':' type)* ';' ;

marking: 'MARKING' place ':' constant (',' place ':' constant)* ;

transition: 'TRANSITION' transitionName
  variable_declaration*
  transition_preset*
  transition_postset*
  guard?
;

variable_declaration: 'VAR' variable ':' type (',' variable ':' type)* ';' ;
transition_preset: ('CONSUME' place ':' variable (',' place ':' variable)*)? ';' ;
transition_postset: ('PRODUCE' place ':' variable (',' place ':' variable)*)? ';' ;
guard: 'GUARD' formula ';' ;

formula: andFormula ('OR' andFormula)*;
andFormula: implFormula ('AND' implFormula)*;
implFormula: negFormula ('IMPLIES' negFormula)?;
negFormula: 'NOT'? atomFormula;
atomFormula
  : 'true'                                                                   # top
  | 'false'                                                                  # bottom
  | lhs=expression operator=('<' | '<=' | '!=' | '>=' | '>') rhs=expression  # comparison
  | expression ('=' expression)+                                             # equality
  | ('EXISTS' | 'FORALL') (variable (',' variable)) formula                  # quantification
  ;

expression: multiplyExpression (operator=('+' | '-') multiplyExpression)* ;
multiplyExpression: signedAtom ('*' signedAtom)* ;
signedAtom
  : '+' atom
  | '-' atom
  | atom
  ;
atom
  : variable           # variableExpression
  | constant           # constantExpression
  | '(' expression ')' # parExpression
  ;

variable: IDENT;
constant: VALUE;
type: TYPE;
place: IDENT;
transitionName: IDENT;

COMMENT: '{' (~[\r\n])+ '}' -> skip ;
WHITESPACE: [ \t\r\n]+ -> skip ;

VALUE: INT;

INT: '-'? [0-9]+ ;
TYPE: [A-Z]([0-9a-zA-Z])* | 'Int' ;
IDENT: ([0-9a-zA-Z])+ ;
CALCULATION_OPERATOR: [-+*] ;
