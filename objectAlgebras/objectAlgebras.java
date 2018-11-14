package objectAlgebras;

import java.util.HashMap;

interface IntAlg<A> {
    A lit(int x);

    A add(A e1, A e2);
}

interface Value {
    int getInt();

    Boolean getBoolean();
}

interface Exp {
    Value eval();
}

class Lit implements Exp {
    int x;

    public Lit(int x) {
        this.x = x;
    }

    public Value eval() {
        return new VInt(x);
    }
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

class VBool implements Value {
    private final Boolean b;

    public VBool(Boolean b) {
        this.b = b;
    }

    @Override
    public int getInt() {
        throw new UnknownError();
    }

    @Override
    public Boolean getBoolean() {
        return null;
    }
}


class Add implements Exp {
    Exp l, r;

    public Add(Exp l, Exp r) {
        this.l = l;
        this.r = r;
    }

    public Value eval() {
        return new VInt(l.eval().getInt() + r.eval().getInt());
    }
}

class IntFactory implements IntAlg<Exp> {
    public Exp lit(int x) {
        return new Lit(x);
    }

    public Exp add(Exp e1, Exp e2) {
        return new Add(e1, e2);
    }
}

interface IPrint {
    String print();
}

class IntPrint implements IntAlg<IPrint> {
    public IPrint lit(int x) {
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

class Test1 {
    <A> A exp(IntAlg<A> f) {
        return f.add(f.lit(3), f.lit(5));
    }

    void test() {
        IntFactory base = new IntFactory();
        IntPrint print = new IntPrint();
        int x = exp(base).eval().getInt(); // int x = exp.eval();    }
        String s = exp(print).print();
    }
}

// Adding a printing operation.

class Print2 implements IntAlg<String> {
    public String lit(int x) {
        return new Integer(x).toString();
    }

    public String add(String e1, String e2) {
        return e1 + " + " + e2;
    }
}

class Test2 {
    <A> A exp(IntAlg<A> f) {
        return f.add(f.lit(3), f.lit(5));
    }

    void test() {
        Print2 print = new Print2();
        String x = exp(print);
    }
}


// Adding boolean expression variants.

interface IntBoolAlg<A> extends IntAlg<A> {
    A bool(Boolean b);

    A iff(A e1, A e2, A e3);
}

class Bool implements Exp {

    private final Boolean b;

    public Bool(Boolean b) {
        this.b = b;
    }

    @Override
    public Value eval() {
        return new VBool(b);
    }
}

class Iff implements Exp {

    public Iff(Exp e1, Exp e2, Exp e3) {
    }

    @Override
    public Value eval() {
        return null;
    }
}

class IntBoolFactory extends IntFactory implements IntBoolAlg<Exp> {
    public Exp bool(Boolean b) {
        return new Bool(b);
    }

    public Exp iff(Exp e1, Exp e2, Exp e3) {
        return new Iff(e1, e2, e3);
    }
}

/* Extended Retroactive Implementation for Printing */
class IntBoolPrint extends IntPrint implements IntBoolAlg<IPrint> {
    public IPrint bool(final Boolean b) {
        return new IPrint() {
            public String print() {
                return new Boolean(b).toString();
            }
        };
    }

    public IPrint iff(final IPrint e1, final IPrint e2, final IPrint e3) {
        return new IPrint() {
            public String print() {
                return "if (" + e1.print() + ") then " + e2.print() + " else " + e3.
                        print();
            }
        };
    }
}


class Test3 {
    <A> A exp(IntAlg<A> f) {
        return f.add(f.lit(3), f.lit(5));
    }

    <A> A exp2(IntBoolAlg<A> v) {
        return v.iff(v.bool(false), exp(v), v.lit(0));
    }

    void test() {
        IntBoolPrint p2 = new IntBoolPrint();
        exp(p2).print();

        IntPrint p = new IntPrint();
//        exp2(p).print(); // type-error
    }
}


interface StmtAlg<E, S> extends IntBoolAlg<E> {
    E var(String x);

    E assign(String x, E e);

    S expr(E e);

    S comp(S e1, S e2);
}

interface Stmt {
    void eval();
}

class StmtFactory extends IntBoolFactory implements StmtAlg<Exp, Stmt> {
    HashMap<String, Value> map = new HashMap<String, Value>();

    public Exp var(final String x) {
        return () -> map.get(x);
    }

    public Exp assign(final String x, final Exp e) {
        return () -> {
            Value v = e.eval();
            map.put(x, v);
            return v;
        };
    }

    public Stmt comp(final Stmt s1, final Stmt s2) {
        return () -> {
            s1.eval();
            s2.eval();
        };
    }

    public Stmt expr(final Exp e) {
        return () -> e.eval();
    }

    class Test4 {
        <E, S> E exp(StmtAlg<E, S> v) {
            return v.assign("x", v.add(v.lit(3), v.lit(4)));
        }

        <E, S> S stmt(StmtAlg<E, S> v) {
            return v.comp(v.expr(exp(v)), v.expr(v.var("x")));
        }

//        <E,S> S badStmt(StmtAlg<E,S> v) {
//            return v.comp(exp(v), v.var("x")); //type-error
//        }

        void test() {
            StmtFactory factory = new StmtFactory();
            Value r1 = exp(factory).eval();
            stmt(factory).eval();
        }
    }

//    interface BoolAlg<A> {
//        A bool(boolean x);
//        A iff(A b, A e1, A e2);
//    }
}


/*



@startuml

interface IntAlg <A>
interface IntBoolAlg <A>

IntAlg <|-- IntBoolAlg
IntAlg <|-- IntFactory
IntAlg <|-- IntPrint


IntFactory <|-- IntBoolFactory
IntBoolAlg <|-- IntBoolFactory

IntBoolAlg  <|-- IntBoolPrint
IntPrint  <|-- IntBoolPrint

interface StmtAlg<E, S>

IntBoolAlg  <|-- StmtAlg
IntBoolFactory <|-- StmtFactory
StmtAlg<|-- StmtFactory

@enduml
 */