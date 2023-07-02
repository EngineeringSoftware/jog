import jog.api.*;
import static jog.api.Action.*;

public class SubNodeExample {

    @Group
    public void Ideal() {
        pSub1(0, 0);
        pSub2(0, 0, 0);
        pSub3(0, 0, 0);
        pSubXMinus_XPlusY_(0, 0);
        pSub_XMinusY_MinusX(0, 0);
        pSubXMinus_YPlusX_(0, 0);
        pSub7(0, 0);
        pSub8(0, 0);
        pSub9(0, 0, 0);
        pSub10(0, 0, 0);
        pSub11(0, 0, 0);
        pSub12(0, 0, 0);
        pSub13(0, 0, 0);
        pSubAssociative1(0, 0, 0);
        pSubAssociative2(0, 0, 0);
        pSubAssociative3(0, 0, 0);
        pSubAssociative4(0, 0, 0);
        pSubNegRShiftToURShift(0);
        pNewSubAddSub1539(0, 0);
        pNewSubAddSub1560(0);
        pNewSubAddSub1564(0, 0);
        pNewSubAddSub1574(0, 0, 0);
        pNewSub_XOrY_Minus_XXorY_(0, 0);
        pNewSub_AOrB_Minus_AAndB_(0, 0);
        pNewSub_APlusB_Minus_AOrB_(0, 0);
        pNewSub_APlusB_Minus_AXorB_(0, 0);
        pNewSub_AAndB_Minus_AOrB_(0, 0);
        pNewSub_AXorB_Minus_AOrB_(0, 0);
        pNewSubNotXMinusNotY(0, 0);
        pNewSubCMinus_C2MinuxX_(0, 0, 0);
        pNewSub_XOrY_MinusX(0, 0);
        pNewSub_Op1And_NegX__MinusOp1(0, 0);
        pNewSub_Op1AndC_MinusOp1(0, 0);
        pNewSubXMinus_XAndY_(0, 0);
        pNewNotXMinus_NotXMinY_(0, 0);
        pNewNotXMinus_NotXMinY_Sym(0, 0);
        pNew_NotXMinY_MinusNotX(0, 0);
        pNew_NotXMinY_MinusNotXSym(0, 0);
        pNewNotXMinus_NotXMaxY_(0, 0);
        pNewNotXMinus_NotXMaxY_Sym(0, 0);
        pNew_NotXMaxY_MinusNotX(0, 0);
        pNew_NotXMaxY_MinusNotXSym(0, 0);
    }

    /**
     * Convert "x-c0" into "x+ -c0".
     * <pre>{@code
     * if( t2->base() == Type::Int ){        // Might be bottom or top...
     *   const TypeInt *i = t2->is_int();
     *   if( i->is_con() )
     *     return new AddINode(in1, phase->intcon(-i->get_con()));
     * }
     * }</pre>
     */
    @Pattern
    public void pSub1(int x, @Constant int c0) {
        before(x - c0);
        after(x + -c0);
    }

