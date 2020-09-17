package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils;

public class ActivityUnitDAO extends DatabaseUtils {

    private Connection conn;

    public ActivityUnitDAO() {
        try {
            this.conn = ConnectionDAO.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println("Erro ao obter conexão.");
        }
    }

    public List<ActivityUnit> listAll() throws SQLException {
        ResultSet rs = this.executeQuery("SELECT * FROM activityunit ORDER BY description");

        List<ActivityUnit> list = new ArrayList<ActivityUnit>();

        while (rs.next()) {
            list.add(this.loadObject(rs));
        }

        return list;
    }

    public ActivityUnit findById(int id) throws SQLException {
        ResultSet rs = this.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?", false, stmt -> {
            stmt.setInt(1, id);
        });

        if (rs.next()) {
            return this.loadObject(rs);
        } else {
            return null;
        }
    }

    public int save(int idUser, ActivityUnit unit) throws SQLException {
        boolean insert = (unit.getIdActivityUnit() == 0);
        ResultSet rs = this.prepareStatement(insert ? "INSERT INTO activityunit(description, fillAmount, " +
                "amountDescription) VALUES(?, ?, ?)" : "UPDATE activityunit SET description=?, fillAmount=?, " +
                "amountDescription=? WHERE " +
                "idActivityUnit=?", true, stmt -> {
            stmt.setString(1, unit.getDescription());
            stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
            stmt.setString(3, unit.getAmountDescription());

            if (!insert) {
                stmt.setInt(4, unit.getIdActivityUnit());
            }
        });
        if (rs.next()) {
            unit.setIdActivityUnit(rs.getInt(1));
        }

        if (insert) {
            if (rs.next()) {
                unit.setIdActivityUnit(rs.getInt(1));
            }

            new UpdateEvent(this.conn).registerInsert(idUser, unit);
        } else {
            new UpdateEvent(this.conn).registerUpdate(idUser, unit);
        }


        return unit.getIdActivityUnit();
    }

    private ActivityUnit loadObject(ResultSet rs) throws SQLException {
        ActivityUnit unit = new ActivityUnit();

        unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
        unit.setDescription(rs.getString("Description"));
        unit.setFillAmount(rs.getInt("fillAmount") == 1);
        unit.setAmountDescription(rs.getString("amountDescription"));

        return unit;
    }

}
