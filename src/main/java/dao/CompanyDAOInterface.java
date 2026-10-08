package dao;

import exception.DatabaseException;
import model.Company;
import java.util.List;

public interface CompanyDAOInterface extends GenericDAO<Company> {
    List<Company> findByRecruiterId(int recruiterId) throws DatabaseException;
}
