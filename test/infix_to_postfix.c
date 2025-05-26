#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#define MAX 100

char stack[MAX];
int top = -1;

void push(char ch) {
    stack[++top] = ch;
}

char pop() {
    return stack[top--];
}

char peek() {
    return stack[top];
}

int is_empty() {
    return top == -1;
}

int precedence(char op) {
    switch(op) {
        case '^': return 3;
        case '*':
        case '/': return 2;
        case '+':
        case '-': return 1;
        default:  return 0;
    }
}

int is_right_associative(char op) {
    return op == '^';
}

int is_operator(char ch) {
    return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
}

void infix_to_postfix(const char* infix, char* postfix) {
    int i, k = 0;
    char ch;

    for (i = 0; infix[i]; i++) {
        ch = infix[i];

        if (isspace(ch)) continue;

        if (isalnum(ch)) {
            postfix[k++] = ch;
        }
        else if (ch == '(') {
            push(ch);
        }
        else if (ch == ')') {
            while (!is_empty() && peek() != '(') {
                postfix[k++] = pop();
            }
            if (!is_empty() && peek() == '(')
                pop(); // remove '('
        }
        else if (is_operator(ch)) {
            while (!is_empty() && peek() != '(' &&
                   (precedence(peek()) > precedence(ch) ||
                   (precedence(peek()) == precedence(ch) && !is_right_associative(ch)))) {
                postfix[k++] = pop();
            }
            push(ch);
        }
    }

    while (!is_empty())
        postfix[k++] = pop();

    postfix[k] = '\0';
}

int main() {
    char infix[MAX], postfix[MAX];

    printf("Enter infix expression: ");
    fgets(infix, MAX, stdin);
    infix[strcspn(infix, "\n")] = '\0';

    infix_to_postfix(infix, postfix);

    printf("Postfix expression: %s\n", postfix);

    return 0;
}
