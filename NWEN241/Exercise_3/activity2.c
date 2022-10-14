#include <stdio.h>

int main(void)
{
  int a, b;
  FILE *in; // use for handling input file
  FILE *out; // use for handling output file

  // Open raw.txt for reading
  in = fopen("raw.txt", "r");

  // Open processed.txt for writing
  out = fopen("processed.txt", "w");

  if (!in || !out) return 0;

  // Go thru raw.txt file and generate processed.txt file accordingly
  while (1) {
    fscanf(in, "%d %d", &a, &b);
    if (feof(in)) break;
    fprintf(out, "%d %d %d\n", a, b, a+b);
  }
  fclose(in);
  fclose(out);

  return 0;
}
