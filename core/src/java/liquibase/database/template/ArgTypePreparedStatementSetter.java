package liquibase.database.template;

import liquibase.exception.JDBCException;

import java.util.Collection;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Simple adapter for PreparedStatementSetter that applies
 * given arrays of arguments and JDBC argument types.
 *
 * @author Juergen Hoeller
 */
class ArgTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

	private final Object[] args;

	private final int[] argTypes;


	/**
	 * Create a new ArgTypePreparedStatementSetter for the given arguments.
	 * @param args the arguments to set
	 * @param argTypes the corresponding SQL types of the arguments
	 */
	public ArgTypePreparedStatementSetter(Object[] args, int[] argTypes) throws JDBCException {
		if ((args != null && argTypes == null) || (args == null && argTypes != null) ||
				(args != null && args.length != argTypes.length)) {
			throw new JDBCException("args and argTypes parameters must match");
		}
		this.args = args;
		this.argTypes = argTypes;
	}


	public void setValues(PreparedStatement ps) throws SQLException {
		int argIndx = 1;
		if (this.args != null) {
			for (int i = 0; i < this.args.length; i++) {
				Object arg = this.args[i];
				if (arg instanceof Collection && this.argTypes[i] != Types.ARRAY) {
					Collection entries = (Collection) arg;
					for (Iterator it = entries.iterator(); it.hasNext();) {
						Object entry = it.next();
						StatementCreatorUtils.setParameterValue(ps, argIndx++, this.argTypes[i], entry);
					}
				}
				else {
					StatementCreatorUtils.setParameterValue(ps, argIndx++, this.argTypes[i], arg);
				}
			}
		}
	}

	public void cleanupParameters() {
		StatementCreatorUtils.cleanupParameters(this.args);
	}

}
