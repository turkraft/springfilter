grammar Filter;

options {
	contextSuperClass = Filter;
}

import FilterTokens;

filter: predicate EOF;

predicate:
	left = predicate operator = (
		LIKE
		| EQUAL
		| NOT_EQUAL
		| GREATER_THAN
		| GREATER_THAN_OR_EQUAL
		| LESS_THAN
		| LESS_THAN_OR_EQUAL
	) right = predicate 										# infix
	| left = predicate operator = IN LPAREN (
		arguments += predicate (COMMA arguments += predicate)*
	)+ RPAREN 													# infix
	| left = predicate operator = (
		IS_NULL
		| IS_NOT_NULL
		| IS_EMPTY
		| IS_NOT_EMPTY
	)															# prefix
	| operator = NOT right = predicate							# postfix
	| left = predicate operator = AND right = predicate			# infix
	| left = predicate operator = OR right = predicate			# infix
	| LPAREN predicate RPAREN									# priority
	| ID LPAREN (
		arguments += predicate (COMMA arguments += predicate)*
	)? RPAREN													# function
	| ID (DOT ID)*												# field
	| (STRING | NUMBER | TRUE | FALSE)							# input;
