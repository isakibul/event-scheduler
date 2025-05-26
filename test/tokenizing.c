#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

int isKeyword(char word[]) {
    char keywords[4][10] = {"int", "float", "if", "else"};
    for (int i = 0; i < 4; i++) {
        if (strcmp(word, keywords[i]) == 0)
            return 1;
    }
    return 0;
}

void simpleTokenizer(char code[]) {
    int i = 0;
    char word[50];
    while (code[i] != '\0') {
        if (isspace(code[i])) {
            i++;
            continue;
        }

        if (isalpha(code[i])) {
            int j = 0;
            while (isalnum(code[i])) {
                word[j++] = code[i++];
            }
            word[j] = '\0';

            if (isKeyword(word))
                printf("Keyword: %s\n", word);
            else
                printf("Identifier: %s\n", word);
        }
        else if (isdigit(code[i])) {
            int num = 0;
            while (isdigit(code[i])) {
                num = num * 10 + (code[i] - '0');
                i++;
            }
            printf("Number: %d\n", num);
        }
        else if (code[i] == '+' || code[i] == '-' || code[i] == '=') {
            printf("Operator: %c\n", code[i]);
            i++;
        }
        else if (code[i] == ';' || code[i] == '(' || code[i] == ')') {
            printf("Delimiter: %c\n", code[i]);
            i++;
        }
        else {
            printf("Unknown: %c\n", code[i]);
            i++;
        }
    }
}

int main() {
    FILE *fp;
    char code[1000];

    fp = fopen("input.txt", "r");
    if (fp == NULL) {
        printf("Error opening file.\n");
        return 1;
    }

    char line[200];
    code[0] = '\0';
    while (fgets(line, sizeof(line), fp)) {
        strcat(code, line);
    }

    fclose(fp);

    printf("Tokens:\n");
    simpleTokenizer(code);

    return 0;
}
