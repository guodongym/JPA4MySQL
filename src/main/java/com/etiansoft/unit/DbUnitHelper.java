package com.etiansoft.unit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.XmlProducer;
import org.dbunit.ext.oracle.OracleConnection;
import org.dbunit.operation.DatabaseOperation;
import org.xml.sax.InputSource;

public class DbUnitHelper {

	@SuppressWarnings("deprecation")
	public static void exportData(IDatabaseConnection connection, String fileName, Set<String> tableNames, Map<String, String> sqls, boolean streamed, boolean flat) throws SQLException, DataSetException, IOException {
		connection.getConfig().setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		if (streamed) {
			connection.getConfig().setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, new ForwardOnlyResultSetTableFactory());
		}

		QueryDataSet dataSet = new QueryDataSet(connection);

		if (null != tableNames) {
			for (Iterator<String> it = tableNames.iterator(); it.hasNext();) {
				dataSet.addTable((String) it.next());
			}
		}

		if (null != sqls) {
			Map<String, String> hm = sqls;
			for (Map.Entry<String, String> m : hm.entrySet()) {
				dataSet.addTable(m.getKey(), m.getValue());
			}
		}

		if (flat) {
			FlatXmlDataSet.write(dataSet, new FileOutputStream(fileName));
		} else {
			XmlDataSet.write(dataSet, new FileOutputStream(fileName));
		}
	}

	public static void exportDatabase(IDatabaseConnection connection, String fileName, boolean streamed, boolean flat) throws SQLException, DatabaseUnitException, IOException {

		if (streamed) {
			connection.getConfig().setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, new ForwardOnlyResultSetTableFactory());
		}

		IDataSet dataSet = connection.createDataSet();
		if (flat) {
			FlatXmlDataSet.write(dataSet, new FileOutputStream(fileName));
		} else {
			XmlDataSet.write(dataSet, new FileOutputStream(fileName));
		}
	}

	public static void refreshDatabase(IDatabaseConnection connection, String input, boolean streamed, boolean flat) throws DatabaseUnitException, SQLException {
		IDataSetProducer producer;
		IDataSet dataSet;
		if (flat) {
			producer = new FlatXmlProducer(new InputSource(input));
		} else {
			producer = new XmlProducer(new InputSource(input));
		}
		if (streamed) {
			dataSet = new StreamingDataSet(producer);
		} else {
			dataSet = new CachedDataSet(producer);
		}

		DatabaseOperation.REFRESH.execute(connection, dataSet);
	}

	public static void cleanImportDatabase(IDatabaseConnection dbConnection, String input, boolean flat) throws DatabaseUnitException, SQLException, IOException {
		IDataSetProducer producer;
		IDataSet dataSet;

		input = "src/test/java/" + input;

		if (flat) {
			producer = new FlatXmlProducer(new InputSource(input));
		} else {
			producer = new XmlProducer(new InputSource(input));
		}
		dataSet = new CachedDataSet(producer);
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}

	/**
	 * �����ݼ��ļ�������ԭ��ݿ����ݡ�
	 * 
	 * @param dbConnection
	 * @param input
	 * @param flat
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void backupDatabase(IDatabaseConnection dbConnection, String input, boolean flat) throws DatabaseUnitException, SQLException, IOException {
		IDataSetProducer producer;
		IDataSet dataSet;

		input = "src/test/java/" + input;
		if (flat) {
			producer = new FlatXmlProducer(new InputSource(input));
		} else {
			producer = new XmlProducer(new InputSource(input));
		}
		dataSet = new CachedDataSet(producer);

		String tableNames[] = dataSet.getTableNames();

		Set<String> tableNameSet = new HashSet<String>();
		for (String tableName : tableNames) {
			tableNameSet.add(tableName);
		}
		Map<String, String> sqls = new HashMap<String, String>();

		String backupFileName = input + "_backup.xml";
		exportData(dbConnection, backupFileName, tableNameSet, sqls, false, true);
	}

	public static void restoreDatabase(IDatabaseConnection dbConnection, String input, boolean flat) throws DatabaseUnitException, SQLException, IOException {
		IDataSetProducer producer;
		IDataSet dataSet;

		input = "src/test/java/" + input;
		if (flat) {
			producer = new FlatXmlProducer(new InputSource(input));
		} else {
			producer = new XmlProducer(new InputSource(input));
		}
		dataSet = new CachedDataSet(producer);
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}

	public static IDatabaseConnection getDatabaseConnection() throws Exception {
		Connection connnection = getJdbcConnection();
		IDatabaseConnection dbConnection = new OracleConnection(connnection, "ZHOUP");
		return dbConnection;
	}

	public static Connection getJdbcConnection() throws Exception {
		Properties props = new Properties();
		props.load(ClassLoader.getSystemClassLoader().getResourceAsStream("database.properties"));
		Class.forName(props.getProperty("oracle.jdbc.driverClass"));
		return DriverManager.getConnection(props.getProperty("oracle.jdbc.url"), props.getProperty("user"), props.getProperty("password"));
	}

	public static ArrayList<String> getTables(Connection connection, String databaseName) throws Exception {
		ArrayList<String> tableNames = new ArrayList<String>();
		DatabaseMetaData dbMetaData = connection.getMetaData();
		String[] types = { "TABLE" }; // {"TABLE","VIEW"};
		ResultSet rs = dbMetaData.getTables(null, databaseName, "%", types);
		while (rs.next()) {
			tableNames.add(rs.getString(3));
		}
		return tableNames;
	}

	public static void exportDatabaseSeperate(IDatabaseConnection dbConnection, String dirName, boolean streamed, boolean flat, String databaseName) throws Exception {

		ArrayList<String> tableNameList = getTables(dbConnection.getConnection(), databaseName);

		for (String eachOne : tableNameList) {
			Set<String> tableNames = new HashSet<String>();
			tableNames.add(eachOne);
			exportData(dbConnection, dirName + eachOne + ".xml", tableNames, null, false, true);
		}
	}

	private static void testExportSingleTable() throws Exception {
		IDatabaseConnection dbConnection = getDatabaseConnection();

		Set<String> tableNames = new HashSet<String>();
		tableNames.add("SC_PERMISSION");

		Map<String, String> sqls = new HashMap<String, String>();
		// sqls.put("AC_ACCOUNT_AFFAIR", " select * from AC_ACCOUNT_AFFAIR where AFFAIR_ID='19577' ");

		sqls.put("SC_USER", " select * from SC_USER WHERE USER_CODE = 'trader'");
		sqls.put("SC_USER_ROLE", " select * from SC_USER_ROLE WHERE USER_CODE = 'trader'");
		sqls.put("SC_ROLE", " select r.* from SC_ROLE r left join SC_USER_ROLE ur ON UR.ROLE_ID = R.ROLE_ID WHERE ur.USER_CODE = 'trader'");
		sqls.put("SC_ROLE_PERMISSION", " select rp.* from SC_ROLE_PERMISSION RP left join SC_USER_ROLE ur ON UR.ROLE_ID = RP.ROLE_ID WHERE ur.USER_CODE = 'trader'");

		exportData(dbConnection, "D:\\1.xml", tableNames, sqls, false, true);
		System.out.println("finished!");
	}

	public static void main(String[] args) throws Exception {
		testExportSingleTable();
	}
}
