package OOP;

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

//class VBool : Value
//{
//    public VBool(bool b)
//    {
//    }
//}

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

// Addition of new data variant
class Mul implements Exp {
    Exp l, r;

    public Mul(Exp l, Exp r) {
        this.l = l;
        this.r = r;
    }

    public Value eval() {
        return new VInt(l.eval().getInt() * r.eval().getInt());
    }
}

// Addition of new operation is very hard
// need to change interface Exp
