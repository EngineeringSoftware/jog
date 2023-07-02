import jog.api.*;

import static jog.api.Action.*;

public class AddNodeExample {

    @Group
    public void Ideal() {
        pAdd1(0, 0, 0);
        pAdd2(0, 0, 0, 0);
        pAdd3(0, 0, 0);
        pAdd3Sym(0, 0, 0);
        pAdd4(0, 0, 0);
        pAdd4Sym(0, 0, 0);
        pAdd5(0, 0, 0);
        pAdd6(0, 0, 0);
        pAdd7(0, 0);
        pAdd8(0, 0);
        pAdd9(0, 0, 0);
        pAddAssociative1(0, 0, 0);
        pAddAssociative2(0, 0, 0);
        pAddAssociative3(0, 0, 0);
        pAddAssociative4(0, 0, 0);
        pAddURShiftThenLShiftToRRotation(0, 0, 0);
        pAddURShiftThenLShiftToRRotationSym(0, 0, 0);
        pAddNotXPlusOne(0);
        pAddMoveConstantRight(0, 0);
        pAddConstantAssociative(0, 0, 0);
        pAddPushConstantDown(0, 0, 0);
        pAddPushConstantDownSym(0, 0, 0);
        p_AMaxB_Plus_AMinB_(0, 0);
        p_AMaxB_Plus_AMinB_Sym(0, 0);
        p_XLShiftC1_Or_XURShiftC2_(0, 0, 0);
        p_XURShiftC1_Or_XLShiftC2_(0, 0, 0);
        p_XLShiftS_Or_XURShift_ConMinusS__(0, 0, 0);
        p_XURShiftS_Or_XLShift_ConMinusS__(0, 0, 0);
        pMinAssociative(0, 0, 0);
        pAddDistributiveOverMin(0, 0, 0);
        pAssociativeThenAddDistributiveOverMin(0, 0, 0, 0);
        pNewAddNotXPlusOneToNegX1(0, 0);
        pNewAddNotXPlusOneToNegX2(0, 0);
        pNewAddNotXPlusOneToNegX3(0, 0);
        pNewAddNotXPlusOneToNegX4(0, 0);
        pNewAddAddSub1165(0, 0);
        pNewAddAddSub1156(0);
        pNewAddAddSub1202(0, 0);
        pNewAddAddSub1295(0, 0);
        pNewAddAddSub1295Sym(0, 0);
        pNewAddAddSub1309(0, 0);
        pNewAddAddSub1309Sym(0, 0);
        pNewAdd_APlusC1_Plus_C2MinusB_(0, 0, 0, 0);
        pNewAddXModC0PlusXDivC0ModC1MulC0(0, 0, 0);
        pNewAddAddSub1040(0, 0, 0, 0);
        pNewAddAddSub1043(0, 0, 0, 0);
        pNew_XOrC2_PlusC(0, 0, 0);
        pNewXPlus_ConMinusY_(0, 0, 0);
        pNewXPlus_ConMinusY_Sym(0, 0, 0);
        pNewDeMorganLawOrToAnd(0, 0);
        pNewDeMorganWithReassociationOrToAnd(0, 0, 0);
        pNewDeMorganWithReassociationOrToAndSym(0, 0, 0);
        pNew_AAndB_OrNot_AOrB_(0, 0);
        pNew_AAndB_OrNot_AOrB_Sym(0, 0);
        pNew_AAndB_OrNot_BOrA_(0, 0);
        pNew_AAndB_OrNot_BOrA_Sym(0, 0);
        pNew_AXorB_OrNot_AOrB_(0, 0);
        pNew_AXorB_OrNot_AOrB_Sym(0, 0);
        pNew_AXorB_OrNot_BOrA_(0, 0);
        pNew_AXorB_OrNot_BOrA_Sym(0, 0);
        pNew_AAndNotB_Or_NotAAndB_1(0, 0);
        pNew_AAndNotB_Or_NotAAndB_2(0, 0);
        pNew_AAndNotB_Or_NotAAndB_3(0, 0);
        pNew_AAndNotB_Or_NotAAndB_4(0, 0);
        pNew_Not_AOrB_AndC_Or_Not_AOrC_AndB_(0, 0, 0);
        pNew_Not_AOrB_AndC_Or_Not_BOrC_AndA_(0, 0, 0);
        pNew_Not_AOrB_AndC_OrNot_AOrC_(0, 0, 0);
        pNew_Not_AOrB_AndC_OrNot_AOrC_Sym(0, 0 , 0);
        pNew_Not_AOrB_AndC_OrNot_BOrC_(0, 0, 0);
        pNew_Not_AOrB_AndC_OrNot_BOrC_Sym(0, 0, 0);
        pNew_Not_AOrB_AndC_OrNot_COr_AXorB__(0, 0, 0);
        pNew_Not_AOrB_AndC_OrNot_COr_AXorB__Sym(0, 0, 0);
        pNew__NotAAndB_AndC_OrNot__AOrB_OrC_(0, 0, 0);
        pNew__NotAAndB_AndC_OrNot__AOrB_OrC_Sym(0, 0, 0);
        pNew_NotAAndBAndC_Or_Not_AOrB_(0, 0, 0);
        pNew_NotAAndBAndC_Or_Not_AOrB_Sym(0, 0, 0);
        pNew_NotAAndBAndC_Or_Not_AOrC_(0, 0, 0);
        pNew_NotAAndBAndC_Or_Not_AOrC_Sym(0, 0, 0);
    }

