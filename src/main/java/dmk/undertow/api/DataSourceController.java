package dmk.undertow.api;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "데이터소스 사용법")
@RequestMapping("/api")
public class DataSourceController {

	Logger logger = LoggerFactory.getLogger(DataSourceController.class);

	@Autowired
	DataSource dataSource;

	@ApiOperation(value = "현재시간 조회", notes = "현재시간을 조회한다.")
	@RequestMapping(value = "/now", method = RequestMethod.GET, name = "현재시간을 조회")
	public String now(@ApiIgnore HttpSession session, @ApiIgnore HttpServletRequest req,
			@ApiIgnore HttpServletResponse res) throws Exception {

		String result = "";
		Connection conn = null;
		try {

			conn = dataSource.getConnection();

			if (conn == null)
				result = "Connection is null";
			else {
				ResultSet rs = conn.prepareStatement("select now()").executeQuery();
				while (rs.next()) {
					result = rs.getObject(1).toString();
				}

				rs.close();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (conn != null)
				conn.close();
		}

		return result;
	}
}
