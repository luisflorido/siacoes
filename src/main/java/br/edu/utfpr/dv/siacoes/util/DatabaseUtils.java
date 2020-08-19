package br.edu.utfpr.dv.siacoes.util;

/**
 * Classe criada para centralizar execuções SQL a fim de diminuir repetição de código e reunir essas transações para que se haja algum erro, será na lógica e não nos métodos do DAO.
 * 
 */

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.bytecode.ByteArray;

public class DatabaseUtils {

    private Connection con;

    public DatabaseUtils(Connection con) {
        this.con = con;
    }

    public ResultSet executeQuery(String query) throws SQLException {
		if (!Objects.isNull(con)) {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
                stmt.close();
            return rs;
		} 
		return null;
    }
    
    public ResultSet prepareStatement(String query, Boolean withKeys, Object... params) throws SQLException {
        if (!Objects.isNull(con)) {
			PreparedStatement stmt = con.prepareStatement(query, withKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
            
            AtomicInteger i = new AtomicInteger(1);
            Arrays.stream(params).forEach(e -> {
                int x = i.get();
                try{
                    if (e.getClass() == Integer.class) {
                        stmt.setInt(x, (int) e);
                    } else if (e.getClass() == String.class) {
                        stmt.setString(x, (String) e);
                    } else if (e.getClass() == Byte.class) {
                        stmt.setByte(x, (byte) e);
                    } else if (e.getClass() == ByteArray.class) {
                        stmt.setBytes(x, (byte[]) e);
                    } else if (e.getClass() == Date.class) {
                        stmt.setDate(x, (Date) e);
                    } else if (e.getClass() == NullType.class) {
                        NullType type = (NullType) e;
                        if (type.equals(NullType.BINARY)) {
                            stmt.setNull(x, Types.BINARY);
                        } else if (type.equals(NullType.DATE)) {
                            stmt.setNull(x, Types.DATE);
                        }
                    }
                }catch(Exception er) {
                }
                i.set(x + 1);
            });

            ResultSet rs = stmt.executeQuery();
            
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
                stmt.close();
            return rs;
		} 
		return null;
    }
    
    public enum NullType {
        BINARY,
        DATE
    };
}