    /**
     * Convert "(x+c0) - y" into (x-y) + c0"
     * Do not collapse (x+c0)-y if "+" is a loop increment or
     * if "y" is a loop induction variable.
     * <pre>{@code
     * if( op1 == Op_AddI && ok_to_convert(in1, in2) ) {
     *   const Type *tadd = phase->type( in1->in(2) );
     *   if( tadd->singleton() && tadd != Type::TOP ) {
     *     Node *sub2 = phase->transform( new SubINode( in1->in(1), in2 ));
     *     return new AddINode( sub2, in1->in(2) );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSub2(int x, int y, @Constant int c0) {
        before((x + c0) - y);
        if (Lib.okToConvert(x + c0, y)) {
            after((x - y) + c0);
        }
    }

    /**
     * Convert "x - (y+c0)" into "(x-y) - c0".
     * Need the same check as in above optimization but reversed.
     * <pre>{@code
     * if (op2 == Op_AddI && ok_to_convert(in2, in1)) {
     *   Node* in21 = in2->in(1);
     *   Node* in22 = in2->in(2);
     *   const TypeInt* tcon = phase->type(in22)->isa_int();
     *   if (tcon != NULL && tcon->is_con()) {
     *     Node* sub2 = phase->transform( new SubINode(in1, in21) );
     *     Node* neg_c0 = phase->intcon(- tcon->get_con());
     *     return new AddINode(sub2, neg_c0);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @Shadows("pNewSubAddSub1574")
    public void pSub3(int x, int y, @Constant int c0) {
        before(x - (y + c0));
        if (Lib.okToConvert(y + c0, x)) {
            after((x - y) + -c0);
        }
    }

    /**
     * Convert "x - (x+y)" into "-y".
     * <pre>{@code
     * if (op2 == Op_AddI && in1 == in2->in(1)) {
     *   return new SubINode(phase->intcon(0), in2->in(2));
     * }
     * }</pre>
     */
    @Pattern
    public void pSubXMinus_XPlusY_(int x, int y) {
        before(x - (x + y));
        after(0 - y);
    }


    /**
     * Convert "(x-y) - x" into "-y".
     * <pre>{@code
     * if (op1 == Op_SubI && in1->in(1) == in2) {
     *   return new SubINode(phase->intcon(0), in1->in(2));
     * }
     * }</pre>
     */
    @Pattern
    public void pSub_XMinusY_MinusX(int x, int y) {
        before((x - y) - x);
        after(0 - y);
    }

    /**
     * Convert "x - (y+x)" into "-y".
     * <pre>{@code
     * if (op2 == Op_AddI && in1 == in2->in(2)) {
     *   return new SubINode(phase->intcon(0), in2->in(1));
     * }
     * }</pre>
     */
    @Pattern
    public void pSubXMinus_YPlusX_(int x, int y) {
        before(x - (y + x));
        after(0 - y);
    }

    /**
     * Convert "0 - (x-y)" into "y-x".
     * <pre>{@code
     * if (t1 == TypeInt::ZERO && op2 == Op_SubI && phase->type(in2->in(1)) != TypeInt::ZERO) {
     *   return new SubINode( in2->in(2), in2->in(1) );
     * }
     * }</pre>
     */
    @Pattern
    public void pSub7(int x, int y) {
        before(0 - (x - y));
        if (x != 0) {
            after(y - x);
        }
    }

    /**
     * Convert "0 - (x+con)" into "-con-x".
     * <pre>{@code
     * jint con;
     * if( t1 == TypeInt::ZERO && op2 == Op_AddI &&
     *     (con = in2->in(2)->find_int_con(0)) != 0 )
     *   return new SubINode( phase->intcon(-con), in2->in(1) );
     * }</pre>
     */
    @Pattern
    public void pSub8(int x, @Constant int con) {
        before(0 - (x + con));
        if (con != 0) {
            after(-con - x);
        }
    }

    /**
     * Convert "(X+A) - (X+B)" into "A - B".
     * <pre>{@code
     * if( op1 == Op_AddI && op2 == Op_AddI && in1->in(1) == in2->in(1) )
     *   return new SubINode( in1->in(2), in2->in(2) );
     * }</pre>
     */
    @Pattern
    public void pSub9(int X, int A, int B) {
        before((X + A) - (X + B));
        after(A - B);
    }

    /**
     * Convert "(A+X) - (B+X)" into "A - B".
     * <pre>{@code
     * if( op1 == Op_AddI && op2 == Op_AddI && in1->in(2) == in2->in(2) )
     *   return new SubINode( in1->in(1), in2->in(1) );
     * }</pre>
     */
    @Pattern
    public void pSub10(int X, int A, int B) {
        before((A + X) - (B + X));
        after(A - B);
    }

