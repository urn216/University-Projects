#include <stdio.h>

int main(void)
{
	printf("Enter a string: ");
	char s[50];
	fgets(&s, sizeof(s), stdin);
	puts(s);

	system("pause");
	return 0;
}