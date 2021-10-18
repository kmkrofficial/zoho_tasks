package two_phase_commit;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.*;

import com.mysql.cj.jdbc.MysqlXADataSource;

public class MainCode {

	private XADataSource xaDS;
	private XAConnection xaCon;
	private XAResource xaRes;
	private Xid xid;
	private boolean result = false;
	private String statement;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	MainCode(String statement, int i) {
		try {
			xaDS = getDataSource();
			xaCon = xaDS.getXAConnection("root", "password");
			xaRes = xaCon.getXAResource();
			xid = new MyXid(100+i, new byte[] { (byte) (i*2) }, new byte[] { (byte) (i*3) });
			this.statement = statement;
		} catch (SQLException e) {
//			e.printStackTrace();
		}
	}

	private MysqlXADataSource getDataSource() throws SQLException {
		MysqlXADataSource xaDS = new MysqlXADataSource();
		xaDS.setDatabaseName("d1");
		xaDS.setServerName("localhost");
		xaDS.setPortNumber(3306);
		return xaDS;
	}

	public void commit() {
		try {
			xaRes.commit(xid, false);
			System.out.println("Successfully Committed Xid: " + xid);
		} catch (XAException e) {
			System.out.println("Exception Occured on Xid: " + xid + ", " + e);
//			e.printStackTrace();
		}
	}

	public void rollback() {
		try {
			xaRes.rollback(xid);
			System.out.println("Error Occured, Rolling back Xid: " + xid);
		} catch (XAException e) {
			System.out.println("Exception Occured on Xid: " + xid + ", " + e);
//			e.printStackTrace();
		}
	}

	private boolean implementation() {
		Connection con;
		Statement stmt;
		int ret;
		try {
			con = xaCon.getConnection();
			stmt = con.createStatement();
			xaRes.start(xid, XAResource.TMNOFLAGS);
			stmt.executeUpdate(this.statement);
			xaRes.end(xid, XAResource.TMSUCCESS);
			ret = xaRes.prepare(xid);
			if (ret == XAResource.XA_OK) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e1) {
//			e1.printStackTrace();
			return false;
		}
	}

	public void finalize() throws Throwable {
		System.gc();
	}

	public void callerFunction() {
		boolean result = this.implementation();
		setResult(result);
	}

}
