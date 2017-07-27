package xa;

import com.ibm.mq.*; // WebSphere MQ Classes for Java
import java.sql.*; // JDBC classes 
import com.ibm.db2.jcc.DB2XADataSource; // DB2 type-4 JDBC XADataSource
import java.util.Hashtable; // Hashtable used to create a MQQueueManager

/**
 * This is a simple example application that shows how to put a message to a
 * WebSphere MQ queue and update a database table under a single distributed
 * unit of work. If both operations succeed then the transaction is committed,
 * otherwise it is backed out. 
 */
public class XAExample {

	/** The name of the WebSphere MQ Queue Manager to connect to */
	public static final String QM_NAME = "ExampleQM";

	/** The name of the queue to send a message to */
	public static final String QUEUE_NAME = "SYSTEM.DEFAULT.LOCAL.QUEUE";

	/** The name of the database to connect to */
	public static final String DATABASE_NAME = "TESTDB";

	/** The database username */
	public static final String USER_NAME = "testuser";

	/** The database password */
	public static final String PASSWORD = "secret";

	/** Reference to the MQQueueManager managing the transaction */
	private static MQQueueManager theQueueManager = null;

	/** Reference to the database XAConnection */
	private static Connection theDatabaseConnection = null;

	/**
	 * Main method. This initializes the MQQueueManager and JDBC XAConnection
	 * objects. If this initialization succeeds, then it starts a transaction to
	 * update the database and put a message to a queue, otherwise an error
	 * message describing the failure is sent to System.out
	 */
	public static final void main(String[] args) {
		try {
			// initialize the MQQueueManager and JDBC XAConnection
			System.out.println("Initializing queue manager and database connection");
			initialize();
			// if that worked, start the transaction
			System.out.println("Beginning transaction");
			runTransaction();
			// finished
			System.out.println("Complete!");
		}
		catch (MQException me) {
			// an MQException can be thrown if there is a failure to create the
			// MQQueueManager object, or if the call to
			// MQQueueManager.getJDBCConnection(...) failed
			System.out.println("**FAILURE** Queue Manager initialization failed "
					+ "with the following exception:");
			me.printStackTrace();
		}
		catch (SQLException se) {
			// an SQLException can be thrown if any of the JDBC calls in the
			// initialize() method fail
			System.out.println("**FAILURE** An attempt to access the database failed "
					+ "with the following exception:");
			se.printStackTrace();
		}
		catch (Exception e) {
			// An Exception can be thrown by MQQueueManager.getJDBCConnection()
			System.out.println("**FAILURE** The attempt to register the database connection "
					+ "failed with the following exception:");
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes the MQQueueManager object and the JDBC
	 * XAConnection to the database.
	 * 
	 * @throws MQException if the attempt to create an MQQueueManager object
	 *             failed
	 * @throws SQLException if database access failed
	 * @throws Exception if the MQQueueManager.getJDBCConnection(...) call
	 *             failed
	 */
	public static void initialize() throws Exception {

		// create a properties Hashtable
		Hashtable properties = new Hashtable();
		// set the THREAD_AFFINITY_PROPERTY to TRUE
		properties.put(MQC.THREAD_AFFINITY_PROPERTY, new Boolean(true));

		// add other properties if required
		properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_BINDINGS);

		// create the MQQueueManager
		theQueueManager = new MQQueueManager("ExampleQM", properties);
		System.out.println("  Created MQQueueManager for " + QM_NAME);

		// create an XADataSource
		System.out.println("  Creating a XADataSource for " + DATABASE_NAME);
		DB2XADataSource xads = new DB2XADataSource();

		// configure the DataSource (note that these configuration
		// parameters will vary, depending on the database and type of JDBC
		// drivers being used)
		xads.setDatabaseName(DATABASE_NAME);
		xads.setUser(USER_NAME);
		xads.setPassword(PASSWORD);

		// get a connection to the database
		System.out.println("  Getting a Connection to " + DATABASE_NAME + " from MQQueueManager "
				+ QM_NAME);
		theDatabaseConnection = theQueueManager.getJDBCConnection(xads);

		// disable JDBC autocommit
		theDatabaseConnection.setAutoCommit(false);

		// print database connection MetaData
		DatabaseMetaData md = theDatabaseConnection.getMetaData();
		System.out.println("  Connected to: ");
		System.out.println("   driver name: " + md.getDriverName());
		System.out.println("   product    : " + md.getDatabaseProductName() + ", version "
				+ md.getDatabaseProductVersion());
		System.out.println("   URL        : " + md.getURL());

		// all done
		System.out.println("Initialization complete");
	}

	/**
	 * This method uses the MQQueueManager and JDBC XAConnection objects created
	 * by the initialize() method to insert data into the database and send a
	 * message to a WebSphere MQ Queue. If both operations succeed, then the
	 * transaction is committed, otherwise it is backed out.
	 */
	public static void runTransaction() {

		try {
			// begin a new unit of work
			theQueueManager.begin();

			// create a java.sql.PreparedStatement to use
			PreparedStatement stmt = theDatabaseConnection
					.prepareStatement("INSERT INTO  EXAMPLE VALUES (?,?)");
			// update the time
			long time = System.currentTimeMillis();
			stmt.setLong(1, time);
			// create the message
			String msg = "A test message sent on date " + new Date(time);
			stmt.setString(2, msg);
			// execute the statement
			System.out.println("  Executing database INSERT");
			stmt.execute();
			System.out.println("  Database updated successfully");
			// and close it
			stmt.close();

			// send a message (this will throw an exception if the send fails)
			System.out.println("  Sending a message to queue:" + QUEUE_NAME);
			sendMessage(msg);
			System.out.println("  Message sent successfully, committing the transaction");
			// message sent ok, so commit the transaction
			theQueueManager.commit();
			System.out.println("  Transaction committed successfully");
		}
		catch (Exception e) {
			// send failed, so backout the transaction
			System.out.println("  **FAILURE** the application will backout due to the exception: ");
			e.printStackTrace();
			try {
				theQueueManager.backout();
			}
			catch (MQException me) {
				// if the backout call fails, the unit of work will usually be
				// backed out automatically (see MQBACK in the WebSphere MQ
				// Application Programming Reference for more detail)
				System.out.println("  **WARNING** attempt to backout failed with exception: ");
				me.printStackTrace();
			}
		}
		finally {
			// disconnect from the MQQueueManager and close the JDBC XAConnection
			System.out.println("  Disconnecting resources ");
			try {
				// disconnect from the queue manager
				theQueueManager.disconnect();
				// close the connection
				theDatabaseConnection.close();
			}
			catch (Exception e) {
				// warn the user of the failure
				System.out.println("  **WARNING** attempt to disconnect failed with exception: ");
				e.printStackTrace();				
			}
		}
	}

	/**
	 * This method sends a message to a specified WebSphere MQ queue. In the event of a failure, 
	 * an Exception will be thrown, causing the transaction to be backed out. 
	 * 
	 * @param text the contents of the message
	 * @throws Exception if a failure occurs sending the message
	 */
	private static void sendMessage(String text) throws Exception {

		// open the MQQueue 
		MQQueue queue = theQueueManager.accessQueue(QUEUE_NAME, MQC.MQOO_INPUT_AS_Q_DEF
				| MQC.MQOO_OUTPUT, null, null, null);

		// create a new MQMessage
		MQMessage msg = new MQMessage();
		// add the text to the mesasge
		msg.writeUTF(text);
		// and put it to the queue
		MQPutMessageOptions pmo = new MQPutMessageOptions();
		pmo.options += MQC.MQPMO_SYNCPOINT;
		queue.put(msg, pmo);
	}
}