    /**
     * Convert "(con1-x)+con2" into "(con1+con2)-x".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   const Type *t_sub1 = phase->type( in1->in(1) );
     *   const Type *t_2    = phase->type( in2        );
     *   if( t_sub1->singleton() && t_2->singleton() && t_sub1 != Type::TOP && t_2 != Type::TOP )
     *     return new SubINode(phase->makecon( add_ring( t_sub1, t_2 ) ), in1->in(2) );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd1(int x, @Constant int con1, @Constant int con2) {
        before((con1 - x) + con2);
        after((con1 + con2) - x);
    }

    /**
     * Convert "(a-b)+(c-d)" into "(a+c)-(b+d)".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   if( op2 == Op_SubI ) {
     *     Node *sub  = new SubINode(NULL, NULL);
     *     sub->init_req(1, phase->transform(new AddINode(in1->in(1), in2->in(1) ) ));
     *     sub->init_req(2, phase->transform(new AddINode(in1->in(2), in2->in(2) ) ));
     *     return sub;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @Shadows({"pAdd5", "pAdd6", "pNewAddAddSub1165"})
    public void pAdd2(int a, int b, int c, int d) {
        before((a - b) + (c - d));
        after((a + c) - (b + d));
    }

    /**
     * Convert "(a-b)+(b+c)" into "(a+c)".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   if( op2 == Op_AddI && in1->in(2) == in2->in(1) ) {
     *     return new AddINode(in1->in(1), in2->in(2));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd3(int a, int b, int c) {
        before((a - b) + (b + c));
        after(a + c);
    }

    /**
     * Convert "(b+a)+(c-b)" into "(a+c)".
     * <pre>{@code
     * if( op1 == Op_AddI ) {
     *   if( op2 == Op_SubI && in1->in(1) == in2->in(2) ) {
     *     return new AddINode(in1->in(2), in2->in(1));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd3Sym(int a, int b, int c) {
        before((b + a) + (c - b));
        after(c + a);
    }

    /**
     * Convert "(a-b)+(c+b)" into "(a+c)".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   if( op2 == Op_AddI && in1->in(2) == in2->in(2) ) {
     *     return new AddINode(in1->in(1), in2->in(1));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd4(int a, int b, int c) {
        before((a - b) + (c + b));
        after(a + c);
    }

    /**
     * Convert "(a+b)+(c-b)" into "(a+c)".
     * <pre>{@code
     * if( op1 == Op_AddI ) {
     *   if( op2 == Op_SubI && in1->in(2) == in2->in(2) ) {
     *     return new AddINode(in1->in(1), in2->in(1));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd4Sym(int a, int b, int c) {
        before((a + b) + (c - b));
        after(c + a);
    }

    /**
     * Convert "(a-b)+(b-c)" into "(a-c)".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   if( op2 == Op_SubI && in1->in(2) == in2->in(1) ) {
     *     return new SubINode(in1->in(1), in2->in(2));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @PR(6752)
    public void pAdd5(int a, int b, int c) {
        before((a - b) + (b - c));
        after(a - c);
    }

    /**
     * Convert "(a-b)+(c-a)" into "(c-b)".
     * <pre>{@code
     * if( op1 == Op_SubI ) {
     *   if( op2 == Op_SubI && in1->in(1) == in2->in(2) ) {
     *     return new SubINode(in2->in(1), in1->in(2));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @PR(6752)
    public void pAdd6(int a, int b, int c) {
        before((a - b) + (c - a));
        after(c - b);
    }

    /**
     * Convert "x+(0-y)" into "(x-y)".
     * <pre>{@code
     * if( op2 == Op_SubI && phase->type(in2->in(1)) == TypeInt::ZERO )
     *   return new SubINode(in1, in2->in(2) );
     * }</pre>
     */
    @Pattern
    @Shadows("pNewAddAddSub1165")
    public void pAdd7(int x, int y) {
        before(x + (0 - y));
        after(x - y);
    }

    /**
     * Convert "(0-y)+x" into "(x-y)".
     * <pre>{@code
     * if( op1 == Op_SubI && phase->type(in1->in(1)) == TypeInt::ZERO )
     *   return new SubINode(in2, in1->in(2) );
     * }</pre>
     */
    @Pattern
    @Shadows("pNewAddAddSub1165")
    public void pAdd8(int x, int y) {
        before((0 - y) + x);
        after(x - y);
    }