    /**
     * Convert "(A+X) - (X+B)" into "A - B".
     * <pre>{@code
     * if( op1 == Op_AddI && op2 == Op_AddI && in1->in(2) == in2->in(1) )
     *   return new SubINode( in1->in(1), in2->in(2) );
     * }</pre>
     */
    @Pattern
    public void pSub11(int X, int A, int B) {
        before((A + X) - (X + B));
        after(A - B);
    }

    /**
     * Convert "(X+A) - (B+X)" into "A - B".
     * <pre>{@code
     * if( op1 == Op_AddI && op2 == Op_AddI && in1->in(1) == in2->in(2) )
     *   return new SubINode( in1->in(2), in2->in(1) );
     * }</pre>
     */
    @Pattern
    public void pSub12(int X, int A, int B) {
        before((X + A) - (B + X));
        after(A - B);
    }

    /**
     * Convert "A-(B-C)" into (A+C)-B", since add is commutative and
     * generally nicer to optimize than subtract.
     * <pre>{@code
     * if( op2 == Op_SubI && in2->outcnt() == 1) {
     *   Node *add1 = phase->transform( new AddINode( in1, in2->in(2) ) );
     *   return new SubINode( add1, in2->in(1) );
     * }
     * }</pre>
     */
    @Pattern
    public void pSub13(int A, int B, int C) {
        before(A - (B - C));
        if (Lib.outcnt(B - C) == 1) {
            after((A + C) - B);
        }
    }

