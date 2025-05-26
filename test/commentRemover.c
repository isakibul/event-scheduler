#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

int is_space(char ch) {
    return ch == ' ' || ch == '\t' || ch == '\n';
}

int main() {
    FILE *fin = fopen("input.txt", "r");
    FILE *fout = fopen("output.txt", "w");

    if (!fin || !fout) {
        printf("Error opening file.\n");
        return 1;
    }

    char ch, next;
    int in_single_comment = 0, in_multi_comment = 0;
    int prev_space = 0;

    while ((ch = fgetc(fin)) != EOF) {
        if (in_single_comment) {
            if (ch == '\n') {
                in_single_comment = 0;
                fputc('\n', fout);
                prev_space = 1;
            }
            continue;
        }

        if (in_multi_comment) {
            if (ch == '*' && (next = fgetc(fin)) == '/') {
                in_multi_comment = 0;
            }
            continue;
        }

        if (ch == '/') {
            next = fgetc(fin);
            if (next == '/') {
                in_single_comment = 1;
                continue;
            } else if (next == '*') {
                in_multi_comment = 1;
                continue;
            } else {
                fputc(ch, fout);
                ungetc(next, fin);
                prev_space = 0;
            }
        } else if (is_space(ch)) {
            if (!prev_space) {
                fputc(' ', fout);
                prev_space = 1;
            }
        } else {
            fputc(ch, fout);
            prev_space = 0;
        }
    }

    fclose(fin);
    fclose(fout);

    printf("Comments and extra whitespace removed. Output written to output.c\n");
    return 0;
}
