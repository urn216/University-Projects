import java.util.ArrayList;

/** Waveform loads and saves wav files into arraylists of double */

public class FT{

  public static final ArrayList<ComplexNumber> dft(ArrayList<Double> x) {

    int N = x.size();
    double circle = 2*Math.PI/N;
    ArrayList<ComplexNumber> X = new ArrayList<ComplexNumber>(N);

    for (int k=0; k < N; k++) {
      X.add(new ComplexNumber());
      for (int n=0; n < N; n++) {
        double b = n * k * circle;
        X.set(k, X.get(k).add(new ComplexNumber(x.get(n), 0).multiply(new ComplexNumber(Math.cos(-b), Math.sin(-b)))));
      }
    }

    return X;
  }

  public static final ArrayList<ComplexNumber> idft(ArrayList<ComplexNumber> X) {

    int N = X.size();
    double circle = 2*Math.PI/N;
    ArrayList<ComplexNumber> x = new ArrayList<ComplexNumber>(N);

    for (int k=0; k < N; k++) {
      x.add(new ComplexNumber());
      for (int n=0; n < N; n++) {
        double b = n * k * circle;
        x.set(k, x.get(k).add(new ComplexNumber(Math.cos(b), Math.sin(b)).multiply(X.get(n))));
      }
    }
    return x;
  }

  public static final ArrayList<ComplexNumber> fft(ArrayList<Double> x) {
    int N = x.size();

    if (N==1) {
      return dft(x);
    }

    ArrayList<ComplexNumber> X = new ArrayList<ComplexNumber>(N);

    ArrayList<Double> xEven = new ArrayList<Double>(N/2);
    ArrayList<Double> xOdd = new ArrayList<Double>(N/2);
    for (int i = 0; i < N; i+=2) {
      X.add(new ComplexNumber());
      X.add(new ComplexNumber());
      xEven.add(x.get(i));
      xOdd.add(x.get(i+1));
    }

    ArrayList<ComplexNumber> XEven = fft(xEven);
    ArrayList<ComplexNumber> XOdd = fft(xOdd);

    double circle = 2*Math.PI/N;

    for (int k = 0; k < N/2; k++) {
      double b = k*circle;
      ComplexNumber wF = new ComplexNumber(Math.cos(-b), Math.sin(-b));
      X.set(k, XEven.get(k).add(XOdd.get(k).multiply(wF)));
      b = (k+N/2)*circle;
      ComplexNumber wS = new ComplexNumber(Math.cos(-b), Math.sin(-b));
      X.set(k+N/2, XEven.get(k).add(XOdd.get(k).multiply(wS)));
    }

    return X;
  }

  public static final ArrayList<ComplexNumber> ifft(ArrayList<ComplexNumber> X) {
    int N = X.size();

    if (N==1) {
      return idft(X);
    }

    ArrayList<ComplexNumber> x = new ArrayList<ComplexNumber>(N);

    ArrayList<ComplexNumber> XEven = new ArrayList<ComplexNumber>(N/2);
    ArrayList<ComplexNumber> XOdd = new ArrayList<ComplexNumber>(N/2);
    for (int i = 0; i < N; i+=2) {
      x.add(new ComplexNumber());
      x.add(new ComplexNumber());
      XEven.add(X.get(i));
      XOdd.add(X.get(i+1));
    }

    ArrayList<ComplexNumber> xEven = ifft(XEven);
    ArrayList<ComplexNumber> xOdd = ifft(XOdd);

    double circle = 2*Math.PI/N;

    for (int k = 0; k < N/2; k++) {
      double b = k*circle;
      ComplexNumber wF = new ComplexNumber(Math.cos(b), Math.sin(b));
      x.set(k, xEven.get(k).add(xOdd.get(k).multiply(wF)));
      b = (k+N/2)*circle;
      ComplexNumber wS = new ComplexNumber(Math.cos(b), Math.sin(b));
      x.set(k+N/2, xEven.get(k).add(xOdd.get(k).multiply(wS)));
    }

    return x;
  }

  /**
  * Trims a list of type double to a power of two. Useful for FFT.
  * @param x The list to trim.
  * @return The trimmed list.
  */
  public static final ArrayList<Double> trimList(ArrayList<Double> x) {
    int i = 1;
    while (i <= x.size()) {i*=2;}
    for(int j = x.size()-1; j>=i/2; j--) {x.remove(j);}
    return x;
  }
}
