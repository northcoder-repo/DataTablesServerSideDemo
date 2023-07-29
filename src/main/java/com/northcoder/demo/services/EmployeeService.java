package com.northcoder.demo.services;

import com.northcoder.demo.entities.Employee;
import com.northcoder.demo.repositories.DemoRepository;
import com.northcoder.demo.services.request.ServerSideRequest;
import com.northcoder.demo.services.request.SqlQuery;
import jakarta.inject.Inject;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final DemoRepository demoRepository;

    @Inject
    public EmployeeService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    public ResponseContainer getListing(ServerSideRequest ssr) {
        long tableCount = demoRepository.countTable();
        SqlQuery sqlQuery = new SqlQuery(ssr);
        long filteredCount = demoRepository.countFiltered(sqlQuery);
        List<Employee> currentPage = demoRepository.getCurrentPage(sqlQuery);
        return new ResponseContainer(ssr.draw(), tableCount, filteredCount, currentPage);
    }

    public List<String> getOffices() {
        return demoRepository.getOffices();
    }

}
