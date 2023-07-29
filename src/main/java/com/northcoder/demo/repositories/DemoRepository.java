package com.northcoder.demo.repositories;

import com.northcoder.demo.entities.Employee;
import com.northcoder.demo.services.request.SqlQuery;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends CrudRepository<Employee, Long> {

    public long countTable();

    public long countFiltered(SqlQuery sqlQuery);

    public List<Employee> getCurrentPage(SqlQuery sqlQuery);
}
