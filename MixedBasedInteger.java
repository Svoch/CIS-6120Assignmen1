public class MixedBasedInteger {

	private int[] base;
	private int[] digit;
	private int[] factor;

	public static void main(String[] args) {
		int decimal = 3;
		
		int[] schema = new int[5];
		for(int i=0; i<5; i++)
			schema[i] = 2;
		
		MixedBasedInteger mbi = new MixedBasedInteger(schema);

		for(int i=0; i<schema.length; i++)
			System.out.print(schema[i]+" ");
		System.out.println();
		System.out.println("-------------------");

		for(int i=0; i<11; i++) {
			for(int j=0; j<mbi.getDigit().length; j++)
				System.out.print(mbi.getDigit()[j]+" ");
			
			System.out.println( " "+ mbi.getDecimalValue());
			
			mbi.plusplus();
		
		}
		
		System.out.println(mbi.getDecimalValue());
		mbi.computeDigitsFor(3);
		mbi.plusplus();
		mbi.computeDigitsFor(decimal);
		System.out.println(mbi.getDecimalValue());
		for(int i=0; i<mbi.getDigit().length; i++)
			System.out.println("digit["+i+"] is "+mbi.getDigit()[i]+" ");
		System.out.println("--------------------");
	}


	public int getDecimalValue() {
		int decimal_value = 0;
		for(int i=0; i<factor.length; i++) {
			decimal_value += factor[i]*digit[i];
		}
		return decimal_value;
	}
	public int[] computeDigitsFor(int decimal_value) {
		int n = decimal_value;
		for(int i=(digit.length-1); i>=0; i--) {
			digit[i] = n % base[i];
			n = (n-digit[i]) / base[i];
		}
		return digit.clone();
	}

	// duh
	public void plusplus() {
		int temp = this.getDecimalValue();
		temp++;
		computeDigitsFor(temp);
	}
	public MixedBasedInteger(int[] base) {

		this. base = base;
		digit = new int[base.length];
		factor = new int[base.length];
		
		for(int i=0; i<base.length; i++) {
			this.base[i] = base[i];
			digit[i] = 0;
			factor[i] = 1;
		}
		
		for(int i=0; i<base.length; i++) {
			for(int j=0; j<i; j++) {
				factor[base.length-1-i] *= base[base.length-1-j];
			}
		}
		factor[base.length-1]=1;
	}
	public int[] getBase() {
		return base;
	}
	public int[] getDigit() {
		return digit;
	}
	public int getLength() {
		return base.length;
	}
	public void setDigit(int[] digit) {
		this.digit = digit;
	}


	public void setFactor(int[] is) {
		factor = is;
	}
	

}
