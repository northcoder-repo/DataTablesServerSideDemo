package com.northcoder.demo.repositories;

import com.northcoder.demo.entities.Employee;
import com.northcoder.demo.services.request.SqlQuery;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DemoRepositoryImpl implements DemoRepository {

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(DemoRepositoryImpl.class);

    private final String sqlCountAll = """
        select count(1)
        from datatables.employees
    """;

    @Inject
    public DemoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long countTable() {
        return jdbcTemplate.queryForObject(
                sqlCountAll,
                Long.TYPE
        );
    }

    @Override
    public long countFiltered(SqlQuery sqlQuery) {
        return jdbcTemplate.queryForObject(
                sqlQuery.getCountString(),
                sqlQuery.getParams(),
                sqlQuery.getParamTypes(),
                Long.TYPE
        );
    }

    @Override
    public List<Employee> getCurrentPage(SqlQuery sqlQuery) {
        RowMapper employeeMapper = DataClassRowMapper.newInstance(Employee.class);
        logger.info("SQL: " + sqlQuery.getSqlString() + " "
                + Arrays.toString(sqlQuery.getParams()));
        List<Employee> employees = jdbcTemplate.query(
                sqlQuery.getSqlString(),
                sqlQuery.getParams(),
                sqlQuery.getParamTypes(),
                employeeMapper
        );
        return employees;
    }

    //
    // -------------------------------
    // none of these is currently used:
    // -------------------------------
    //
    @Override
    public List<Employee> findAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <S extends Employee> S save(S entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <S extends Employee> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<Employee> findById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean existsById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<Employee> findAllById(Iterable<Long> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Employee entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAll(Iterable<? extends Employee> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
