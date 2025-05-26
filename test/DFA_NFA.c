#include <stdio.h>
#include <string.h>

#define STATES 3
#define SYMBOLS 2

int dfa[STATES][SYMBOLS] = {
    {1, 0},
    {1, 2},
    {1, 0}
};

int nfa[STATES][SYMBOLS][STATES] = {
    { {1, -1}, {-1} },
    { {1, 2}, {-1} },
    { {-1}, {-1} }
};

int is_accepted_dfa(char *input) {
    int state = 0;

    for (int i = 0; input[i]; i++) {
        int sym = input[i] - 'a';
        if (sym < 0 || sym >= SYMBOLS) return 0;
        state = dfa[state][sym];
    }

    return state == 2;
}

int is_accepted_nfa(char *input) {
    int current[STATES] = {1, 0, 0};
    int next[STATES] = {0};

    for (int i = 0; input[i]; i++) {
        int sym = input[i] - 'a';
        if (sym < 0 || sym >= SYMBOLS) return 0;

        memset(next, 0, sizeof(next));

        for (int state = 0; state < STATES; state++) {
            if (current[state]) {
                for (int k = 0; k < STATES && nfa[state][sym][k] != -1; k++) {
                    next[nfa[state][sym][k]] = 1;
                }
            }
        }

        memcpy(current, next, sizeof(current));
    }

    return current[2];
}

int main() {
    char input[100];

    printf("Enter a string (only 'a' and 'b' allowed): ");
    scanf("%s", input);

    if (is_accepted_dfa(input))
        printf("DFA: Accepted\n");
    else
        printf("DFA: Rejected\n");

    if (is_accepted_nfa(input))
        printf("NFA: Accepted\n");
    else
        printf("NFA: Rejected\n");

    return 0;
}
