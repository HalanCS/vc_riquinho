package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
	private static final String URL = "jdbc:mysql://localhost:3306/vcRiquinhoDB";
	private static final String USER = "root";
	private static final String PASSWORD = "Hacker_hds16";
	
	protected Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		
		// Mensagem de depuração para certificar que a conexão deu certo
		if (conn != null) {
			System.out.println("SUCESSO: Conexão com o banco 'vcRiquinhoDB' estabelecida com êxito!");
		}
		
		return conn;
	}
}
