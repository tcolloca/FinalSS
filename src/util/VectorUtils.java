package util;

public class VectorUtils {

	public static double[] sum(double[] v1, double[] v2) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] + v2[i];
		}
		return v;
	}
	
	public static double[] sub(double[] v1, double[] v2) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] - v2[i];
		}
		return v;
	}
	
	public static double[] multiply(double[] v1, double k) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] * k;
		}
		return v;
	}
	
	public static double norm2(double[] v) {
		double acum = 0;
		for (int i = 0; i < v.length; i++) {
			acum += v[i] * v[i];
		}
		return Math.sqrt(acum);
	}

	public static double dist(double[] color1, double[] color2) {
		return norm2(sub(color1, color2));
	}
}
