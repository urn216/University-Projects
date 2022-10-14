#include <stdio.h>

int main(void)
{
	float cTemp = 0, fTemp = 0;
	printf("Enter temperature: ");
	scanf("%f", &cTemp);
	fTemp = (cTemp * 9 / 5) + 32;
	printf("%.3f\n", fTemp);

	system("pause");
	return 0;
}