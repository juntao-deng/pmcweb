package net.juniper.jmp.monitor.ds;

import javax.sql.DataSource;

import net.juniper.jmp.persist.datasource.DataSourceProvider;

import org.apache.derby.jdbc.EmbeddedDataSource;

public class EmbededDataSourceProvider implements DataSourceProvider {
	private EmbeddedDataSource derbyDs;
	@Override
	public DataSource getDataSource(String name) {
		return getDataSource();
	}

	private DataSource getDataSource() {
		if(derbyDs == null){
			derbyDs = new EmbeddedDataSource();
			derbyDs.setCreateDatabase("create");
			derbyDs.setDataSourceName("MysqlDS");
			derbyDs.setDatabaseName("clientdb");
		}
		return derbyDs;
	}

}