    /**
     * Convert "a * b - a * c" into "a * (b - c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* sub_in1 = NULL;
     *   Node* sub_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(1) == in2->in(1)) {
     *     // Convert "a*b-a*c into a*(b-c)
     *     sub_in1 = in1->in(2);
     *     sub_in2 = in2->in(2);
     *     mul_in = in1->in(1);
     *   }
     *   if (mul_in != NULL) {
     *     Node* sub = phase->transform(new SubINode(sub_in1, sub_in2));
     *     return new MulINode(mul_in, sub);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSubAssociative1(int a, int b, int c) {
        before(a * b - a * c);
        after(a * (b - c));
    }

    /**
     * Convert "a * b - b * c" into "b * (a - c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* sub_in1 = NULL;
     *   Node* sub_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(2) == in2->in(1)) {
     *     // Convert "a*b-a*c into a*(b-c)
     *     sub_in1 = in1->in(1);
     *     sub_in2 = in2->in(2);
     *     mul_in = in1->in(2);
     *   }
     *   if (mul_in != NULL) {
     *     Node* sub = phase->transform(new SubINode(sub_in1, sub_in2));
     *     return new MulINode(mul_in, sub);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSubAssociative2(int a, int b, int c) {
        before(a * b - b * c);
        after(b * (a - c));
    }

    /**
     * Convert "a * c - b * c" into "c * (a - b)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* sub_in1 = NULL;
     *   Node* sub_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(2) == in2->in(1)) {
     *     // Convert "a*b-a*c into a*(b-c)
     *     sub_in1 = in1->in(1);
     *     sub_in2 = in2->in(1);
     *     mul_in = in1->in(2);
     *   }
     *   if (mul_in != NULL) {
     *     Node* sub = phase->transform(new SubINode(sub_in1, sub_in2));
     *     return new MulINode(mul_in, sub);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSubAssociative3(int a, int b, int c) {
        before(a * c - b * c);
        after(c * (a - b));
    }

    /**
     * Convert "a * b - c * a" into "a * (b - c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* sub_in1 = NULL;
     *   Node* sub_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(2) == in2->in(1)) {
     *     // Convert "a*b-a*c into a*(b-c)
     *     sub_in1 = in1->in(2);
     *     sub_in2 = in2->in(1);
     *     mul_in = in1->in(1);
     *   }
     *   if (mul_in != NULL) {
     *     Node* sub = phase->transform(new SubINode(sub_in1, sub_in2));
     *     return new MulINode(mul_in, sub);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSubAssociative4(int a, int b, int c) {
        before(a * b - c * a);
        after(a * (b - c));
    }

    /**
     * Convert "0 - (A >> 31)" into "(A >>> 31)".
     * <pre>{@code
     * if ( op2 == Op_RShiftI ) {
     *   Node *in21 = in2->in(1);
     *   Node *in22 = in2->in(2);
     *   const TypeInt *zero = phase->type(in1)->isa_int();
     *   const TypeInt *t21 = phase->type(in21)->isa_int();
     *   const TypeInt *t22 = phase->type(in22)->isa_int();
     *   if ( t21 && t22 && zero == TypeInt::ZERO && t22->is_con(31) ) {
     *     return new URShiftINode(in21, in22);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pSubNegRShiftToURShift(int A) {
        before(0 - (A >> 31));
        after(A >>> 31);
    }

    /**
     * Convert "C - (C2 - X) into "X + (C - C2)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1915
     * <p>
     *      C - (C2 - X)
     *   -> (C + X) - C2    // pSub13
     *   -> (X + C) - C2    // pAddMoveConstantRight
     *   -> (X + C) + (-C2) // pSub1
     *   -> X + (C + -C2)   // pAddConstantAssociative
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSubCMinus_C2MinuxX_(@Constant int C, @Constant int C2, int X) {
        before(C - (C2 - X));
        after(X + (C - C2));
    }

    /**
     * Covert "x - (0 - y)" into "x + y".
     * <p>
     * AddSub1539
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSubAddSub1539(int x, int y) {
        before(x - (0 - y));
        after(x + y);
    }

    /**
     * Convert "-1 - x" into "x ^ -1".
     * <p>
     * AddSub1560
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewSubAddSub1560(int x) {
        before(-1 - x);
        after(x ^ -1);
    }

    /**
     * Convert "(~x) - (~y)" into "y - x".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1865
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewSubNotXMinusNotY(int x, int y) {
        before((x^(-1)) - (y^(-1)));
        after(y - x);
    }

    /**
     * Convert "c - (x ^ -1)" into "x + (c + 1)".
     * <p>
     * AddSub1564
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewSubAddSub1564(int x, @Constant int c) {
        before(c - (x ^ -1));
        after(x + (c + 1));
    }

    /**
     * Convert "(x | y) - (x ^ y)" into "x & y".
     * <p>
     * AddSub:1624
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1973
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_XOrY_Minus_XXorY_(int x, int y) {
        before((x | y) - (x ^ y));
        after(x & y);
    }

    /**
     * Convert "(A | B) - (A & B)" into "A ^ B".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1940
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_AOrB_Minus_AAndB_(int A, int B) {
        before((A | B) - (A & B));
        after(A ^ B);
    }

    /**
     * Convert "(A + B) - (A | B)" into "A & B".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1948
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_APlusB_Minus_AOrB_(int A, int B) {
        before((A + B) - (A | B));
        after(A & B);
    }

    /**
     * Convert "(A + B) - (A & B)" into "A | B".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1956
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_APlusB_Minus_AXorB_(int A, int B) {
        before((A + B) - (A & B));
        after(A | B);
    }

    /**
     * Convert "(A & B) - (A | B)" into "- (A ^ B)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1964
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_AAndB_Minus_AOrB_(int A, int B) {
        before((A & B) - (A | B));
        after(0 - (A ^ B));
    }

    /**
     * Convert "(A ^ B) - (A | B)" into "- (A & B)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1981
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewSub_AXorB_Minus_AOrB_(int A, int B) {
        before((A ^ B) - (A | B));
        after(0 - (A & B));
    }

    /**
     * Convert "c0 - (x + c1)" into "(c0 - c1) - x".
     * <p>
     * AddSub:1574
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(6441)
    public void pNewSubAddSub1574(int x, @Constant int c0, @Constant int c1) {
        before(c0 - (x + c1));
        if (Lib.okToConvert(x + c1, c0)) {
            after((c0 - c1) - x);
        }
    }

    /**
     * Convert ((X | Y) - X) into (~X & Y).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1992
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSub_XOrY_MinusX(int X, int Y) {
        before((X | Y) - X);
        after((X ^ (-1)) & Y);
    }

    /**
     * Convert (Op1 & -X) - Op1 into - (Op1 & (X + -1)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1999
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSub_Op1And_NegX__MinusOp1(int Op1, int X) {
        before((Op1 & (0 - X)) - Op1);
        after(0 - (Op1 & (X + -1)));
    }

    /**
     * Convert (Op1 & C) - Op1 into - (Op1 & (~C)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2009
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSub_Op1AndC_MinusOp1(int Op1, @Constant int C) {
        before((Op1 & C) - Op1);
        after(0 - (Op1 & (~C)));
    }

    /**
     * Convert (X - (X & Y)) into (X & ~Y).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2066
     */
    @Pattern
    @Origin("LLVM")
    public void pNewSubXMinus_XAndY_(int X, int Y) {
        before(X - (X & Y));
        after(X & (Y ^ (-1)));
    }

