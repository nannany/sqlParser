package sqlParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

//余計なコメントはカットしたバージョン
public class SqlParser {
    public static void main(String[] args) {

	try {
	    // String tes = System.getProperty("user.dir");
	    File file = new File("./src/main/java/sql.txt");
	    FileReader is = new FileReader(file);

	    BufferedReader br = new BufferedReader(is);

	    String str;
	    String sql = "";
	    while ((str = br.readLine()) != null) {
		sql += str + "\r\n";
	    }
	    parse(sql);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * パースを行うメソッド 引数はsql
     */
    private final static void parse(String sql) {
	try {
	    // statementに引数のパース結果を格納
	    CCJSqlParserManager pm = new CCJSqlParserManager();
	    net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sql));
	    // SELECT文の解析
	    if (statement instanceof Select) {
		Select selectStatement = (Select) statement;
		// 不等号の結果を表示
		InequalitySignFinder inequalitySignFinder = new InequalitySignFinder();
		List inequalityList = inequalitySignFinder.getInequalitySign(selectStatement);
		for (int i = 0; i < inequalityList.size(); i++) {
		    System.out.println(inequalityList.get(i));
		}
	    }
	    // UPDATE文の解析
	    if (statement instanceof Update) {
		Update updateStatement = (Update) statement;
		// 不等号の結果を表示
		InequalitySignFinder inequalitySignFinder = new InequalitySignFinder();
		List inequalityList = inequalitySignFinder.getInequalitySign(updateStatement);
		for (int i = 0; i < inequalityList.size(); i++) {
		    System.out.println(inequalityList.get(i));
		}
	    }
	    // DELETE分の解析
	    if (statement instanceof Delete) {
		Delete deleteStatement = (Delete) statement;
		// 不等号の結果を表示
		InequalitySignFinder inequalitySignFinder = new InequalitySignFinder();
		List inequalityList = inequalitySignFinder.getInequalitySign(deleteStatement);
		for (int i = 0; i < inequalityList.size(); i++) {
		    System.out.println(inequalityList.get(i));
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}