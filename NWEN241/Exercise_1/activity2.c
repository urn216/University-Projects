#include <stdio.h>

int main(void)
{
	printf("Enter a string without whitespaces: ");
	char s[20];
	scanf("%s", &s);
	printf("%s\n", s);

	system("pause");
	return 0;
}