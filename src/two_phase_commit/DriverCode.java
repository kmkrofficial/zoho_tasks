package two_phase_commit;

public class DriverCode {
	public static void main(String[] args) {
		MainCode[] objs = new MainCode[3];
		boolean overallResult = true;
		for(int i=1;i<4;i++) {
			if (i==3){
				objs[i-1] = new MainCode("insert into t1 values ("+"dummyText"+","+i*100+");", i);
				continue;
			}
			if(i%2==0) {
				objs[i-1] = new MainCode("insert into t1 values ("+i+","+i*100+");", i);
			} else {
				objs[i-1] = new MainCode("insert into t2 values ("+i+","+i*100+");", i);
			}
		}
		for(int i=0;i<objs.length;i++) {
			objs[i].callerFunction();
			if (!objs[i].isResult()) {
				overallResult = false;
			}
		}
		if (overallResult) {
			for(MainCode i: objs) {
				i.commit();
			}
			System.out.println("Successful Commit....");
		}
		else {
			System.out.println("Error Occured, Rolling back....");
			for(MainCode i:objs) {
				i.rollback();
			}
			System.out.println("Rollback successful");
		}
	}
}
