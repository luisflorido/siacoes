package br.edu.utfpr.dv.siacoes.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DepartmentTest {

    private final int ID = 1;
    private final String NAME = "NOME";

    @Test
    public void setIdDepartment() {
        Department d = new Department();
        d.setIdDepartment(ID);
        assertEquals(d.getIdDepartment(), ID);
    }

    @Test
    public void setName() {
        Department d = new Department();
        d.setName(NAME);
        assertEquals(d.getName(), NAME);
    }
}
