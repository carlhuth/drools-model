package org.drools.model.flow;

import org.drools.model.AccumulateFunction;
import org.drools.model.Condition;
import org.drools.model.DSL;
import org.drools.model.DataSource;
import org.drools.model.DataStore;
import org.drools.model.ExistentialPattern;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.functions.Function0;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;

import java.util.List;

import static org.drools.model.functions.FunctionUtils.toFunctionN;
import static org.drools.model.impl.ViewBuilder.viewItems2Conditions;

public class FlowDSL {

    public static View view(ViewItem... viewItems) {
        List<Condition> conditions = viewItems2Conditions(viewItems);
        return DSL.view(conditions.toArray(new Condition[conditions.size()]));
    }

    public static <T> ViewItem<T> input(Variable<T> var) {
        return input(var, new Function0<DataSource<T>>() {
            @Override
            public DataSource<T> apply() {
                return DataStore.EMPTY;
            }
        });
    }

    public static <T> ViewItem<T> input(Variable<T> var, Function0<DataSource<T>> provider) {
        return new InputViewItem(var, provider);
    }

    public static <T> ExprViewItem<T> expr(Variable<T> var, Predicate1<T> predicate) {
        return new Expr1ViewItem<T>(var, predicate);
    }

    public static <T, U> ExprViewItem<T> expr(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return new Expr2ViewItem<T, U>(var1, var2, predicate);
    }

    public static <T> ExprViewItem<T> expr(String exprId, Variable<T> var, Predicate1<T> predicate) {
        return new Expr1ViewItem<T>(exprId, var, predicate);
    }

    public static <T, U> ExprViewItem<T> expr(String exprId, Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return new Expr2ViewItem<T, U>(exprId, var1, var2, predicate);
    }

    public static <T> ExprViewItem<T> not(ExprViewItem expr) {
        return expr.setExistentialType(ExistentialPattern.ExistentialType.NOT);
    }

    public static <T> ExprViewItem<T> not(Variable<T> var) {
        return not(var, Predicate1.TRUE);
    }

    public static <T> ExprViewItem<T> not(Variable<T> var, Predicate1<T> predicate) {
        return not(new Expr1ViewItem<T>(var, predicate));
    }

    public static <T, U> ExprViewItem<T> not(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return not(new Expr2ViewItem<T, U>(var1, var2, predicate));
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var) {
        return exists(var, Predicate1.TRUE);
    }

    public static <T> ExprViewItem<T> exists(ExprViewItem<T> expr) {
        return expr.setExistentialType(ExistentialPattern.ExistentialType.EXISTS);
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var, Predicate1<T> predicate) {
        return exists(new Expr1ViewItem<T>(var, predicate));
    }

    public static <T, U> ExprViewItem<T> exists(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return exists(new Expr2ViewItem<T, U>(var1, var2, predicate));
    }

    public static <T> ExprViewItem<T> accumulate(ExprViewItem<T> expr, AccumulateFunction<T, ?, ?>... functions) {
        return new AccumulateExprViewItem(expr, functions);
    }

    public static ExprViewItem or(ExprViewItem... expressions) {
        return new CombinedExprViewItem(Condition.OrType.INSTANCE, expressions);
    }

    public static ExprViewItem and(ExprViewItem... expressions) {
        return new CombinedExprViewItem(Condition.AndType.INSTANCE, expressions);
    }

    public static <T> SetViewItemBuilder<T> set(Variable<T> var) {
        return new SetViewItemBuilder<T>(var);
    }

    public static class SetViewItemBuilder<T> {
        private final Variable<T> var;

        private SetViewItemBuilder(Variable<T> var) {
            this.var = var;
        }

        public SetViewItem<T> invoking(Function0<T> f) {
            return new SetViewItem<T>(toFunctionN(f), false, var);
        }

        public <A> SetViewItem<T> invoking(Variable<A> var1, Function1<A, T> f) {
            return new SetViewItem<T>(toFunctionN(f), false, var, var1);
        }

        public <A, B> SetViewItem<T> invoking(Variable<A> var1, Variable<B> var2, Function2<A, B, T> f) {
            return new SetViewItem<T>(toFunctionN(f), false, var, var1, var2);
        }

        public SetViewItem<T> in(Function0<Iterable<? extends T>> f) {
            return new SetViewItem<T>(toFunctionN(f), true, var);
        }

        public <A> SetViewItem<T> in(Variable<A> var1, Function1<A, Iterable<? extends T>> f) {
            return new SetViewItem<T>(toFunctionN(f), true, var, var1);
        }

        public <A, B> SetViewItem<T> in(Variable<A> var1, Variable<B> var2, Function2<A, B, Iterable<? extends T>> f) {
            return new SetViewItem<T>(toFunctionN(f), true, var, var1, var2);
        }
    }
}
