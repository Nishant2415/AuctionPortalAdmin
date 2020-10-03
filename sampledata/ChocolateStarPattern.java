class Main {
	public static void main(String[] args) {
		int k=0, n=20, s=10, p=0;
		boolean toogle;
		for (int i=1; i<10; i++) {
			if(i<=5) k++; else k--;
			if(i<=5) n--; else n++;
			if(i<=5) p++; else p--;
			toogle = true;
			for (int j=1; j<20; j++) {				
				if(j<=k || j>=n || (j>s-p && j<s+p && toogle)) {
					System.out.print("*");
					if(i==5) toogle = true;
					else toogle = false;
				} else {
					toogle = true;
					System.out.print(" ");
				}				
			}
			System.out.println();
		}
	}
}
