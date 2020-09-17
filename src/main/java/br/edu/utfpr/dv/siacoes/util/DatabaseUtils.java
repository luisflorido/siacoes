package br.edu.utfpr.dv.siacoes.util;

/**
 * Classe criada para centralizar execuções SQL a fim de diminuir repetição de código e reunir essas transações para que se haja algum erro, será na lógica e não nos métodos do DAO.
 */

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;

import java.sql.*;
import java.util.Objects;

public abstract class DatabaseUtils {

    private Connection con;

    private void openConnection() {
        try {
            this.con = ConnectionDAO.getInstance().getConnection();
        } catch (SQLException err) {
            System.out.println("Erro ao abrir conexão!");
            err.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        openConnection();
        if (!Objects.isNull(con)) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if ((rs != null) && !rs.isClosed())
                rs.close();
            if (!stmt.isClosed())
                stmt.close();
            return rs;
        }
        return null;
    }

    public ResultSet prepareStatement(String query, Boolean withKeys,
                                      IPreparedStatement prepared) throws SQLException {
        openConnection();
        if (!Objects.isNull(con)) {
            PreparedStatement stmt = con.prepareStatement(query, withKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);

            prepared.setValues(stmt);
            ResultSet rs = stmt.executeQuery();

            if ((rs != null) && !rs.isClosed())
                rs.close();
            if (!stmt.isClosed())
                stmt.close();
            return rs;
        }
        return null;
    }

    public interface IPreparedStatement {
        void setValues(PreparedStatement stmt) throws SQLException;
    }
}