    /**
     * Convert ~X - Min(~X, Y) -> ~Min(X, ~Y) - X
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2072
     */
    @Pattern
    @Origin("LLVM")
    public void pNewNotXMinus_NotXMinY_(int X, int Y) {
        before((X ^ (-1)) - Math.min(X ^ (-1), Y));
        after((Math.min(X, Y ^ (-1)) ^ (-1)) - X);
    }

    /**
     * Convert ~X - Min(Y, ~X) -> ~Min(X, ~Y) - X
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2073
     */
    @Pattern
    @Origin("LLVM")
    public void pNewNotXMinus_NotXMinY_Sym(int X, int Y) {
        before((X ^ (-1)) - Math.min(Y, X ^ (-1)));
        after((Math.min(X, Y ^ (-1)) ^ (-1)) - X);
    }

    /**
     * Convert Min(~X, Y) - ~X -> X - ~Min(X, ~Y)
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2074
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotXMinY_MinusNotX(int X, int Y) {
        before(Math.min(X ^ (-1), Y) - (X ^ (-1)));
        after(X - (Math.min(X, Y ^ (-1)) ^ (-1)));
    }

    /**
     * Convert Min(Y, ~X) - ~X -> X - ~Min(X, ~Y)
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2075
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotXMinY_MinusNotXSym(int X, int Y) {
        before(Math.min(Y, X ^ (-1)) - (X ^ (-1)));
        after(X - (Math.min(X, Y ^ (-1)) ^ (-1)));
    }

    /**
     * Convert ~X - Max(~X, Y) -> ~Max(X, ~Y) - X
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2072
     */
    @Pattern
    @Origin("LLVM")
    public void pNewNotXMinus_NotXMaxY_(int X, int Y) {
        before((X ^ (-1)) - Math.max(X ^ (-1), Y));
        after((Math.max(X, Y ^ (-1)) ^ (-1)) - X);
    }

    /**
     * Convert ~X - Max(Y, ~X) -> ~Max(X, ~Y) - X
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2073
     */
    @Pattern
    @Origin("LLVM")
    public void pNewNotXMinus_NotXMaxY_Sym(int X, int Y) {
        before((X ^ (-1)) - Math.max(Y, X ^ (-1)));
        after((Math.max(X, Y ^ (-1)) ^ (-1)) - X);
    }

    /**
     * Convert Max(~X, Y) - ~X -> X - ~Max(X, ~Y)
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2074
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotXMaxY_MinusNotX(int X, int Y) {
        before(Math.max(X ^ (-1), Y) - (X ^ (-1)));
        after(X - (Math.max(X, Y ^ (-1)) ^ (-1)));
    }

    /**
     * Convert Max(Y, ~X) - ~X -> X - ~Max(X, ~Y)
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L2075
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotXMaxY_MinusNotXSym(int X, int Y) {
        before(Math.max(Y, X ^ (-1)) - (X ^ (-1)));
        after(X - (Math.max(X, Y ^ (-1)) ^ (-1)));
    }
}
