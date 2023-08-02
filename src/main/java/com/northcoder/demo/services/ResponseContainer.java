package com.northcoder.demo.services;

import com.northcoder.demo.entities.Employee;
import java.util.List;

// this models the returned data for a server-side DataTable, as
// defined here: https://datatables.net/manual/server-side#Returned-data
// TODO: add the optional error field also.
public record ResponseContainer(
        long draw,
        long recordsTotal,
        long recordsFiltered,
        List<Employee> data) {

}
