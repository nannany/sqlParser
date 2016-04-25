package sqlParse;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;

public class InequalitySignFinder implements SelectVisitor, ExpressionVisitor, FromItemVisitor {

    private List<String> items;

    public List<String> getInequalitySign(Select select) {
	init();
	select.getSelectBody().accept(this);
	return items;
    }

    public List<String> getInequalitySign(Update update) {
	init();
	// update.getFromItem().accept(this);
	update.getWhere().accept(this);
	return items;
    }

    public List<String> getInequalitySign(Delete delete) {
	init();
	delete.getWhere().accept(this);
	return items;
    }

    private void init() {
	items = new ArrayList<String>();
    }

    // 左と右を見に行く
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
	binaryExpression.getLeftExpression().accept(this);
	binaryExpression.getRightExpression().accept(this);
    }

    public void visit(Parenthesis paramParenthesis) {
	paramParenthesis.getExpression().accept(this);
    }

    public void visit(AndExpression paramAndExpression) {
	visitBinaryExpression(paramAndExpression);
    }

    public void visit(OrExpression paramOrExpression) {
	visitBinaryExpression(paramOrExpression);
    }

    public void visit(EqualsTo paramEqualsTo) {
	visitBinaryExpression(paramEqualsTo);
    }

    public void visit(GreaterThan paramGreaterThan) {
	items.add(paramGreaterThan.toString());
    }

    public void visit(GreaterThanEquals paramGreaterThanEquals) {
	items.add(paramGreaterThanEquals.toString());
    }

    // ＜をみてreturnに加える
    public void visit(MinorThan paramMinorThan) {
	items.add(paramMinorThan.toString());
    }

    // ＜＝をみてreturnに加える
    public void visit(MinorThanEquals paramMinorThanEquals) {
	items.add(paramMinorThanEquals.toString());
    }

    // <>があったら、その両辺をみにいく
    public void visit(NotEqualsTo paramNotEqualsTo) {
	visitBinaryExpression(paramNotEqualsTo);
    }

    public void visit(SubSelect paramSubSelect) {
	paramSubSelect.getSelectBody().accept(this);
    }

    // FROM句とWHERE句を再帰的にみる
    public void visit(PlainSelect paramPlainSelect) {
	paramPlainSelect.getFromItem().accept(this);
	if (paramPlainSelect.getWhere() != null) {
	    paramPlainSelect.getWhere().accept(this);
	}
    }

    public void visit(Table paramTable) {

    }

    public void visit(SubJoin paramSubJoin) {

    }

    public void visit(LateralSubSelect paramLateralSubSelect) {

    }

    public void visit(ValuesList paramValuesList) {

    }

    public void visit(NullValue paramNullValue) {

    }

    public void visit(Function paramFunction) {

    }

    public void visit(SignedExpression paramSignedExpression) {

    }

    public void visit(JdbcParameter paramJdbcParameter) {

    }

    public void visit(JdbcNamedParameter paramJdbcNamedParameter) {

    }

    public void visit(DoubleValue paramDoubleValue) {

    }

    public void visit(LongValue paramLongValue) {

    }

    public void visit(DateValue paramDateValue) {

    }

    public void visit(TimeValue paramTimeValue) {

    }

    public void visit(TimestampValue paramTimestampValue) {

    }

    public void visit(StringValue paramStringValue) {

    }

    public void visit(Addition paramAddition) {

    }

    public void visit(Division paramDivision) {

    }

    public void visit(Multiplication paramMultiplication) {

    }

    public void visit(Subtraction paramSubtraction) {

    }

    public void visit(Between paramBetween) {

    }

    public void visit(InExpression paramInExpression) {

    }

    public void visit(IsNullExpression paramIsNullExpression) {

    }

    public void visit(LikeExpression paramLikeExpression) {

    }

    public void visit(Column paramColumn) {

    }

    public void visit(CaseExpression paramCaseExpression) {

    }

    public void visit(WhenClause paramWhenClause) {

    }

    public void visit(ExistsExpression paramExistsExpression) {

    }

    public void visit(AllComparisonExpression paramAllComparisonExpression) {

    }

    public void visit(AnyComparisonExpression paramAnyComparisonExpression) {

    }

    public void visit(Concat paramConcat) {

    }

    public void visit(Matches paramMatches) {

    }

    public void visit(BitwiseAnd paramBitwiseAnd) {

    }

    public void visit(BitwiseOr paramBitwiseOr) {

    }

    public void visit(BitwiseXor paramBitwiseXor) {

    }

    public void visit(CastExpression paramCastExpression) {

    }

    public void visit(Modulo paramModulo) {

    }

    public void visit(AnalyticExpression paramAnalyticExpression) {

    }

    public void visit(ExtractExpression paramExtractExpression) {

    }

    public void visit(IntervalExpression paramIntervalExpression) {

    }

    public void visit(OracleHierarchicalExpression paramOracleHierarchicalExpression) {

    }

    public void visit(RegExpMatchOperator paramRegExpMatchOperator) {

    }

    public void visit(SetOperationList paramSetOperationList) {

    }

    public void visit(WithItem paramWithItem) {

    }

}
