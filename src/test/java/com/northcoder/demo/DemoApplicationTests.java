package com.northcoder.demo;

import com.northcoder.demo.entities.Employee;
import com.northcoder.demo.services.ResponseContainer;
import com.northcoder.demo.services.request.Column;
import com.northcoder.demo.services.request.OrderCol;
import com.northcoder.demo.services.request.Search;
import com.northcoder.demo.services.request.ServerSideRequest;
import com.northcoder.demo.services.request.SqlQuery;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private DemoController demoController;

    //
    // @SpringBootTest loads the Spring app context, so we can do this:
    //
    @Test
    public void contextLoads() {
        assertThat(demoController).isNotNull();
    }

    //
    // Unit test:
    //
    @Test
    public void sqlStatement() {
        ServerSideRequest ssr = buildRequest();
        SqlQuery sqlQuery = new SqlQuery(ssr);
        String actualSql = sqlQuery.getSqlString();
        Object[] actualParams = sqlQuery.getParams();
        String expectedSql = "select name, position, office, age, start_date, salary "
                + "from datatables.employees "
                + "where (name like concat(?, '%') "
                + "or position like concat(?, '%') "
                + "or office like concat(?, '%') "
                + "or age like concat(?, '%') "
                + "or start_date like concat(?, '%') "
                + "or salary like concat(?, '%')) "
                + "and office = ? "
                + "order by 4 asc "
                + "limit 10 offset 0";
        Object[] expectedParams = {"t", "t", "t", "t", "t", "t", "Edinburgh"};

        assertThat(expectedSql).isEqualTo(actualSql);
        assertThat(expectedParams).containsExactly(actualParams);
    }

    //
    // INTEGRATION TEST:
    // This test assumes you have the test database set up and loaded.
    //
    @Test
    public void tableResults() {
        ServerSideRequest ssr = buildRequest();
        ResponseContainer response = demoController.postListing(ssr);
        assertThat(response.data()).size().isEqualTo(1);
        if (response.data().size() == 1) {
            Employee emp = response.data().get(0);
            assertThat(emp.name()).isEqualTo("Tiger Nixon");
        }
    }

    //
    // INTEGRATION TEST:
    // This test assumes you have the test database set up and loaded.
    //
    @Test
    public void officesListing() {
        Model model = new ExtendedModelMap();
        demoController.getListing(model);
        var officeObjects = (List<Object>) model.getAttribute("offices");
        assertThat(officeObjects).isNotNull();

        if (officeObjects != null) {
            List<String> actualOffices = officeObjects.stream()
                    .map(object -> Objects.toString(object, null))
                    .toList();
            List<String> expectedOffices = List.of(
                    "Edinburgh",
                    "London",
                    "New York",
                    "San Francisco",
                    "Singapore",
                    "Sydney",
                    "Tokyo"
            );
            assertThat(expectedOffices).containsExactlyElementsOf(actualOffices);
        }
    }

    private ServerSideRequest buildRequest() {
        Search noSearch = new Search(null, false);
        Search officeSearch = new Search("Edinburgh", false);

        Column nameCol = new Column("name", null, true, true, noSearch);
        Column positionCol = new Column("position", null, true, true, noSearch);
        Column officeCol = new Column("office", null, true, true, officeSearch);
        Column ageCol = new Column("age", null, true, true, noSearch);
        Column startDateCol = new Column("startDate", null, true, true, noSearch);
        Column salaryCol = new Column("salary", null, true, true, noSearch);
        List<Column> columns = List.of(nameCol, positionCol, officeCol, ageCol, startDateCol, salaryCol);

        OrderCol orderCol = new OrderCol(3, "asc");
        List<OrderCol> order = List.of(orderCol);

        Search search = new Search("t", false);

        return new ServerSideRequest(1, columns, order, 0, 10, search);
    }

}
