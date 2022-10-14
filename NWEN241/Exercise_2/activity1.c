#include <stdio.h>
#include <ctype.h>

void capitalize(char *str)
{
	int i = 0;
	while(str[i] != '\0') {
		if (islower(str[i])) str[i] = toupper(str[i]);
		i++;
	}
}

int main(void)
{
	char s[] = "ABC123 is the most common password";
	capitalize(s);
	printf("%s\n", s);
	return 0;
}
