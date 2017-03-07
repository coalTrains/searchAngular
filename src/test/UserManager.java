package test;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserManager {
	
	private Logger mLog = LogManager.getLogger(UserManager.class);
	public UserManager() {
	}
		
	DbPoolManager pool= DbPoolManager.getInstance();
	
	
	// Aggiungi utente al Db
	public  int addUser(UserBean lUserBeanUser) {
		
		mLog.debug("Start UserManager.addUser(UserBean): " + lUserBeanUser.getId());
		Connection connection=pool.getPoolConnection();
		PreparedStatement pstatement=null;
		PreparedStatement historyStatement = null;
		PreparedStatement pStatementSelect = null;
		PreparedStatement lErrorStatement = null;
		ResultSet rs=null;
		int lIntId=0;
		
		try {
			connection.setAutoCommit(false);
			pstatement = connection.prepareStatement("insert into alex_users (name,surname,date) values ( ? , ? , ? )");
			pstatement.setString(1, lUserBeanUser.getName());
			pstatement.setString(2, lUserBeanUser.getSurname());
			String lStrDate = lUserBeanUser.getDate();
			java.sql.Date lSqlDate =  java.sql.Date.valueOf(lStrDate);
			pstatement.setDate(3, lSqlDate);
			pstatement.executeUpdate();
			
			// Restituisci utente aggiunto
			String sql = "select * from alex_users where name = ? and surname = ? and date = ?";
			pStatementSelect = connection.prepareStatement(sql);
			pStatementSelect.setString(1,lUserBeanUser.getName());
			pStatementSelect.setString(2,lUserBeanUser.getSurname());
			pStatementSelect.setDate(3, lSqlDate);
			rs = pStatementSelect.executeQuery();
			
			while (rs.next()) {
				lIntId = rs.getInt("id");
			}
			lUserBeanUser.setId(lIntId);
			
			// Aggiornamento tabella history
			historyStatement = connection.prepareStatement("insert into history (transactions) values (?)");
			historyStatement.setString(1, "NEW user with id = "+lUserBeanUser.getId()+", name = "+lUserBeanUser.getName() + ", surname = "+ lUserBeanUser.getSurname()+", date = "+lUserBeanUser.getDate()+" ADDED into users");
			historyStatement.executeUpdate();
			connection.commit();
			
		} catch (Exception e) {
			mLog.error("Errore in fase di inserimento nuovo utente su Db. Ripristino dati originali...  " + e );

			try {
				//report errore tabella history
				connection.rollback();
				lErrorStatement = connection.prepareStatement("insert into history (transactions) values (?)");
				lErrorStatement.setString(1, "ERROR while ADDING new user into users with id = "+lUserBeanUser.getId()+", name = "+lUserBeanUser.getName() + ", surname = "+ lUserBeanUser.getSurname()+", date = "+lUserBeanUser.getDate());
				lErrorStatement.executeUpdate();
				lErrorStatement.close();
				
			} catch (Exception e1) {
				mLog.error(e1 );
				throw new MyException(e1);
			}
			throw new MyException(e);
			
		} finally {
			try {
				rs.close();
				pstatement.close();
				historyStatement.close();
				pStatementSelect.close();
				connection.setAutoCommit(true);
				connection.close();
				
			} catch (Exception e) {
				mLog.error(e );
				throw new MyException(e);
			}
			
		}
		mLog.debug("End UserManager.addUser(UserBean)");
		return lIntId;
	}
	
	// Eliminazione da Db
	public UserBean deleteUser(int pIntId) {	
		mLog.debug("Start UserManager.deleteUser(int)");
		Connection connection=pool.getPoolConnection();
		UserBean lUserBeanUser=new UserBean();
		lUserBeanUser.setId(pIntId);
		connection=pool.getPoolConnection();
		PreparedStatement pstatement=null;
		PreparedStatement pStatementDelete = null;
		PreparedStatement historyStatement = null;
		PreparedStatement lErrorStatement = null;
		
		try {
			connection.setAutoCommit(false);

			//Restituisci utente eliminato
			String lStrSql = "select * from alex_users where id = ?";
			pstatement = connection.prepareStatement(lStrSql);
			pstatement.setInt(1,pIntId);
			ResultSet rs = pstatement.executeQuery();
			
			while (rs.next()) {
				lUserBeanUser.setName(rs.getString("name"));
				lUserBeanUser.setSurname(rs.getString("surname"));
				String lStrDate= (String) rs.getDate("date").toString();
				lUserBeanUser.setDate(lStrDate);
			}
			rs.close();
			
			// Elimina
			pStatementDelete = connection.prepareStatement("DELETE FROM alex_users WHERE id = ?");
			pStatementDelete.setInt(1, pIntId);
			pStatementDelete.executeUpdate();
			
			// Scrittura transaction
			historyStatement = connection.prepareStatement("insert into history (transactions) values (?)");
			historyStatement.setString(1, "user with id = "+pIntId+" DELETED" );
			historyStatement.executeUpdate();
			connection.commit();
			
		} catch (Exception e) {
			mLog.error("ERROR while DELETING user with id = "+pIntId+".  "+ e );
			
			try {
				//Report errore tabella history
				connection.rollback();
				lErrorStatement = connection.prepareStatement("insert into history (transactions) values (?)");
				lErrorStatement.setString(1, "ERROR while DELETING new user in users with id = "+lUserBeanUser.getId()+", name = "+lUserBeanUser.getName() + ", surname = "+ lUserBeanUser.getSurname()+", date = "+lUserBeanUser.getDate());
				lErrorStatement.executeUpdate();
				lErrorStatement.close();
				
			} catch (Exception e1) {
				mLog.error(e1);
				throw new MyException(e1);
			}
			throw new MyException(e);
			
		} finally{
			
			try {
				pstatement.close();
				pStatementDelete.close();
				historyStatement.close();
				connection.setAutoCommit(true);
				connection.close();
				
			} catch (Exception e) {
				mLog.error( e );
				throw new MyException(e);
			}
		}
		mLog.debug("End UserManager.deleteUser(int)");
		return lUserBeanUser;
	}
	
	// Modifica utente Db
	public UserBean updateUser(UserBean pUserBeanUser) {
		
		mLog.debug("Start UserManager.updateUser(UserBean)");
		UserBean lUserBeanOldUser = new UserBean();
		Connection connection=pool.getPoolConnection();
		PreparedStatement pStatement=null;
		PreparedStatement pStatementUpdate=null;
		PreparedStatement historyStatement=null;
		PreparedStatement lErrorStatement = null;
		ResultSet rs=null;
		int lIntID= pUserBeanUser.getId();
		
		try {
			connection.setAutoCommit(false);
			
			//Restituisci vecchio utente
			lUserBeanOldUser.setId(lIntID);
			pStatement = connection.prepareStatement("select * from alex_users where id=?");
			pStatement.setInt(1,lIntID);
			rs = pStatement.executeQuery();
			
			while (rs.next()) {
				lUserBeanOldUser.setName(rs.getString("name"));
				lUserBeanOldUser.setSurname(rs.getString("surname"));
				String lStrDate= (String) rs.getDate("date").toString();
				lUserBeanOldUser.setDate(lStrDate);
			}
			
			
			//Modifica
			pStatementUpdate = connection.prepareStatement("update alex_users set name=?, surname=?, date=?" + "where id=?");
			pStatementUpdate.setString(1, pUserBeanUser.getName());
			pStatementUpdate.setString(2, pUserBeanUser.getSurname());
			String lStrDate= pUserBeanUser.getDate();
			java.sql.Date lSqlDate = java.sql.Date.valueOf(lStrDate);
			pStatementUpdate.setDate(3, lSqlDate);
			pStatementUpdate.setInt(4, lIntID);
			pStatementUpdate.executeUpdate();

			//Aggiornamento tabella history
			historyStatement = connection.prepareStatement("insert into history (transactions) values (?)");
			historyStatement.setString(1, "user with id = "+lUserBeanOldUser.getId()+" UPDATED" );
			historyStatement.executeUpdate();
			connection.commit();

		} catch (Exception e) {
			mLog.error( e );
			
			try {
				//Report errore tabella history
				connection.rollback();
				lErrorStatement = connection.prepareStatement("insert into history (transactions) values (?)");
				lErrorStatement.setString(1, "ERROR while UPDATING new user into users with id = "+lUserBeanOldUser.getId()+", name = "+lUserBeanOldUser.getName() + ", surname = "+ lUserBeanOldUser.getSurname()+", date = "+lUserBeanOldUser.getDate());
				lErrorStatement.executeUpdate();
				lErrorStatement.close();
				
			} catch (SQLException e1) {
				mLog.error("ERROR while UPDATING user with id ="+lIntID+".  "+e);
				throw new MyException(e1);
			}
			throw new MyException(e);
			
		} finally {
			try {
				rs.close();
				pStatement.close();
				pStatementUpdate.close();
				historyStatement.close();
				connection.setAutoCommit(true);
				connection.close();
				
			} catch (Exception e) {
				mLog.error( e );
				throw new MyException(e);
			}

		}
		mLog.debug("End UserManager.updateUser(UserBean)");
		return lUserBeanOldUser;
	}
	
	// Lista completa
	public List<UserBean> getAllUsers() {
		
		mLog.debug("Start UserManager.getAllUsers()");
		List<UserBean> lLstUsers = new ArrayList<UserBean>();
		lLstUsers = getUsers(new UserBean());
		mLog.debug("End  UserManager.getAllUsers()");
		return lLstUsers;
	}
	
	//Ricerca utenti
	public List<UserBean> getUsers(UserBean pUserBeanUser) {
		
		mLog.debug("Start UserManager.getUsers(UserBean)");
		boolean lBlnUseAnd = false;
		
		List <UserBean> lListusers = new ArrayList<UserBean>();
		String lStrNome = pUserBeanUser.getName();
		String lStrCognome = pUserBeanUser.getSurname();
		String lStrData = pUserBeanUser.getDate();
		Connection connection=pool.getPoolConnection(); 		
		
		List<Object> lListParams = new ArrayList<Object>();	
		StringBuilder lStrBuilder = new StringBuilder(); 
		PreparedStatement lPreparedStatement=null;
		ResultSet rs=null;
		
		try {
			lStrBuilder = new StringBuilder("select * from alex_users");
			
			
			if(StringUtils.isNotBlank(lStrNome)){
				lBlnUseAnd = true;
				lStrBuilder.append(" where name like ?");
				lListParams.add((String) "%" + lStrNome + "%");
			}
			if(StringUtils.isNotBlank(lStrCognome)){
				if (lBlnUseAnd) {
					lStrBuilder.append(" and");
				} else {
					lStrBuilder.append(" where");
					lBlnUseAnd = true;
				}
				lStrBuilder.append(" surname like ?");
				lListParams.add((String) "%" + lStrCognome + "%");
			}
			if(StringUtils.isNotBlank(lStrData)){
				java.sql.Date lSqlDate=  java.sql.Date.valueOf(lStrData);
				if (lBlnUseAnd) {
					lStrBuilder.append(" and");
				} else {
					lStrBuilder.append(" where");
					lBlnUseAnd = true;
				}
				lStrBuilder.append(" date = ?");
				lListParams.add(lSqlDate);
			}
		
			String lStrSql = lStrBuilder.toString();

			lPreparedStatement = connection.prepareStatement(lStrSql);
			for (int j = 0; j < lListParams.size(); j++) {
				Object lObject = lListParams.get(j);
				if(lObject instanceof String){
					lPreparedStatement.setString(j+1, (String) lObject);
				}
				else if(lObject instanceof Date){
					lPreparedStatement.setDate(j+1, (Date) lObject);
				}
			}
			
			rs = lPreparedStatement.executeQuery();			
			
			while (rs.next()) {
				UserBean lUserBeanUser = new UserBean();
				lUserBeanUser.setId(rs.getInt("id"));
				lUserBeanUser.setName(rs.getString("name"));
				lUserBeanUser.setSurname(rs.getString("surname"));
				String lStrDate= (String) rs.getDate("date").toString();
				lUserBeanUser.setDate(lStrDate);
				
				lListusers.add(lUserBeanUser);
			} 	
			

		} catch (Exception e) {
			mLog.error( e );
			throw new MyException(e);
			
		} finally{
			try {
				rs.close();
				lPreparedStatement.close();
				connection.close();
				
			} catch (Exception e) {
				mLog.error( e );
				throw new MyException(e);
			}
		}
		mLog.debug("End UserManager.getUsers(UserBean)");

			return lListusers;	
	}
	
}