    /**
     * Convert (x>>>z)+y into (x+(y<<z))>>>z for small constant z and
     * y. Transform works for small z and small negative y when the
     * addition (x + (y << z)) does not cross zero: z < 5 && -5 < y <
     * 0 && x >= -(y << z).
     * <pre>{@code
     * if( op1 == Op_URShiftI && op2 == Op_ConI &&
     *     in1->in(2)->Opcode() == Op_ConI ) {
     *   jint z = phase->type( in1->in(2) )->is_int()->get_con() & 0x1f; // only least significant 5 bits matter
     *   jint y = phase->type( in2 )->is_int()->get_con();
     *
     *   if( z < 5 && -5 < y && y < 0 ) {
     *     const Type *t_in11 = phase->type(in1->in(1));
     *     if( t_in11 != Type::TOP && (t_in11->is_int()->_lo >= -(y << z)) ) {
     *       Node *a = phase->transform( new AddINode( in1->in(1), phase->intcon(y<<z) ) );
     *       return new URShiftINode( a, in1->in(2) );
     *     }
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAdd9(int x, @Constant int y, @Constant int z) {
        before((x >>> z) + y);
        z = z & 0x1f;
        if (z < 5 && -5 < y && y < 0 && x >= -(y << z)) {
            after((x + (y << z)) >>> z);
        }
    }

    /**
     * Convert "a * b + a * c" into "a * (b + c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* add_in1 = NULL;
     *   Node* add_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(1) == in2->in(1)) {
     *     add_in1 = in1->in(2);
     *     add_in2 = in2->in(2);
     *     mul_in = in1->in(1);
     *   }
     *   if (mul_in != NULL) {
     *     Node* add = phase->transform(new AddINode(add_in1, add_in2));
     *     return new MulINode(mul_in, add);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddAssociative1(int a, int b, int c) {
        before(a * b + a * c);
        after(a * (b + c));
    }

    /**
     * Convert "a * b + a * c" into "a * (b + c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* add_in1 = NULL;
     *   Node* add_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(1) == in2->in(1)) {
     *     add_in1 = in1->in(1);
     *     add_in2 = in2->in(2);
     *     mul_in = in1->in(2);
     *   }
     *   if (mul_in != NULL) {
     *     Node* add = phase->transform(new AddINode(add_in1, add_in2));
     *     return new MulINode(mul_in, add);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddAssociative2(int a, int b, int c) {
        before(a * b + b * c);
        after(b * (a + c));
    }

    /**
     * Convert "a * c + b * c" into "c * (a + b)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* add_in1 = NULL;
     *   Node* add_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(1) == in2->in(1)) {
     *     add_in1 = in1->in(1);
     *     add_in2 = in2->in(1);
     *     mul_in = in1->in(2);
     *   }
     *   if (mul_in != NULL) {
     *     Node* add = phase->transform(new AddINode(add_in1, add_in2));
     *     return new MulINode(mul_in, add);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddAssociative3(int a, int b, int c) {
        before(a * c + b * c);
        after(c * (a + b));
    }

    /**
     * Convert "a * b + c * a" into "a * (b + c)".
     * <pre>{@code
     * if (op1 == Op_MulI && op2 == Op_MulI) {
     *   Node* add_in1 = NULL;
     *   Node* add_in2 = NULL;
     *   Node* mul_in = NULL;
     *
     *   if (in1->in(1) == in2->in(1)) {
     *     add_in1 = in1->in(2);
     *     add_in2 = in2->in(1);
     *     mul_in = in1->in(1);
     *   }
     *   if (mul_in != NULL) {
     *     Node* add = phase->transform(new AddINode(add_in1, add_in2));
     *     return new MulINode(mul_in, add);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddAssociative4(int a, int b, int c) {
        before(a * b + c * a);
        after(a * (b + c));
    }

    /**
     * Convert "(x >>> rshift) + (x << lshift)" into
     * "RotateRight(x, rshift)", when {@literal rshift + lshift = 32}.
     * <pre>{@code
     * if (Matcher::match_rule_supported(Op_RotateRight) &&
     *     (op1 == Op_URShiftI && op2 == Op_LShiftI) &&
     *     in1->in(1) != NULL && in1->in(1) == in2->in(1)) {
     *   Node* rshift = op1 == in1->in(2);
     *   Node* lshift = op1 == in2->in(2);
     *   if (rshift != NULL && lshift != NULL) {
     *     const TypeInt* rshift_t = phase->type(rshift)->isa_int();
     *     const TypeInt* lshift_t = phase->type(lshift)->isa_int();
     *     if (lshift_t != NULL && lshift_t->is_con() &&
     *         rshift_t != NULL && rshift_t->is_con() &&
     *         ((lshift_t->get_con() & 0x1F) == (32 - (rshift_t->get_con() & 0x1F)))) {
     *       return new RotateRightNode(in1->in(1), phase->intcon(rshift_t->get_con() & 0x1F), TypeInt::INT);
     *     }
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddURShiftThenLShiftToRRotation(int x, @Constant int rshift, @Constant int lshift) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_RIGHT)) {
            before((x >>> rshift) + (x << lshift));
            if ((lshift & 31) == 32 - (rshift & 31)) {
                after(Integer.rotateRight(x, rshift & 31));
            }
        }
    }

    /**
     * Convert "(x >>> rshift) + (x << lshift)" into
     * "RotateRight(x, rshift)", when {@literal rshift + lshift = 32}.
     * <pre>{@code
     * if (Matcher::match_rule_supported(Op_RotateRight) &&
     *     (op1 == Op_LShiftI && op2 == Op_URShiftI) &&
     *     in1->in(1) != NULL && in1->in(1) == in2->in(1)) {
     *   Node* rshift = in2->in(2);
     *   Node* lshift = in1->in(2);
     *   if (rshift != NULL && lshift != NULL) {
     *     const TypeInt* rshift_t = phase->type(rshift)->isa_int();
     *     const TypeInt* lshift_t = phase->type(lshift)->isa_int();
     *     if (lshift_t != NULL && lshift_t->is_con() &&
     *         rshift_t != NULL && rshift_t->is_con() &&
     *         ((lshift_t->get_con() & 0x1F) == (32 - (rshift_t->get_con() & 0x1F)))) {
     *       return new RotateRightNode(in1->in(1), phase->intcon(rshift_t->get_con() & 0x1F), TypeInt::INT);
     *     }
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddURShiftThenLShiftToRRotationSym(int x, @Constant int lshift, @Constant int rshift) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_RIGHT)) {
            before((x << lshift) + (x >>> rshift));
            if ((lshift & 31) == 32 - (rshift & 31)) {
                after(Integer.rotateRight(x, rshift & 31));
            }
        }
    }

    /**
     * Convert (~x+1) into -x.
     * Note there isn't a bitwise not bytecode, "~x" would be
     * typically represented as "x^(-1)", so (~x+1) will be
     * (x^(-1))+1.
     * <pre>{@code
     * if (op1 == Op_XorI && phase->type(in2) == TypeInt::ONE &&
     *     phase->type(in1->in(2)) == TypeInt::MINUS_1) {
     *   return new SubINode(phase->makecon(TypeInt::ZERO), in1->in(1));
     * }
     * }</pre>
     */
    @Pattern
    public void pAddNotXPlusOne(int x) {
        before((x ^ -1) + 1);
        after(0 - x);
    }

    /**
     * Convert con + x into x + con.
     * <pre>{@code
     * bool con_left = phase->type(in1)->singleton();
     * bool con_right = phase->type(in2)->singleton();
     *
     * // Convert "1+x" into "x+1".
     * // Right is a constant; leave it
     * if( con_right ) return false;
     * // Left is a constant; move it right.
     * if( con_left ) {
     *   add->swap_edges(1, 2);
     *   return true;
     * }
     * }</pre>
     */
    @Pattern
    public void pAddMoveConstantRight(int x, @Constant int con) {
        before(con + x);
        after(x + con);
    }

