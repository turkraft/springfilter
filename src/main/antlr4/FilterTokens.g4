lexer grammar FilterTokens;

LIKE: '~';
EQUAL: ':';
NOT_EQUAL: '!';
GREATER_THAN: '>';
GREATER_THAN_OR_EQUAL: '>:';
LESS_THAN: '<';
LESS_THAN_OR_EQUAL: '<:';

AND: 'AND' | 'and';
OR: 'OR' | 'or';
NOT: 'NOT' | 'not';

IN: 'IN' | 'in';

IS_NULL: 'IS NULL' | 'is null';
IS_NOT_NULL: 'IS NOT NULL' | 'is not null';
IS_EMPTY: 'IS EMPTY' | 'is empty';
IS_NOT_EMPTY: 'IS NOT EMPTY' | 'is not empty';

TRUE: 'TRUE' | 'true';
FALSE: 'FALSE' | 'false';

DOT: '.';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';

ID: [a-zA-Z_$][a-zA-Z_$0-9]*;
NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '\'' (~('\'' | '\\') | '\\' ('\'' | '\\'))* '\'';
WS: [ \t]+ -> skip; //
