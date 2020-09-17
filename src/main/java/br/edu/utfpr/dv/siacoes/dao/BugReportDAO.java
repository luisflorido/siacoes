package br.edu.utfpr.dv.siacoes.dao;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BugReportDAO extends DatabaseUtils {

    public BugReport findById(int id) throws SQLException {
        ResultSet rs = this.prepareStatement("SELECT bugreport.*, \"user\".name " +
                "FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
                "WHERE idBugReport = ?", false, stmt -> {
            stmt.setInt(1, id);
        });

        if (rs.next()) {
            return this.loadObject(rs);
        } else {
            return null;
        }
    }

    public List<BugReport> listAll() throws SQLException {
        ResultSet rs = this.executeQuery("SELECT bugreport.*, \"user\".name " +
                "FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
                "ORDER BY status, reportdate");

        List<BugReport> list = new ArrayList<BugReport>();

        while (rs.next()) {
            list.add(this.loadObject(rs));
        }

        return list;
    }

    public int save(BugReport bug) throws SQLException {
        boolean insert = (bug.getIdBugReport() == 0);

        ResultSet rs = this.prepareStatement(insert ? "INSERT INTO bugreport(idUser, module, title, description, " +
                        "reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        : "UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, " +
                        "type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?",
                true, stmt -> {
                    stmt.setInt(1, bug.getUser().getIdUser());
                    stmt.setInt(2, bug.getModule().getValue());
                    stmt.setString(3, bug.getTitle());
                    stmt.setString(4, bug.getDescription());
                    stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
                    stmt.setInt(6, bug.getType().getValue());
                    stmt.setInt(7, bug.getStatus().getValue());
                    if (bug.getStatus() == BugStatus.REPORTED) {
                        stmt.setNull(8, Types.DATE);
                    } else {
                        stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
                    }
                    stmt.setString(9, bug.getStatusDescription());

                    if (!insert) {
                        stmt.setInt(10, bug.getIdBugReport());
                    }
                });
        if (rs.next()) {
            bug.setIdBugReport(rs.getInt(1));
        }

        return bug.getIdBugReport();
    }

    private BugReport loadObject(ResultSet rs) throws SQLException {
        BugReport bug = new BugReport();

        bug.setIdBugReport(rs.getInt("idBugReport"));
        bug.setUser(new User());
        bug.getUser().setIdUser(rs.getInt("idUser"));
        bug.getUser().setName(rs.getString("name"));
        bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
        bug.setTitle(rs.getString("title"));
        bug.setDescription(rs.getString("description"));
        bug.setReportDate(rs.getDate("reportDate"));
        bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
        bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
        bug.setStatusDate(rs.getDate("statusDate"));
        bug.setStatusDescription(rs.getString("statusDescription"));

        return bug;
    }

}
