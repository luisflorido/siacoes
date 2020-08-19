package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils.NullType;

public class DepartmentDAO {

	private Connection conn;
	private DatabaseUtils utils;

	public DepartmentDAO() {
		try {
			this.conn = ConnectionDAO.getInstance().getConnection();
		} catch(Exception e) {
			System.out.println("Erro ao obter conexão.");
		}
		this.utils = new DatabaseUtils(this.conn);
	}

	public Department findById(int id) throws SQLException{
		ResultSet rs = utils.prepareStatement("SELECT department.*, campus.name AS campusName " +
		"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
		"WHERE idDepartment = ?", false, id);
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		ResultSet rs = utils.executeQuery("SELECT department.*, campus.name AS campusName " +
		"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
		(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
		
		List<Department> list = new ArrayList<Department>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		ResultSet rs = utils.executeQuery("SELECT department.*, campus.name AS campusName " +
		"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
		"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
		
		List<Department> list = new ArrayList<Department>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		
		if(insert){
			ResultSet rs = utils.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", true, department.getCampus().getIdCampus(), department.getName(), Objects.isNull(department.getLogo()) ? NullType.BINARY : department.getLogo(), department.isActive() ? 1 : 0, department.getSite(), department.getFullName(), department.getInitials());

			if(rs.next()){
				department.setIdDepartment(rs.getInt(1));
			}

			new UpdateEvent(conn).registerInsert(idUser, department);
		}else{
			utils.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?", false, department.getCampus().getIdCampus(), department.getName(), Objects.isNull(department.getLogo()) ? NullType.BINARY : department.getLogo(), department.isActive() ? 1 : 0, department.getSite(), department.getFullName(), department.getInitials(), department.getIdDepartment());
			new UpdateEvent(conn).registerUpdate(idUser, department);
		}
		
		return department.getIdDepartment();
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}
	
}