    /**
     * Convert (x + con1) + con2 into x + (con1 + con2).
     * <pre>{@code
     * if (con_right && t2 != Type::TOP && // Right input is a constant?
     *     add1_op == this_op) { // Left input is an Add?
     *
     *   // Type of left _in right input
     *   const Type *t12 = phase->type(add1->in(2));
     *   if (t12->singleton() && t12 != Type::TOP) { // Left input is an add of a constant?
     *     // Check for rare case of closed data cycle which can happen inside
     *     // unreachable loops. In these cases the computation is undefined.
     *     // The Add of the flattened expression
     *     Node *x1 = add1->in(1);
     *     Node *x2 = phase->makecon(add1->as_Add()->add_ring(t2, t12));
     *     set_req_X(2, x2, phase);
     *     set_req_X(1, x1, phase);
     *     progress = this;            // Made progress
     *     add1 = in(1);
     *     add1_op = add1->Opcode();
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAddConstantAssociative(int x, @Constant int con1, @Constant int con2) {
        before((x + con1) + con2);
        after(x + (con1 + con2));
    }

    /**
     * Convert (x + con) + y into (x + y) + con.
     * <pre>{@code
     * if (add1_op == this_op && !con_right) {
     *   Node *a12 = add1->in(2);
     *   const Type *t12 = phase->type( a12 );
     *   if (t12->singleton() && t12 != Type::TOP && (add1 != add1->in(1)) &&
     *       !(add1->in(1)->is_Phi() && (add1->in(1)->as_Phi()->is_tripcount(T_INT) || add1->in(1)->as_Phi()->is_tripcount(T_LONG)))) {
     *     add2 = add1->clone();
     *     add2->set_req(2, in(2));
     *     add2 = phase->transform(add2);
     *     set_req_X(1, add2, phase);
     *     set_req_X(2, a12, phase);
     *     progress = this;
     *     add2 = a12;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @Shadows({"pAddConstantAssociative", "pNewAddNotXPlusOneToNegX1", "pNewAdd_APlusC1_Plus_C2MinusB_", "pNewAddAddSub1043", "pNewAddAddSub1040"})
    public void pAddPushConstantDown(int x, @Constant int con, int y) {
        before((x + con) + y);
        after((x + y) + con);
    }

    /**
     * Convert x + (y + con) into (x + y) + con.
     * <pre>{@code
     * int add2_op = add2->Opcode();
     * if (add2_op == this_op && !con_left) {
     *   Node *a22 = add2->in(2);
     *   const Type *t22 = phase->type( a22 );
     *   if (t22->singleton() && t22 != Type::TOP && (add2 != add2->in(1)) &&
     *       !(add2->in(1)->is_Phi() && (add2->in(1)->as_Phi()->is_tripcount(T_INT) || add2->in(1)->as_Phi()->is_tripcount(T_LONG)))) {
     *     Node *addx = add2->clone();
     *     addx->set_req(1, in(1));
     *     addx->set_req(2, add2->in(1));
     *     addx = phase->transform(addx);
     *     set_req_X(1, addx, phase);
     *     set_req_X(2, a22, phase);
     *     progress = this;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    @Shadows("pNewAddNotXPlusOneToNegX2")
    public void pAddPushConstantDownSym(int x, int y, @Constant int con) {
        before(x + (y + con));
        after((x + y) + con);
    }

    /**
     * Convert "max(a,b) + min(a,b)" into "a+b".
     * <p>
     * <pre>{@code
     * if ((in1->Opcode() == add->as_Add()->max_opcode() && in2->Opcode() == add->as_Add()->min_opcode())
     *     || (in1->Opcode() == add->as_Add()->min_opcode() && in2->Opcode() == add->as_Add()->max_opcode())) {
     *   Node *in11 = in1->in(1);
     *   Node *in12 = in1->in(2);
     *
     *   Node *in21 = in2->in(1);
     *   Node *in22 = in2->in(2);
     *
     *   if ((in11 == in21 && in12 == in22) ||
     *       (in11 == in22 && in12 == in21)) {
     *     add->set_req(1, in11);
     *     add->set_req(2, in12);
     *     PhaseIterGVN* igvn = phase->is_IterGVN();
     *     if (igvn) {
     *       igvn->_worklist.push(in1);
     *       igvn->_worklist.push(in2);
     *     }
     *     return true;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_AMaxB_Plus_AMinB_(int a, int b) {
        before(Math.max(a, b) + Math.min(a, b));
        after(a + b);
    }

    /**
     * Convert "min(a,b) + max(a,b)" into "a+b".
     * <pre>{@code
     * if ((in1->Opcode() == add->as_Add()->max_opcode() && in2->Opcode() == add->as_Add()->min_opcode())
     *     || (in1->Opcode() == add->as_Add()->min_opcode() && in2->Opcode() == add->as_Add()->max_opcode())) {
     *   Node *in11 = in1->in(1);
     *   Node *in12 = in1->in(2);
     *
     *   Node *in21 = in2->in(1);
     *   Node *in22 = in2->in(2);
     *
     *   if ((in11 == in21 && in12 == in22) ||
     *       (in11 == in22 && in12 == in21)) {
     *     add->set_req(1, in11);
     *     add->set_req(2, in12);
     *     PhaseIterGVN* igvn = phase->is_IterGVN();
     *     if (igvn) {
     *       igvn->_worklist.push(in1);
     *       igvn->_worklist.push(in2);
     *     }
     *     return true;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_AMaxB_Plus_AMinB_Sym(int a, int b) {
        before(Math.min(a, b) + Math.max(a, b));
        after(a + b);
    }

    /**
     * Convert (x << lshift) | (x >>> rshift) into rotateLeft(x, lshift).
     * <pre>{@code
     * // Find shift value for Integer or Long OR.
     * Node* rotate_shift(PhaseGVN* phase, Node* lshift, Node* rshift, int mask) {
     * // val << norm_con_shift | val >> ({32|64} - norm_con_shift) => rotate_left val, norm_con_shift
     * const TypeInt* lshift_t = phase->type(lshift)->isa_int();
     * const TypeInt* rshift_t = phase->type(rshift)->isa_int();
     * if (lshift_t != NULL && lshift_t->is_con() &&
     *     rshift_t != NULL && rshift_t->is_con() &&
     *     ((lshift_t->get_con() & mask) == ((mask + 1) - (rshift_t->get_con() & mask)))) {
     *   return phase->intcon(lshift_t->get_con() & mask);
     * }
     * // val << var_shift | val >> ({0|32|64} - var_shift) => rotate_left val, var_shift
     * if (rshift->Opcode() == Op_SubI && rshift->in(2) == lshift && rshift->in(1)->is_Con()){
     *   const TypeInt* shift_t = phase->type(rshift->in(1))->isa_int();
     *   if (shift_t != NULL && shift_t->is_con() &&
     *       (shift_t->get_con() == 0 || shift_t->get_con() == (mask + 1))) {
     *     return lshift;
     *   }
     * }
     * return NULL;
     * }
     *
     * int lopcode = in(1)->Opcode();
     * int ropcode = in(2)->Opcode();
     * if (Matcher::match_rule_supported(Op_RotateLeft) &&
     *     lopcode == Op_LShiftI && ropcode == Op_URShiftI && in(1)->in(1) == in(2)->in(1)) {
     *   Node* lshift = in(1)->in(2);
     *   Node* rshift = in(2)->in(2);
     *   Node* shift = rotate_shift(phase, lshift, rshift, 0x1F);
     *   if (shift != NULL) {
     *     return new RotateLeftNode(in(1)->in(1), shift, TypeInt::INT);
     *   }
     *   return NULL;
     * }
     * }</pre>
     */
    @Pattern
    public void p_XLShiftC1_Or_XURShiftC2_(int x, @Constant int lshift, @Constant int rshift) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_LEFT)) {
            before((x << lshift) | (x >>> rshift));
            if ((lshift & 31) == 32 - (rshift & 31)) {
                after(Integer.rotateLeft(x, lshift & 31));
            }
        }
    }

    /**
     * Convert "(x >>> rshift) | (x << lshift)" into rotateRight(x, rshift)
     * <pre>{@code
     * // Find shift value for Integer or Long OR.
     * Node* rotate_shift(PhaseGVN* phase, Node* lshift, Node* rshift, int mask) {
     * // val << norm_con_shift | val >> ({32|64} - norm_con_shift) => rotate_left val, norm_con_shift
     * const TypeInt* lshift_t = phase->type(lshift)->isa_int();
     * const TypeInt* rshift_t = phase->type(rshift)->isa_int();
     * if (lshift_t != NULL && lshift_t->is_con() &&
     *     rshift_t != NULL && rshift_t->is_con() &&
     *     ((lshift_t->get_con() & mask) == ((mask + 1) - (rshift_t->get_con() & mask)))) {
     *   return phase->intcon(lshift_t->get_con() & mask);
     * }
     * // val << var_shift | val >> ({0|32|64} - var_shift) => rotate_left val, var_shift
     * if (rshift->Opcode() == Op_SubI && rshift->in(2) == lshift && rshift->in(1)->is_Con()){
     *   const TypeInt* shift_t = phase->type(rshift->in(1))->isa_int();
     *   if (shift_t != NULL && shift_t->is_con() &&
     *       (shift_t->get_con() == 0 || shift_t->get_con() == (mask + 1))) {
     *     return lshift;
     *   }
     * }
     * return NULL;
     * }
     *
     * if (Matcher::match_rule_supported(Op_RotateRight) &&
     *     lopcode == Op_URShiftI && ropcode == Op_LShiftI && in(1)->in(1) == in(2)->in(1)) {
     *   Node* rshift = in(1)->in(2);
     *   Node* lshift = in(2)->in(2);
     *   Node* shift = rotate_shift(phase, rshift, lshift, 0x1F);
     *   if (shift != NULL) {
     *     return new RotateRightNode(in(1)->in(1), shift, TypeInt::INT);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XURShiftC1_Or_XLShiftC2_(int x, @Constant int rshift, @Constant int lshift) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_RIGHT)) {
            before((x >>> rshift) | (x << lshift));
            if ((rshift & 31) == 32 - (lshift & 31)) {
                after(Integer.rotateRight(x, rshift & 31));
            }
        }
    }

    /**
     * Convert (x << s) | (x >>> ({0|32} - s)) into rotateLeft(x, s).
     * <pre>{@code
     * // Find shift value for Integer or Long OR.
     * Node* rotate_shift(PhaseGVN* phase, Node* lshift, Node* rshift, int mask) {
     * // val << var_shift | val >> ({0|32|64} - var_shift) => rotate_left val, var_shift
     * if (rshift->Opcode() == Op_SubI && rshift->in(2) == lshift && rshift->in(1)->is_Con()){
     *   const TypeInt* shift_t = phase->type(rshift->in(1))->isa_int();
     *   if (shift_t != NULL && shift_t->is_con() &&
     *       (shift_t->get_con() == 0 || shift_t->get_con() == (mask + 1))) {
     *     return lshift;
     *   }
     * }
     * return NULL;
     * }
     *
     * Node* OrINode::Ideal(PhaseGVN* phase, bool can_reshape) {
     *   int lopcode = in(1)->Opcode();
     *   int ropcode = in(2)->Opcode();
     *   if (Matcher::match_rule_supported(Op_RotateLeft) &&
     *       lopcode == Op_LShiftI && ropcode == Op_URShiftI && in(1)->in(1) == in(2)->in(1)) {
     *     Node* lshift = in(1)->in(2);
     *     Node* rshift = in(2)->in(2);
     *     Node* shift = rotate_shift(phase, lshift, rshift, 0x1F);
     *     if (shift != NULL) {
     *       return new RotateLeftNode(in(1)->in(1), shift, TypeInt::INT);
     *     }
     *     return NULL;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XLShiftS_Or_XURShift_ConMinusS__(int x, int s, @Constant int con) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_LEFT)) {
            before((x << s) | (x >>> (con - s)));
            if (con == 0 || con == 32) {
                after(Integer.rotateLeft(x, s));
            }
        }
    }

    /**
     * Convert (x << s) | (x >>> ({0|32} - s)) into rotateLeft(x, s).
     * <pre>{@code
     * // Find shift value for Integer or Long OR.
     * Node* rotate_shift(PhaseGVN* phase, Node* lshift, Node* rshift, int mask) {
     * // val << var_shift | val >> ({0|32|64} - var_shift) => rotate_left val, var_shift
     * if (rshift->Opcode() == Op_SubI && rshift->in(2) == lshift && rshift->in(1)->is_Con()){
     *   const TypeInt* shift_t = phase->type(rshift->in(1))->isa_int();
     *   if (shift_t != NULL && shift_t->is_con() &&
     *       (shift_t->get_con() == 0 || shift_t->get_con() == (mask + 1))) {
     *     return lshift;
     *   }
     * }
     * return NULL;
     * }
     *
     * Node* OrINode::Ideal(PhaseGVN* phase, bool can_reshape) {
     *   int lopcode = in(1)->Opcode();
     *   int ropcode = in(2)->Opcode();
     *   if (Matcher::match_rule_supported(Op_RotateLeft) &&
     *       lopcode == Op_LShiftI && ropcode == Op_URShiftI && in(1)->in(1) == in(2)->in(1)) {
     *     Node* lshift = in(1)->in(2);
     *     Node* rshift = in(2)->in(2);
     *     Node* shift = rotate_shift(phase, lshift, rshift, 0x1F);
     *     if (shift != NULL) {
     *       return new RotateLeftNode(in(1)->in(1), shift, TypeInt::INT);
     *     }
     *     return NULL;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XURShiftS_Or_XLShift_ConMinusS__(int x, int s, @Constant int con) {
        if (Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_RIGHT)) {
            before((x >>> s) | (x << (con - s)));
            if (con == 0 || con == 32) {
                after(Integer.rotateRight(x, s));
            }
        }
    }

    /**
     * Convert MinI1( MinI2(a,b), c) into MinI1( a, MinI2(b,c) )
     * <pre>{@code
     * Node *l = in(1);
     * Node *r = in(2);
     * // Transform  MinI1( MinI2(a,b), c)  into  MinI1( a, MinI2(b,c) )
     * // to force a right-spline graph for the rest of MinINode::Ideal().
     * if( l->Opcode() == Op_MinI ) {
     *   r = phase->transform(new MinINode(l->in(2),r));
     *   l = l->in(1);
     *   set_req_X(1, l, phase);
     *   set_req_X(2, r, phase);
     *   return this;
     * }
     * }</pre>
     */
    @Pattern
    public void pMinAssociative(int a, int b, int c) {
        before(Math.min(Math.min(a, b), c));
        after(Math.min(a, Math.min(b, c)));
    }

    /**
     * Convert MIN2(x + c0, x + c1) into x + MIN2(c0, c1).
     * <pre>{@code
     * // Check if addition of an integer with type 't' and a constant 'c' can overflow
     * static bool can_overflow(const TypeInt* t, jint c) {
     *   jint t_lo = t->_lo;
     *   jint t_hi = t->_hi;
     *   return ((c < 0 && (java_add(t_lo, c) > t_lo)) ||
     *           (c > 0 && (java_add(t_hi, c) < t_hi)));
     * }
     *
     * // Get left input & constant
     * Node *x = l;
     * jint x_off = 0;
     * if( x->Opcode() == Op_AddI && // Check for "x+c0" and collect constant
     *     x->in(2)->is_Con() ) {
     *   const Type *t = x->in(2)->bottom_type();
     *   if( t == Type::TOP ) return NULL;  // No progress
     *   x_off = t->is_int()->get_con();
     *   x = x->in(1);
     * }
     *
     * // Scan a right-spline-tree for MINs
     * Node *y = r;
     * jint y_off = 0;
     * // Check final part of MIN tree
     * if( y->Opcode() == Op_AddI && // Check for "y+c1" and collect constant
     *     y->in(2)->is_Con() ) {
     *   const Type *t = y->in(2)->bottom_type();
     *   if( t == Type::TOP ) return NULL;  // No progress
     *   y_off = t->is_int()->get_con();
     *   y = y->in(1);
     * }
     * ...
     * const TypeInt* tx = phase->type(x)->isa_int();
     * ...
     * // Transform MIN2(x + c0, y + c1) into x + MIN2(c0, c1)
     * // if x == y and the additions can't overflow.
     * if (x == y && tx != NULL &&
     *     !can_overflow(tx, x_off) &&
     *     !can_overflow(tx, y_off)) {
     *   return new AddINode(x,phase->intcon(MIN2(x_off,y_off)));
     * }
     * }</pre>
     */
    @Pattern
    public void pAddDistributiveOverMin(int x, @Constant int c0, @Constant int c1) {
        before(Math.min(x + c0, x + c1));
        if (((c0 > 0 && Lib.getHi(x) + c0 > Lib.getHi(x)) || (c0 < 0 && Lib.getLo(x) + c0 < Lib.getLo(x)))
                && ((c1 > 0 && Lib.getHi(x) + c1 > Lib.getHi(x)) || (c1 < 0 && Lib.getLo(x) + c1 < Lib.getLo(x)))) {
            after(x + Math.min(c0, c1));
        }
    }

    /**
     * Convert MIN2(x + c0, MIN2(x + c1, z)) into MIN2(x + MIN2(c0, c1), z)
     * <pre>{@code
     * // Check if addition of an integer with type 't' and a constant 'c' can overflow
     * static bool can_overflow(const TypeInt* t, jint c) {
     *   jint t_lo = t->_lo;
     *   jint t_hi = t->_hi;
     *   return ((c < 0 && (java_add(t_lo, c) > t_lo)) ||
     *           (c > 0 && (java_add(t_hi, c) < t_hi)));
     * }
     *
     * // Get left input & constant
     * Node *x = l;
     * jint x_off = 0;
     * if( x->Opcode() == Op_AddI && // Check for "x+c0" and collect constant
     *     x->in(2)->is_Con() ) {
     *   const Type *t = x->in(2)->bottom_type();
     *   if( t == Type::TOP ) return NULL;  // No progress
     *   x_off = t->is_int()->get_con();
     *   x = x->in(1);
     * }
     *
     * // Scan a right-spline-tree for MINs
     * Node *y = r;
     * jint y_off = 0;
     * // Check final part of MIN tree
     * if( y->Opcode() == Op_AddI && // Check for "y+c1" and collect constant
     *     y->in(2)->is_Con() ) {
     *   const Type *t = y->in(2)->bottom_type();
     *   if( t == Type::TOP ) return NULL;  // No progress
     *   y_off = t->is_int()->get_con();
     *   y = y->in(1);
     * }
     * ...
     * const TypeInt* tx = phase->type(x)->isa_int();
     * ...
     * if( r->Opcode() == Op_MinI ) {
     *   y = r->in(1);
     *   // Check final part of MIN tree
     *   if( y->Opcode() == Op_AddI &&// Check for "y+c1" and collect constant
     *       y->in(2)->is_Con() ) {
     *     const Type *t = y->in(2)->bottom_type();
     *     if( t == Type::TOP ) return NULL;  // No progress
     *     y_off = t->is_int()->get_con();
     *     y = y->in(1);
     *   }
     *
     *   if( x->_idx > y->_idx )
     *     return new MinINode(r->in(1),phase->transform(new MinINode(l,r->in(2))));
     *
     *   // Transform MIN2(x + c0, MIN2(x + c1, z)) into MIN2(x + MIN2(c0, c1), z)
     *   // if x == y and the additions can't overflow.
     *   if (x == y && tx != NULL &&
     *       !can_overflow(tx, x_off) &&
     *       !can_overflow(tx, y_off)) {
     *     return new MinINode(phase->transform(new AddINode(x, phase->intcon(MIN2(x_off, y_off)))), r->in(2));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pAssociativeThenAddDistributiveOverMin(int x, int z, @Constant int c0, @Constant int c1) {
        before(Math.min(x + c0, Math.min(x + c1, z)));
        if (((c0 > 0 && Lib.getHi(x) + c0 > Lib.getHi(x)) || (c0 < 0 && Lib.getLo(x) + c0 < Lib.getLo(x)))
                && ((c1 > 0 && Lib.getHi(x) + c1 > Lib.getHi(x)) || (c1 < 0 && Lib.getLo(x) + c1 < Lib.getLo(x)))) {
            after(Math.min(x + Math.min(c0, c1), z));
        }
    }

    /**
     * Convert (x + 1) + ~y into x - y.
     * <p>
     * https://github.com/llvm/llvm-project/blob/aff115420d96e023a71f4cc7525462255cc455c5/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1337
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewAddNotXPlusOneToNegX1(int x, int y) {
        before((x + 1) + (y ^ -1));
        after(x - y);
    }

    /**
     * Convert ~y + (x + 1) into x - y.
     * <p>
     * https://github.com/llvm/llvm-project/blob/aff115420d96e023a71f4cc7525462255cc455c5/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1338
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewAddNotXPlusOneToNegX2(int x, int y) {
        before((y ^ -1) + (x + 1));
        after(x - y);
    }

    /**
     * Convert (~y + x) + 1 into x - y.
     * <p>
     * https://github.com/llvm/llvm-project/blob/aff115420d96e023a71f4cc7525462255cc455c5/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1339
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewAddNotXPlusOneToNegX3(int x, int y) {
        before(((y ^ -1) + x) + 1);
        after(x - y);
    }

    /**
     * Convert (x + ~y) + 1 into x - y.
     * <p>
     * https://github.com/llvm/llvm-project/blob/aff115420d96e023a71f4cc7525462255cc455c5/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1340
     */
    @Pattern
    @Origin("LLVM")
    @PR(7376)
    public void pNewAddNotXPlusOneToNegX4(int x, int y) {
        before((x + (y ^ -1)) + 1);
        after(x - y);
    }

    /**
     * Convert (0 - a) + (0 - b) into 0 - (a + b).
     * <p>
     * AddSub:1165
     * https://github.com/nunoplopes/alive/blob/d32f848ef2117be301a3a3f17cf0bf1cbc056e1c/tests/instcombine/addsub.opt#L124
     */
    @Pattern
    @Origin("LLVM")
    public void pNewAddAddSub1165(int a, int b) {
        before((0 - a) + (0 - b));
        after(0 - (a + b));
    }

    /**
     * Convert "x + x" into "x << 1".
     * <p>
     * AddSub:1156
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(6675)
    public void pNewAddAddSub1156(int x) {
        before(x + x);
        after(x << 1);
    }

    /**
     * Convert "~x + c" into "(c - 1) - x".
     * Note there isn't a bitwise not bytecode, "~x" would be
     * typically represented as "x^(-1)".
     * <p>
     * AddSub:1202
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(6858)
    @Shadows("pAddNotXPlusOne")
    public void pNewAddAddSub1202(int x, @Constant int c) {
        before((x ^ -1) + c);
        after((c - 1) - x);
    }

    /**
     * Covert "(x & y) + (x ^ y)" into "x | y".
     * <p>
     * AddSub:1295
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewAddAddSub1295(int x, int y) {
        before((x & y) + (x ^ y));
        after(x | y);
    }

    /**
     * Covert "(x ^ y) + (x & y)" into "x | y".
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewAddAddSub1295Sym(int x, int y) {
        before((x ^ y) + (x & y));
        after(x | y);
    }

    /**
     * Covert "(x & y) + (x | y)" into "x + y".
     * <p>
     * AddSub:1309
     * https://github.com/nunoplopes/alive/blob/master/tests/instcombine/addsub.opt
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewAddAddSub1309(int x, int y) {
        before((x & y) + (x | y));
        after(x + y);
    }

    /**
     * Covert "(x | y) + (x & y)" into "x + y".
     */
    @Pattern
    @Origin("LLVM")
    @PR(7395)
    public void pNewAddAddSub1309Sym(int x, int y) {
        before((x | y) + (x & y));
        after(x + y);
    }

    /**
     * Convert "(A + C1) + (C2 - B)" --> "(A - B) + (C1 + C2)"
     * <p>
     * https://github.com/llvm/llvm-project/blob/aff115420d96e023a71f4cc7525462255cc455c5/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1354
     */
    @Pattern
    @Origin("LLVM")
    public void pNewAdd_APlusC1_Plus_C2MinusB_(int A, @Constant int C1, @Constant int C2, int B) {
        before((A + C1) + (C2 - B));
        after((A - B) + (C1 + C2));
    }

    /**
     * Convert "X % C0 + (( X / C0 ) % C1) * C0" into "X % (C0 * C1)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L1364
     */
    @Pattern
    @Origin("LLVM")
    public void pNewAddXModC0PlusXDivC0ModC1MulC0(int X, @Constant int C0, @Constant int C1) {
        before(X % C0 + ((X / C0) % C1) * C0);
        after(X % (C0 * C1));
    }

    /**
     * Convert (((z | c2) ^ c1) + 1) + rhs into rhs - (z & c1), when
     * c2 == ~c1.
     * <p>
     * https://github.com/nunoplopes/alive/blob/d32f848ef2117be301a3a3f17cf0bf1cbc056e1c/tests/instcombine/addsub.opt#L1
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L782
     */
    @Pattern
    @Origin("LLVM")
    public void pNewAddAddSub1040(int z, @Constant int c2, @Constant int c1, int rhs) {
        before((((z | c2) ^ c1) + 1) + rhs);
        if (c2 == ~c1) {
            after(rhs - (z & c1));
        }
    }

    /**
     * Convert (((z & c2) ^ c1) + 1) + rhs into rhs - (z | ~c1), when
     * c2 == ~c1.
     * <p>
     * https://github.com/nunoplopes/alive/blob/d32f848ef2117be301a3a3f17cf0bf1cbc056e1c/tests/instcombine/addsub.opt#L12
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L788
     */
    @Pattern
    @Origin("LLVM")
    public void pNewAddAddSub1043(int z, @Constant int c2, @Constant int c1, int rhs) {
        before((((z & c2) ^ c1) + 1) + rhs);
        if (c2 == c1) {
            after(rhs - (z | ~c1));
        }
    }

    /**
     * Convert (X | C2) + C into (X | C2) ^ C2 iff (C2 == -C)
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAddSub.cpp#L902
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_XOrC2_PlusC(int X, @Constant int C2, @Constant int C) {
        before((X | C2) + C);
        if (C2 == -C) {
            after((X | C2) ^ C2);
        }
    }

    /**
     * Convert x + (con - y) into (x - y) + con.
     */
    @Pattern
    @Origin("OWN")
    @PR(7795)
    @Shadows({"pNewAddAddSub1165", "pNewAdd_APlusC1_Plus_C2MinusB_", "pAdd7"})
    public void pNewXPlus_ConMinusY_(int x, int y, @Constant int con) {
        before(x + (con - y));
        after((x - y) + con);
    }

    /**
     * Convert (con - y) + x into (x - y) + con.
     */
    @Pattern
    @Origin("OWN")
    @PR(7795)
    @Shadows({"pAdd1", "pAdd8", "pNewAddAddSub1165"})
    public void pNewXPlus_ConMinusY_Sym(int x, int y, @Constant int con) {
        before((con - y) + x);
        after((x - y) + con);
    }

    /**
     * Convert "~A | ~B" into ~(A & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1451
     */
    @Pattern
    @Origin("LLVM")
    public void pNewDeMorganLawOrToAnd(int A, int B) {
        before((A ^ -1) | (B ^ -1));
        after((A & B) ^ (-1));
    }

    /**
     * Convert "(A | ~B) | ~C" into "A | ~(B & C)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1476
     */
    @Pattern
    @Origin("LLVM")
    public void pNewDeMorganWithReassociationOrToAnd(int A, int B, int C) {
        before((A | (B ^ -1)) | (C ^ -1));
        after(A | ((B & C) ^ -1));
    }

    /**
     * Convert "(~B | A) | ~C" into "A | ~(B & C)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1477
     */
    @Pattern
    @Origin("LLVM")
    public void pNewDeMorganWithReassociationOrToAndSym(int A, int B, int C) {
        before(((B ^ -1) | A) | (C ^ -1));
        after(A | ((B & C) ^ -1));
    }

    /**
     * Convert (A & B) | ~(A | B) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1644
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndB_OrNot_AOrB_(int A, int B) {
        before((A & B) | ((A | B) ^ -1));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert ~(A | B) | (A & B) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1644
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndB_OrNot_AOrB_Sym(int A, int B) {
        before(((A | B) ^ -1) | (A & B));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (A & B) | ~(B | A) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1645
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndB_OrNot_BOrA_(int A, int B) {
        before((A & B) | ((B | A) ^ -1));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert ~(B | A) | (A & B) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1645
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndB_OrNot_BOrA_Sym(int A, int B) {
        before(((B | A) ^ -1) | (A & B));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (A ^ B) | ~(A | B) into ~(A & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1652
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AXorB_OrNot_AOrB_(int A, int B) {
        before((A ^ B) | ((A | B) ^ -1));
        after((A & B) ^ -1);
    }

    /**
     * Convert ~(A | B) | (A ^ B) into ~(A & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1652
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AXorB_OrNot_AOrB_Sym(int A, int B) {
        before(((A | B) ^ -1) | (A ^ B));
        after((A & B) ^ -1);
    }

    /**
     * Convert (A ^ B) | ~(B | A) into ~(A & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1653
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AXorB_OrNot_BOrA_(int A, int B) {
        before((A ^ B) | ((B | A) ^ -1));
        after((A & B) ^ -1);
    }

    /**
     * Convert ~(B | A) | (A ^ B) into ~(A & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1653
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AXorB_OrNot_BOrA_Sym(int A, int B) {
        before(((B | A) ^ -1) | (A ^ B));
        after((A & B) ^ -1);
    }

    /**
     * Convert (A & ~B) | (~A & B) into A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1659
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndNotB_Or_NotAAndB_1(int A, int B) {
        before((A & (B ^ -1)) | ((A ^ -1) & B));
        after(A ^ B);
    }

    /**
     * Convert (A & ~B) | (B & ~A) into A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1660
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndNotB_Or_NotAAndB_2(int A, int B) {
        before((A & (B ^ -1)) | (B & (A ^ -1)));
        after(A ^ B);
    }

    /**
     * Convert (~B & A) | (~A & B) into A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1661
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndNotB_Or_NotAAndB_3(int A, int B) {
        before(((B ^ -1) & A) | ((A ^ -1) & B));
        after(A ^ B);
    }

    /**
     * Convert (~B & A) | (B & ~A) into A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1662
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AAndNotB_Or_NotAAndB_4(int A, int B) {
        before(((B ^ -1) & A) | (B & (A ^ -1)));
        after(A ^ B);
    }

    /**
     * Convert (~(A | B) & C) | (~(A | C) & B) into (B ^ C) & ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1757
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_Or_Not_AOrC_AndB_(int A, int B, int C) {
        before((((A | B) ^ -1) & C) | ( ((A | C) ^ -1) & B));
        after((B ^ C) & (A ^ -1));
    }

    /**
     * Convert (~(A | B) & C) | (~(B | C) & A) into (A ^ C) & ~B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1767
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_Or_Not_BOrC_AndA_(int A, int B, int C) {
        before((((A | B) ^ -1) & C) | ( ((B | C) ^ -1) & A));
        after((A ^ C) & (B ^ -1));
    }

    /**
     * Convert (~(A | B) & C) | ~(A | C) into ~((B & C) | A).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1777
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_AOrC_(int A, int B, int C) {
        before((((A | B) ^ -1) & C) | ((A | C) ^ -1));
        after(((B & C) | A) ^ -1);
    }

    /**
     * Convert ~(A | C) | (~(A | B) & C) into ~((B & C) | A).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1777
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_AOrC_Sym(int A, int B, int C) {
        before(((A | C) ^ -1) | (((A | B) ^ -1) & C));
        after(((B & C) | A) ^ -1);
    }

    /**
     * Convert (~(A | B) & C) | ~(B | C) into ~((A & C) | B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1784
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_BOrC_(int A, int B, int C) {
        before((((A | B) ^ -1) & C) | ((B | C) ^ -1));
        after(((A & C) | B) ^ -1);
    }

    /**
     * Convert ~(B | C) | (~(A | B) & C) into ~((A & C) | B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1784
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_BOrC_Sym(int A, int B, int C) {
        before(((B | C) ^ -1) | (((A | B) ^ -1) & C));
        after(((A & C) | B) ^ -1);
    }

    /**
     * Convert  (~(A | B) & C) | ~(C | (A ^ B)) --> ~((A | B) & (C | (A ^ B)))
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1791
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_COr_AXorB__(int A, int B, int C) {
        before((((A | B) ^ -1) & C) | ((C | (A ^ B)) ^ -1));
        after(((A | B) & (C | (A ^ B))) ^ -1);
    }

    /**
     * Convert  ~(C | (A ^ B)) | (~(A | B) & C)  --> ~((A | B) & (C | (A ^ B)))
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1791
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AOrB_AndC_OrNot_COr_AXorB__Sym(int A, int B, int C) {
        before(((C | (A ^ B)) ^ -1) | (((A | B) ^ -1) & C));
        after(((A | B) & (C | (A ^ B))) ^ -1);
    }

    /**
     * Convert (~A & B & C) | ~(A | B | C) into ~(A | (B ^ C)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1822
     */
    @Pattern
    @Origin("LLVM")
    public void pNew__NotAAndB_AndC_OrNot__AOrB_OrC_(int A, int B, int C) {
        before((((A ^ -1) & B) & C) | (((A | B) | C) ^ -1));
        after((A | (B ^ C)) ^ -1);
    }

    /**
     * Convert ~(A | B | C) | (~A & B & C) into ~(A | (B ^ C)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1822
     */
    @Pattern
    @Origin("LLVM")
    public void pNew__NotAAndB_AndC_OrNot__AOrB_OrC_Sym(int A, int B, int C) {
        before((((A | B) | C) ^ -1) | (((A ^ -1) & B) & C));
        after((A | (B ^ C)) ^ -1);
    }

    /**
     * Convert (~A & B & C) | ~(A | B) into (C | ~B) & ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1727
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAAndBAndC_Or_Not_AOrB_(int A, int B, int C) {
        before(((A ^ -1) & B & C) | ((A | B) ^ -1));
        after((C | (B ^ -1)) & (A ^ -1));
    }

    /**
     * Convert ~(A | B) | (~A & B & C) into (C | ~B) & ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1727
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAAndBAndC_Or_Not_AOrB_Sym(int A, int B, int C) {
        before(((A | B) ^ -1) | ((A ^ -1) & B & C));
        after((C | (B ^ -1)) & (A ^ -1));
    }

    /**
     * Convert (~A & B & C) | ~(A | C) into (B | ~C) & ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1735
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAAndBAndC_Or_Not_AOrC_(int A, int B, int C) {
        before(((A ^ -1) & B & C) | ((A | C) ^ -1));
        after((B | (C ^ -1)) & (A ^ -1));
    }

    /**
     * Convert ~(A | C) | (~A & B & C) into (B | ~C) & ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1735
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAAndBAndC_Or_Not_AOrC_Sym(int A, int B, int C) {
        before(((A | C) ^ -1) | ((A ^ -1) & B & C));
        after((B | (C ^ -1)) & (A ^ -1));
    }
}
