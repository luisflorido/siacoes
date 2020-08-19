package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils.NullType;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO {
	
	private Connection conn;
	private DatabaseUtils utils;

	public BugReportDAO() {
		try {
			this.conn = ConnectionDAO.getInstance().getConnection();
		} catch(Exception e) {
			System.out.println("Erro ao obter conexão.");
		}
		this.utils = new DatabaseUtils(this.conn);
	}

	public BugReport findById(int id) throws SQLException{
		ResultSet rs = utils.prepareStatement("SELECT bugreport.*, \"user\".name " + 
		"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
		"WHERE idBugReport = ?", false, id);
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<BugReport> listAll() throws SQLException{
		ResultSet rs = utils.executeQuery("SELECT bugreport.*, \"user\".name " +
		"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
		"ORDER BY status, reportdate");

		List<BugReport> list = new ArrayList<BugReport>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);
		
		if(insert){
			ResultSet rs = utils.prepareStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", true, bug.getUser().getIdUser(), bug.getModule().getValue(), bug.getTitle(), bug.getDescription(), new java.sql.Date(bug.getReportDate().getTime()), bug.getType().getValue(), bug.getStatus().getValue(), bug.getStatus() == BugStatus.REPORTED ? Types.DATE : new java.sql.Date(bug.getStatusDate().getTime()), bug.getStatusDescription());
			if(rs.next()){
				bug.setIdBugReport(rs.getInt(1));
			}
		}else{
			utils.prepareStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?", false, bug.getUser().getIdUser(), bug.getModule().getValue(), bug.getTitle(), bug.getDescription(), new java.sql.Date(bug.getReportDate().getTime()), bug.getType().getValue(), bug.getStatus().getValue(), bug.getStatus() == BugStatus.REPORTED ? NullType.DATE : new java.sql.Date(bug.getStatusDate().getTime()), bug.getStatusDescription(), bug.getIdBugReport());
		}
		
		return bug.getIdBugReport();
	}
	
	private BugReport loadObject(ResultSet rs) throws SQLException{
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
