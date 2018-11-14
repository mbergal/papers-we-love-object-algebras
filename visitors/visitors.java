package visitors;

interface Value {
    int getInt();

    Boolean getBoolean();
}

class VInt implements Value {
    private int value;

    public VInt(int i) {
        this.value = i;
    }

    public int getInt() {
        return value;
    }

    public Boolean getBoolean() {
        throw new UnknownError();
    }
}

interface IntAlg<A> {
    A lit(int x);

    A add(A e1, A e2);
}

interface Exp {
    <A> A accept(IntAlg<A> vis);
}

class Lit implements Exp {
    private int i;

    public Lit(int i) {
        this.i = i;
    }

    public <A> A accept(IntAlg<A> vis) {
        return vis.lit(i);
    }
}

class Add implements Exp {
    Exp left, right;

    public Add(Exp left, Exp right) {
        this.left = left;
        this.right = right;
    }

    public <A> A accept(IntAlg<A> vis) {
        return vis.add(left.accept(vis), right.accept(vis));
    }
}

interface IPrint {
    String print();
}

class IntPrint implements IntAlg<IPrint> {
    public IPrint lit(final int x) {
        return new IPrint() {
            public String print() {
                return new Integer(x).toString();
            }
        };
    }

    public IPrint add(final IPrint e1, final IPrint e2) {
        return new IPrint() {
            public String print() {
                return e1.print() + " + " + e2.print();
            }
        };
    }
}

class IntEval implements IntAlg<Value> {
    public Value lit(int x) {
        return new VInt(x);
    }

    @Override
    public Value add(Value e1, Value e2) {
        return new VInt(e1.getInt() + e2.getInt());
    }

}

class Test {
    public void f() {
        Exp e = new Add(new Lit(1), new Lit(2));
        String r1 = e.accept(new IntPrint()).print();
        int r2 = e.accept(new IntEval()).getInt();
    }
}

//    public class Mul : Exp
//    {
//        public A accept<A>(IntMulVisitor<A> vis)
//        {
//            throw new NotImplementedException();
//        }
//    }
//
//    public interface IntMulVisitor<T> : IntVisitor<T>
//    {
//
//    }
