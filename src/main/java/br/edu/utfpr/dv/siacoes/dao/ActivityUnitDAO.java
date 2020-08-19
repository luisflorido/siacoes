package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.util.DatabaseUtils;

public class ActivityUnitDAO {
	
	private Connection conn;
	private DatabaseUtils utils;

	public ActivityUnitDAO() {
		try {
			this.conn = ConnectionDAO.getInstance().getConnection();
		} catch(Exception e) {
			System.out.println("Erro ao obter conexão.");
		}
		this.utils = new DatabaseUtils(this.conn);
	}

	public List<ActivityUnit> listAll() throws SQLException{
		ResultSet rs = utils.executeQuery("SELECT * FROM activityunit ORDER BY description");
		
		List<ActivityUnit> list = new ArrayList<ActivityUnit>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public ActivityUnit findById(int id) throws SQLException{
		ResultSet rs = utils.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?", false, id);
		
		if (rs.next()){
			return this.loadObject(rs);
		} else {
			return null;
		}
	}
	
	public int save(int idUser, ActivityUnit unit) throws SQLException{
		boolean insert = (unit.getIdActivityUnit() == 0);
		if(insert){
			ResultSet rs = utils.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", true, unit.getDescription(), (unit.isFillAmount() ? 1 : 0), unit.getAmountDescription());
			if(rs.next()) {
				unit.setIdActivityUnit(rs.getInt(1));
			}
			new UpdateEvent(this.conn).registerInsert(idUser, unit);
		}else{
			utils.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?", false, unit.getDescription(), (unit.isFillAmount() ? 1 : 0), unit.getAmountDescription(), unit.getIdActivityUnit());
			new UpdateEvent(this.conn).registerUpdate(idUser, unit);
		}

		return unit.getIdActivityUnit();
	}
	
	private ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}
