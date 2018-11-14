package sumTypeEncoding;

// Expr = Lit int
//      | Add Expr Expr

interface Func<A, R> {
    R apply(A a1);
}

interface Func2<A, B, R> {
    R apply(A a, B b);
}


abstract class Expr {
    public abstract <A> A match(
            Func<Integer, A> lit, Func2<Expr, Expr, A> add);
}

class Lit extends Expr {
    private int i;

    public Lit(int i) {
        this.i = i;
    }

    @Override
    public <A> A match(Func<Integer, A> lit, Func2<Expr, Expr, A> add) {
        return lit.apply(i);
    }
}

class Add extends Expr {
    private Expr e1;
    private Expr e2;

    public Add(Expr e1, Expr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public <A> A match(Func<Integer, A> lit, Func2<Expr, Expr, A> add) {
        return add.apply(this.e1, this.e2);
    }
}

