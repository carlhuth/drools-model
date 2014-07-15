package org.drools.model.flow;

import org.drools.model.Condition;
import org.drools.model.DSL;
import org.drools.model.DataSource;
import org.drools.model.ExistentialPattern;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.functions.Function0;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;

import java.util.List;

import static org.drools.model.impl.ViewBuilder.viewItems2Conditions;

public class FlowDSL {

    public static View view(ViewItem... viewItems) {
        List<Condition> conditions = viewItems2Conditions(viewItems);
        return DSL.view(conditions.toArray(new Condition[conditions.size()]));
    }

    public static <T> ViewItem input(Variable<T> var, Function0<DataSource<T>> provider) {
        return new InputViewItem(var, provider);
    }

    public static <T> ExprViewItem expr(Variable<T> var, Predicate1<T> predicate) {
        return new Expr1ViewItem<T>(var, predicate);
    }

    public static <T, U> ExprViewItem expr(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return new Expr2ViewItem<T, U>(var1, var2, predicate);
    }

    public static <T> ExprViewItem not(ExprViewItem expr) {
        return expr.setExistentialType(ExistentialPattern.ExistentialType.NOT);
    }

    public static <T> ExprViewItem not(Variable<T> var, Predicate1<T> predicate) {
        return not(new Expr1ViewItem<T>(var, predicate));
    }

    public static <T, U> ExprViewItem not(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return not(new Expr2ViewItem<T, U>(var1, var2, predicate));
    }

    public static <T> ExprViewItem exists(ExprViewItem expr) {
        return expr.setExistentialType(ExistentialPattern.ExistentialType.EXISTS);
    }

    public static <T> ExprViewItem exists(Variable<T> var, Predicate1<T> predicate) {
        return exists(new Expr1ViewItem<T>(var, predicate));
    }

    public static <T, U> ExprViewItem exists(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return exists(new Expr2ViewItem<T, U>(var1, var2, predicate));
    }

    public static ExprViewItem or(ExprViewItem... expressions) {
        return new CombinedExprViewItem(Condition.OrType.INSTANCE, expressions);
    }

    public static ExprViewItem and(ExprViewItem... expressions) {
        return new CombinedExprViewItem(Condition.AndType.INSTANCE, expressions);
    }
}
