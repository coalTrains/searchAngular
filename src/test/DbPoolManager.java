package test;


import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DbPoolManager {
					
	 private Logger mLog = LogManager.getLogger(DbPoolManager.class);
   	 ConnectionPool mConnectionPool;
   	
	 private static DbPoolManager singleton ;

	   private DbPoolManager() {}

	   public static DbPoolManager getInstance() {
		   if(singleton==null){
			   singleton= new DbPoolManager();
		   }
	       return singleton;
	   }
	
	private void openPool(){
		
		mLog.debug( "Start DbPoolManager.openPool()" );
		
		PoolProperties lPoolProperties= new PoolProperties();
		lPoolProperties.setDriverClassName("com.mysql.jdbc.Driver");
		lPoolProperties.setUrl("jdbc:mysql://sviluppo.rete.make-it:3306/formazione");
		lPoolProperties.setUsername("formazione");
		lPoolProperties.setPassword("formazione123");
		lPoolProperties.setMaxActive(30);
		lPoolProperties.setMinIdle(2);
		lPoolProperties.setMaxIdle(10);
		DataSource dataSource = new DataSource();
		dataSource.setPoolProperties(lPoolProperties);
		
		try{
			mConnectionPool = dataSource.createPool();
			
		} catch (Exception e){
			mLog.error(e);
			throw new MyException(e);
		}		
		
		mLog.debug( "End DbPoolManager.openPool()" );
	}
	
	public Connection getPoolConnection(){
		Logger mLog= LogManager.getRootLogger();
		Connection connection;
		
		if(mConnectionPool== null){
			openPool();
		}	
			
		try {
			connection = mConnectionPool.getConnection();
			
			} catch (Exception e) {
				mLog.error( e );
				throw new MyException(e);
			}	
		return connection;
	}
}
