package servlets;

import java.sql.*;
import javax.servlet.*;

import constants.IOnlineBookStoreConstants;
import sql.IUserContants;

import java.io.*;
import java.util.regex.Pattern;

public class UserRegisterServlet extends GenericServlet {

	// Define patterns to check each rule
	private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");  // At least 8 characters
	private static final Pattern UPPER_CASE_PATTERN = Pattern.compile(".*[A-Z].*");  // At least one uppercase
	private static final Pattern LOWER_CASE_PATTERN = Pattern.compile(".*[a-z].*");  // At least one lowercase
	private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");  // At least one digit
	private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");  // At least one special character


	public static boolean validatePassword(String password) {
		if (password == null) {
			return false;
		}
		boolean isLengthValid = LENGTH_PATTERN.matcher(password).matches();
		boolean hasUpperCase = UPPER_CASE_PATTERN.matcher(password).matches();
		boolean hasLowerCase = LOWER_CASE_PATTERN.matcher(password).matches();
		boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
		boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).matches();

		return isLengthValid && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
	}


	public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
		PrintWriter pw = res.getWriter();
		res.setContentType(IOnlineBookStoreConstants.CONTENT_TYPE_TEXT_HTML);

		String uName = req.getParameter(IUserContants.COLUMN_USERNAME);
		String pWord = req.getParameter(IUserContants.COLUMN_PASSWORD);
		String fName = req.getParameter(IUserContants.COLUMN_FIRSTNAME);
		String lName = req.getParameter(IUserContants.COLUMN_LASTNAME);
		String addr = req.getParameter(IUserContants.COLUMN_ADDRESS);
		String phNo = req.getParameter(IUserContants.COLUMN_PHONE);
		String mailId = req.getParameter(IUserContants.COLUMN_MAILID);

		if(validatePassword(pWord)) {

			try {
				Connection con = DBConnection.getCon();
				PreparedStatement ps = con
						.prepareStatement("insert into " + IUserContants.TABLE_USERS + "  values(?,?,?,?,?,?,?,?)");
				ps.setString(1, uName);
				ps.setString(2, pWord);
				ps.setString(3, fName);
				ps.setString(4, lName);
				ps.setString(5, addr);
				ps.setString(6, phNo);
				ps.setString(7, mailId);
				ps.setInt(8, 2);
				int k = ps.executeUpdate();
				if (k == 1) {
					RequestDispatcher rd = req.getRequestDispatcher("Sample.html");
					rd.include(req, res);
					pw.println("<h3 class='tab'>User Registered Successfully</h3>");
				} else {
					RequestDispatcher rd = req.getRequestDispatcher("userreg");
					rd.include(req, res);
					pw.println("Sorry for interruption! Register again");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			RequestDispatcher rd = req.getRequestDispatcher("userreg");
			rd.include(req, res);
			pw.println("Sorry for interruption! Register Failed due to not proper password");
		}
	}


}
