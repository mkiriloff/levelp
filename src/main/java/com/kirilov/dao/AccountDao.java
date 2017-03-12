package com.kirilov.dao;

import com.kirilov.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("accountDao")
public class AccountDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public boolean add(Account account) throws DataAccessException {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(account);
        return jdbcTemplate.update(
                "insert into accounts (firstname, lastname) values (:firstname, :lastname)",
                parameterSource) == 1;
    }

    public boolean delete(int id) throws DataAccessException {
        return jdbcTemplate.update(
                "delete from accounts where id=:id",
                new MapSqlParameterSource("id", id)) == 1;
    }

    public Account findbyId(int id) throws DataAccessException {
        return jdbcTemplate.queryForObject(
                "select * from accounts where id=:id",
                new MapSqlParameterSource().addValue("id", id),
                new RowMapper<Account>() {
                    public Account mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        Account account = new Account();
                        account.setid(resultSet.getInt("id"));
                        account.setFirstname(resultSet.getString("firstname"));
                        account.setLastname(resultSet.getString("lastname"));
                        account.setBalance(resultSet.getInt("balance"));
                        return account;
                    }
                });
    }

    public boolean updateBalance(Account account) throws DataAccessException {
        return jdbcTemplate.update(
                "update accounts set balance=:balance where id=:id",
                new BeanPropertySqlParameterSource(account)) == 1;
    }

    public List<Account> getAllAccount() throws DataAccessException {
        return jdbcTemplate.query(
                "select * from accounts",
                BeanPropertyRowMapper.newInstance(Account.class));
    }
}