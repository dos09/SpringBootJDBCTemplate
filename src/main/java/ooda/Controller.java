package ooda;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Autowired
	JdbcTemplate jdbcTemplate;
	private boolean tablePrepared = false;

	private void prepareTable() {
		List<Object[]> someCustomers = new ArrayList<>();
		someCustomers.add(new Object[] { "Ivan", "Ivanov" });
		someCustomers.add(new Object[] { "Gosho", "Goshev" });
		someCustomers.add(new Object[] { "Misho", "Mishev" });
		jdbcTemplate.execute("drop table if exists customers");
		jdbcTemplate.execute(
				"create table customers ( id int auto_increment, first_name varchar(50), last_name varchar(50), primary key (id) )");
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", someCustomers);
		tablePrepared = true;
	}

	@RequestMapping("/")
	public String testIt() {
		System.out.println("testIt()");
		if (!tablePrepared) {
			prepareTable();
		}
		jdbcTemplate
				.query("select id, first_name, last_name from customers", (rs, rowNum) -> new Customer(rs.getLong("id"),
						rs.getString("first_name"), rs.getString("last_name")))
				.forEach(customer -> System.out.println(customer));
		return "ok";
	}
}
