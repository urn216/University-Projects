#include<iostream>

namespace Complex
{
  class complex {
  private:
    int a;
    int b;
  public:
    complex() {
      a = 1;
      b = 1;
    }
    complex(int x, int y) {
      a = x;
      b = y;
    }
    int geta() {
      return a;
    }
    int getb() {
      return b;
    }
  };
}

using namespace std;
using namespace Complex;

int main()
{
  complex c1;
  complex c2 (5, 10);
  cout << "Complex number 1: "<<c1.geta()<<" + "<<c1.getb()<<"i\n";
  cout << "Complex number 2: "<<c2.geta()<<" + "<<c2.getb()<<"i\n";

  return 0;
}
