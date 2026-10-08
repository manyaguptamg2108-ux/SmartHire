package service;

import dao.CompanyDAO;
import dao.CompanyDAOInterface;
import dao.RecruiterDAO;
import dao.RecruiterDAOInterface;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.Company;
import model.Recruiter;

import java.util.List;

/**
 * Service managing Company entities and recruiter association rules.
 */
public class CompanyService {

    private final CompanyDAOInterface companyDAO;
    private final RecruiterDAOInterface recruiterDAO;

    public CompanyService() {
        this.companyDAO = new CompanyDAO();
        this.recruiterDAO = new RecruiterDAO();
    }

    public CompanyService(CompanyDAOInterface companyDAO, RecruiterDAOInterface recruiterDAO) {
        this.companyDAO = companyDAO;
        this.recruiterDAO = recruiterDAO;
    }

    public boolean createCompany(Company company) throws InvalidUserException, DatabaseException {
        if (company == null) {
            throw new InvalidUserException("Company object cannot be null.");
        }
        if (company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            throw new InvalidUserException("Company name is required.");
        }
        Recruiter recruiter = recruiterDAO.findById(company.getRecruiterId());
        if (recruiter == null) {
            recruiter = recruiterDAO.findByUserId(company.getRecruiterId());
            if (recruiter == null) {
                throw new InvalidUserException("Associated Recruiter does not exist for ID: " + company.getRecruiterId());
            }
        }

        return companyDAO.save(company);
    }

    public Company getCompanyById(int id) throws DatabaseException {
        return companyDAO.findById(id);
    }

    public List<Company> getCompanyByRecruiterId(int recruiterId) throws DatabaseException {
        return companyDAO.findByRecruiterId(recruiterId);
    }

    public List<Company> getAllCompanies() throws DatabaseException {
        return companyDAO.findAll();
    }

    public boolean updateCompany(Company company) throws InvalidUserException, DatabaseException {
        if (company == null || company.getId() <= 0) {
            throw new InvalidUserException("Invalid Company for update.");
        }
        if (company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            throw new InvalidUserException("Company name is required.");
        }

        return companyDAO.update(company);
    }

    public boolean deleteCompany(int id) throws DatabaseException {
        return companyDAO.delete(id);
    }
}
