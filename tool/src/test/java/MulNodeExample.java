import jog.api.*;
import static jog.api.Action.*;

public class MulNodeExample {

    @Group
    public void Ideal() {
        pMul1(0, 0);
        pMul2(0, 0);
        pMul3(0, 0);
        pMul4(0, 0, 0);
        pMul5(0, 0, 0);
        p_NegX_AndMinus1(0);
        p_XPlusCon1_LShiftCon0(0, 0, 0);
        p_XRShiftC0_LShiftC0(0, 0);
        p_XURShiftC0_LShiftC0(0, 0);
        p__XRShiftC0_AndY_LShiftC0(0, 0, 0);
        p__XURShiftC0_AndY_LShiftC0(0, 0, 0);
        p_XAndRightNBits_LShiftC0(0, 0, 0);
        p_XAndC0_RShiftC1(0, 0, 0);
        p_XURShiftA_URShiftB(0, 0, 0);
        p__XLShiftZ_PlusY_URShiftZ(0, 0, 0);
        p_XAndMask_URShiftZ(0, 0, 0);
        p_XLShiftZ_URShiftZ(0, 0);
        p_XRShiftN_URShift31(0, 0);
        pXRotateLeftC(0, 0);
        pNewDeMorganLawAndToOr(0, 0);
        pNewDeMorganWithReassociationAndToOr(0, 0, 0);
        pNewDeMorganWithReassociationAndToOrSym(0, 0, 0);
        pNew_AOrB_And_NotAAndB_(0, 0);
        pNew_AOrB_And_NotAAndB_Sym(0, 0);
        pNew_AOrB_And_NotBAndA_(0, 0);
        pNew_AOrB_And_NotBAndA_Sym(0, 0);
        pNew_AOrNotB_And_NotAOrB_1(0, 0);
        pNew_AOrNotB_And_NotAOrB_2(0, 0);
        pNew_AOrNotB_And_NotAOrB_3(0, 0);
        pNew_AOrNotB_And_NotAOrB_4(0, 0);
        pNew_Not_AAndB_OrC_And_Not_AAndC_OrB_(0, 0, 0);
        pNew_Not_AAndB_OrC_And_Not_BAndC_OrA_(0, 0, 0);
        pNew_Not_AAndB_OrC_AndNot_AAndC_(0, 0, 0);
        pNew_Not_AAndB_OrC_AndNot_AAndC_Sym(0, 0, 0);
        pNew_Not_AAndB_OrC_AndNot_BAndC_(0, 0, 0);
        pNew_Not_AAndB_OrC_AndNot_BAndC_Sym(0, 0, 0);
        pNew__NotAOrB_OrC_AndNot__AAndB_AndC_(0, 0, 0);
        pNew__NotAOrB_OrC_AndNot__AAndB_AndC_Sym(0, 0, 0);
        pNew_NotAOrBOrC_And_Not_AAndB_(0, 0, 0);
        pNew_NotAOrBOrC_And_Not_AAndB_Sym(0, 0, 0);
        pNew_NotAOrBOrC_And_Not_AAndC(0, 0, 0);
        pNew_NotAOrBOrC_And_Not_AAndCSym(0, 0, 0);
    }

    /**
     * Convert "(0-x)*(0-y)" into "x*y".
     *
     * <pre>{@code
     *  if (real_mul && in1->is_Sub() && in2->is_Sub()) {
     *    if (phase->type(in1->in(1))->is_zero_type() &&
     *        phase->type(in2->in(1))->is_zero_type()) {
     *     set_req(1, in1->in(2));
     *     set_req(2, in2->in(2));
     *     PhaseIterGVN* igvn = phase->is_IterGVN();
     *     if (igvn) {
     *      igvn->_worklist.push(in1);
     *      igvn->_worklist.push(in2);
     *     }
     *     in1 = in(1);
     *     in2 = in(2);
           progress = this;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pMul1(int x, int y) {
        before((0 - x) * (0 - y));
        after(x * y);
    }

    /**
     * Convert "max(x, y) * min(x, y)" into "x * y".
     *
     * <pre>{@code
     * if ((in(1)->Opcode() == max_opcode() && in(2)->Opcode() == min_opcode())
     *     || (in(1)->Opcode() == min_opcode() && in(2)->Opcode() == max_opcode())) {
     *   Node *in11 = in(1)->in(1);
     *   Node *in12 = in(1)->in(2);
     *
     *   Node *in21 = in(2)->in(1);
     *   Node *in22 = in(2)->in(2);
     *
     *   if ((in11 == in21 && in12 == in22) ||
     *       (in11 == in22 && in12 == in21)) {
     *     set_req(1, in11);
     *     set_req(2, in12);
     *     PhaseIterGVN* igvn = phase->is_IterGVN();
     *     if (igvn) {
     *       igvn->_worklist.push(in1);
     *       igvn->_worklist.push(in2);
     *     }
     *     in1 = in(1);
     *     in2 = in(2);
     *     progress = this;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pMul2(int x, int y) {
        before(Math.max(x, y) * Math.min(x, y));
        after(x * y);
    }

    /**
     * Convert "c * x" into "x * c".
     *
     * <pre>{@code
     * if( !(t2->singleton() ||(in(2)->is_Load() &&
     *     !(t1->singleton() || in(1)->is_Load())) ) ) {
     *   if( t1->singleton() || (in(1)->_idx > in(2)->_idx) ) {
     *     swap_edges(1, 2);
     *     const Type *t = t1;
     *     t1 = t2;
     *     t2 = t;
     *     progress = this;
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pMul3(int x, @Constant int c) {
        before(c * x);
        after(x * c);
    }

    /**
     * Convert "(x * c1) * c2" into "x * (c1 * c2)".
     *
     * <pre>{@code
     * if( t2->singleton() &&                   // Right input is a constant?
     *     op != Op_MulF &&                    // Float & double cannot reassociate
     *     op != Op_MulD ) {
     *   if( t2 == Type::TOP ) return NULL;
     *   Node *mul1 = in(1);                     // Get left node
     *   if( mul1->Opcode() == mul_opcode() ) {  // Left node is a multiply?
     *     const Type *t12 = phase->type( mul1->in(2) );
     *     if( t12->singleton() && t12 != Type::TOP) { // Left input is a multiply of a constant (x * c1)?
     *       // Compute new constant; check for overflow
     *       const Type *tcon01 = ((MulNode*)mul1)->mul_ring(t2,t12);
     *       if( tcon01->singleton() ) {                   // Check for overflow
     *         set_req_X(1, mul1->in(1), phase);            // Make left node x
     *         set_req_X(2, phase->makecon(tcon01), phase);
     *         t2 = tcon01;                                 // Make right node (c1 * c2)
     *         progress = this;      // Made progress
     *       }
     *     }
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pMul4(int x, @Constant int c1, @Constant int c2) {
        before((x * c1) * c2);
        if (Lib.getType(c2) != Lib.Type.TOP) {
            after(x * (c1 * c2));
        }
    }

    /**
     * Convert "(x + c1) * c2" into "(x * c2) + (c1 * c2)".
     *
     * <pre>{@code
     * if( t2->singleton() &&                   // Right input is a constant?
     *     op != Op_MulF &&                    // Float & double cannot reassociate
     *     op != Op_MulD ) {
     *   if( t2 == Type::TOP ) return NULL;
     *   const Node *add1 = in(1);               // Get left node
     *   if( add1->Opcode() == add_opcode() ) {  // Left input is an add?
     *     const Type *t12 = phase->type( add1->in(2) );
     *     if( t12->singleton() && t12 != Type::TOP ) { // Left input is an add of a constant?
     *       const Type *tcon01 = mul_ring(t2,t12); // Compute new constant (c1 * c2)
     *       if( tcon01->singleton() ) {            // Check for overflow
     *
     *         Node *mul = clone();                 // mul = () * c2
     *         mul->set_req(1,add1->in(1));         // mul = (x * c2)
     *         mul = phase->transform(mul);
     *
     *         Node *add2 = add1->clone();
     *         add2->set_req(1, mul);               // (x * c2) + (c1 * c2)
     *         add2->set_req(2, phase->makecon(tcon01) );
     *         progress = add2;
     *       }
     *     }
     *   } // End of is left input an add
     * }
     * }</pre>
     */
    @Pattern
    public void pMul5(int x, @Constant int c1, @Constant int c2) {
        before((x + c1) * c2);
        if (Lib.getType(c2) != Lib.Type.TOP) {
            after((x * c2) + (c1 * c2));
        }
    }

    /**
     * Convert (0 - x) & 1 into x & 1.
     * <pre>{@code
     * // Check for 'negate/and-1', a pattern emitted when someone asks for
     * // 'mod 2'.  Negate leaves the low order bit unchanged (think: complement
     * // plus 1) and the mask is of the low order bit.  Skip the negate.
     * if( lop == Op_SubI && mask == 1 && load->in(1) &&
     *     phase->type(load->in(1)) == TypeInt::ZERO )
     *   return new AndINode( load->in(2), in(2) );
     * }</pre>
     */
    @Pattern
    public void p_NegX_AndMinus1(int x) {
        before((0 - x) & 1);
        after(x & 1);
    }

    /**
     * Convert (X + con1) << con0 into (x << con0) + (con1 << con0).
     * <pre>{@code
     * if( add1_op == Op_AddI ) {    // Left input is an add?
     *
     *   // Transform is legal, but check for profit.  Avoid breaking 'i2s'
     *   // and 'i2b' patterns which typically fold into 'StoreC/StoreB'.
     *   if( con < 16 ) {
     *     // Left input is an add of a constant?
     *     const TypeInt *t12 = phase->type(add1->in(2))->isa_int();
     *     if( t12 && t12->is_con() ){ // Left input is an add of a con?
     *       // Compute X << con0
     *       Node *lsh = phase->transform( new LShiftINode( add1->in(1), in(2) ) );
     *       // Compute X<<con0 + (con1<<con0)
     *       return new AddINode( lsh, phase->intcon(t12->get_con() << con));
     *     }
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XPlusCon1_LShiftCon0(int x, @Constant int con1, @Constant int con0) {
        before((x + con1) << con0);
        if (con0 < 16) {
            after((x << con0) + (con1 << con0));
        }
    }

    /**
     * Convert (x>>c0)<<c0 into x & -(1<<c0).
     * <pre>{@code
     * // Check for "(x>>c0)<<c0" which just masks off low bits
     * if( (add1_op == Op_RShiftI || add1_op == Op_URShiftI ) &&
     *     add1->in(2) == in(2) )
     *   // Convert to "(x & -(1<<c0))"
     *   return new AndINode(add1->in(1),phase->intcon( -(1<<con)));
     * }</pre>
     */
    @Pattern
    public void p_XRShiftC0_LShiftC0(int x, @Constant int c0) {
        before((x >> c0) << c0);
        after(x & -(1 << c0));
    }

    /**
     * Convert (x>>>c0)<<c0 into x & -(1<<c0).
     * <pre>{@code
     * // Check for "(x>>c0)<<c0" which just masks off low bits
     * if( (add1_op == Op_RShiftI || add1_op == Op_URShiftI ) &&
     *     add1->in(2) == in(2) )
     *   // Convert to "(x & -(1<<c0))"
     *   return new AndINode(add1->in(1),phase->intcon( -(1<<con)));
     * }</pre>
     */
    @Pattern
    public void p_XURShiftC0_LShiftC0(int x, @Constant int c0) {
        before((x >>> c0) << c0);
        after(x & -(1 << c0));
    }

    /**
     * Convert ((x>>c0) & y)<<c0 into "(x & (y<<c0))"
     * <pre>{@code
     * // Check for "((x>>c0) & Y)<<c0" which just masks off more low bits
     * if( add1_op == Op_AndI ) {
     *   Node *add2 = add1->in(1);
     *   int add2_op = add2->Opcode();
     *   if( (add2_op == Op_RShiftI || add2_op == Op_URShiftI ) &&
     *       add2->in(2) == in(2) ) {
     *     // Convert to "(x & (Y<<c0))"
     *     Node *y_sh = phase->transform( new LShiftINode( add1->in(2), in(2) ) );
     *     return new AndINode( add2->in(1), y_sh );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p__XRShiftC0_AndY_LShiftC0(int x, int y, @Constant int c0) {
        before(((x>>c0) & y)<<c0);
        after((x & (y<<c0)));
    }

    /**
     * Convert ((x>>>c0) & y)<<c0 into "(x & (y<<c0))"
     * <pre>{@code
     * // Check for "((x>>c0) & Y)<<c0" which just masks off more low bits
     * if( add1_op == Op_AndI ) {
     *   Node *add2 = add1->in(1);
     *   int add2_op = add2->Opcode();
     *   if( (add2_op == Op_RShiftI || add2_op == Op_URShiftI ) &&
     *       add2->in(2) == in(2) ) {
     *     // Convert to "(x & (Y<<c0))"
     *     Node *y_sh = phase->transform( new LShiftINode( add1->in(2), in(2) ) );
     *     return new AndINode( add2->in(1), y_sh );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p__XURShiftC0_AndY_LShiftC0(int x, int y, @Constant int c0) {
        before(((x>>>c0) & y)<<c0);
        after((x & (y<<c0)));
    }

    /**
     * Convert ((x & ((1<<(32-c0))-1)) << c0) into x << c0.
     * <pre>{@code
     * // Check for ((x & ((1<<(32-c0))-1)) << c0) which ANDs off high bits
     * // before shifting them away.
     * const jint bits_mask = right_n_bits(BitsPerJavaInteger-con);
     * if( add1_op == Op_AndI &&
     *     phase->type(add1->in(2)) == TypeInt::make( bits_mask ) )
     *   return new LShiftINode( add1->in(1), in(2) );
     * }</pre>
     */
    @Pattern
    public void p_XAndRightNBits_LShiftC0(int x, @Constant int c1, @Constant int c0) {
        before((x & c1) << c0);
        if (c1 == (1 << (32 - c0)) - 1) {
            after(x << c0);
        }
    }

    /**
     * Convert (x & mask) >> shift into (x >> shift) & (mask >> shift).
     * <pre>{@code
     * // Check for (x & 0xFF000000) >> 24, whose mask can be made smaller.
     * // Such expressions arise normally from shift chains like (byte)(x >> 24).
     * const Node *mask = in(1);
     * if( mask->Opcode() == Op_AndI &&
     *     (t3 = phase->type(mask->in(2))->isa_int()) &&
     *     t3->is_con() ) {
     *   Node *x = mask->in(1);
     *   jint maskbits = t3->get_con();
     *   // Convert to "(x >> shift) & (mask >> shift)"
     *   Node *shr_nomask = phase->transform( new RShiftINode(mask->in(1), in(2)) );
     *   return new AndINode(shr_nomask, phase->intcon( maskbits >> shift));
     * }
     * }</pre>
     */
    @Pattern
    public void p_XAndC0_RShiftC1(int x, @Constant int mask, @Constant int shift) {
        before((x & mask) >> shift);
        after((x >> shift) & (mask >> shift));
    }

    /**
     * Convert ((x>>>a)>>>b) into (x>>>(a+b)) when a+b < 32
     * <pre>{@code
     * // Check for ((x>>>a)>>>b) and replace with (x>>>(a+b)) when a+b < 32
     * if( in1_op == Op_URShiftI ) {
     *   const TypeInt *t12 = phase->type( in(1)->in(2) )->isa_int();
     *   if( t12 && t12->is_con() ) { // Right input is a constant
     *     const int con2 = t12->get_con() & 31; // Shift count is always masked
     *     const int con3 = con+con2;
     *     if( con3 < 32 )           // Only merge shifts if total is < 32
     *       return new URShiftINode( in(1)->in(1), phase->intcon(con3) );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XURShiftA_URShiftB(int x, @Constant int a, @Constant int b) {
        before((x >>> a) >>> b);
        a = a & 31;
        if (a + b < 32) {
            after(x >>> (a + b));
        }
    }

    /**
     * Convert ((X << Z) + Y) >>> Z into (X + (Y>>>Z)) & Z-((1 << (32 - Z)) - 1).
     * <pre>{@code
     * // Check for ((x << z) + Y) >>> z.  Replace with x + con>>>z
     * // The idiom for rounding to a power of 2 is "(Q+(2^z-1)) >>> z".
     * // If Q is "X << z" the rounding is useless.  Look for patterns like
     * // ((X<<Z) + Y) >>> Z  and replace with (X + Y>>>Z) & Z-mask.
     * Node *add = in(1);
     * const TypeInt *t2 = phase->type(in(2))->isa_int();
     * if (in1_op == Op_AddI) {
     *   Node *lshl = add->in(1);
     *   if( lshl->Opcode() == Op_LShiftI &&
     *       phase->type(lshl->in(2)) == t2 ) {
     *     Node *y_z = phase->transform( new URShiftINode(add->in(2),in(2)) );
     *     Node *sum = phase->transform( new AddINode( lshl->in(1), y_z ) );
     *     return new AndINode( sum, phase->intcon(mask) );
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p__XLShiftZ_PlusY_URShiftZ(int X, int Y, @Constant int Z) {
        before(((X << Z) + Y) >>> Z);
        int t = ((1 << (32 - Z)) - 1);
        after((X + (Y >>> Z)) & t);
    }

    /**
     * Convert (x & mask) >>> z into (x >>> z) & (mask >> z).
     * <pre>{@code
     * Node *andi = in(1);
     * if( in1_op == Op_AndI ) {
     *   const TypeInt *t3 = phase->type( andi->in(2) )->isa_int();
     *   if( t3 && t3->is_con() ) { // Right input is a constant
     *     jint mask2 = t3->get_con();
     *     mask2 >>= con;  // *signed* shift downward (high-order zeroes do not help)
     *     Node *newshr = phase->transform( new URShiftINode(andi->in(1), in(2)) );
     *     return new AndINode(newshr, phase->intcon(mask2));
     *     // The negative values are easier to materialize than positive ones.
     *     // A typical case from address arithmetic is ((x & ~15) >> 4).
     *     // It's better to change that to ((x >> 4) & ~0) versus
     *     // ((x >> 4) & 0x0FFFFFFF).  The difference is greatest in LP64.
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XAndMask_URShiftZ(int x, @Constant int mask, @Constant int z) {
        before((x & mask) >>> z);
        after((x >>> z) & (mask >> z));
    }

    /**
     * Convert (X << z) >>> z into X & ((1 << (32 - z)) - 1).
     * <pre>{@code
     * // Check for "(X << z ) >>> z" which simply zero-extends
     * Node *shl = in(1);
     * if( in1_op == Op_LShiftI &&
     *     phase->type(shl->in(2)) == t2 )
     *   return new AndINode( shl->in(1), phase->intcon(mask) );
     * }</pre>
     */
    @Pattern
    public void p_XLShiftZ_URShiftZ(int X, @Constant int z) {
        before((X << z) >>> z);
        after(X & ((1 << (32 - z)) - 1));
    }

    /**
     * Convert (x >> n) >>> 31 into x >>> 31.
     * <pre>{@code
     * // Check for (x >> n) >>> 31. Replace with (x >>> 31)
     * Node *shr = in(1);
     * if ( in1_op == Op_RShiftI ) {
     *   Node *in11 = shr->in(1);
     *   Node *in12 = shr->in(2);
     *   const TypeInt *t11 = phase->type(in11)->isa_int();
     *   const TypeInt *t12 = phase->type(in12)->isa_int();
     *   if ( t11 && t2 && t2->is_con(31) && t12 && t12->is_con() ) {
     *     return new URShiftINode(in11, phase->intcon(31));
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void p_XRShiftN_URShift31(int x, @Constant int n) {
        before((x >> n) >>> 31);
        after(x >>> 31);
    }

    /**
     * Convert "x rotateLeft c" into "x rotateRight (32 - (c & 31))";
     * <pre>{@code
     * if (t2->isa_int() && t2->is_int()->is_con()) {
     *   if (t1->isa_int()) {
     *     int lshift = t2->is_int()->get_con() & 31;
     *     return new RotateRightNode(in(1), phase->intcon(32 - (lshift & 31)), TypeInt::INT);
     *   }
     * }
     * }</pre>
     */
    @Pattern
    public void pXRotateLeftC(int x, @Constant int c) {
        before(Integer.rotateLeft(x, c));
        after(Integer.rotateRight(x, 32 - (c & 31)));
    }

    /**
     * Convert "~A & ~B" into ~(A | B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1450
     */
    @Pattern
    @Origin("LLVM")
    @PR(16333)
    public void pNewDeMorganLawAndToOr(int A, int B) {
        before((A ^ -1) & (B ^ -1));
        after((A | B) ^ (-1));
    }

    /**
     * Convert "(A & ~B) & ~C" into "A & ~(B | C)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1474
     */
    @Pattern
    @Origin("LLVM")
    public void pNewDeMorganWithReassociationAndToOr(int A, int B, int C) {
        before((A & (B ^ -1)) & (C ^ -1));
        after(A & ((B | C) ^ -1));
    }

    /**
     * Convert "(~B & A) & ~C" into "A & ~(B | C)".
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1475
     */
    @Pattern
    @Origin("LLVM")
    public void pNewDeMorganWithReassociationAndToOrSym(int A, int B, int C) {
        before(((B ^ -1) & A) & (C ^ -1));
        after(A & ((B | C) ^ -1));
    }

    /**
     * Convert (A | B) & ~(A & B) into (A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1618
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrB_And_NotAAndB_(int A, int B) {
        before((A | B) & ((A & B) ^ -1));
        after(A ^ B);
    }

    /**
     * Convert ~(A & B) & (A | B) into (A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1618
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrB_And_NotAAndB_Sym(int A, int B) {
        before(((A & B) ^ -1) & (A | B));
        after(A ^ B);
    }

    /**
     * Convert (A | B) & ~(B & A) --> A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1619
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrB_And_NotBAndA_(int A, int B) {
        before((A | B) & ((B & A) ^ -1));
        after(A ^ B);
    }

    /**
     * Convert (A | B) & ~(B & A) --> A ^ B.
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1619
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrB_And_NotBAndA_Sym(int A, int B) {
        before(((B & A) ^ -1) & (A | B));
        after(A ^ B);
    }

    /**
     * Convert (A | ~B) & (~A | B) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1624
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrNotB_And_NotAOrB_1(int A, int B) {
        before((A | (B ^ -1)) & ((A ^ -1) | B));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (A | ~B) & (B | ~A) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1625
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrNotB_And_NotAOrB_2(int A, int B) {
        before((A | (B ^ -1)) & (B | (A ^ -1)));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (~B | A) & (~A | B) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1626
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrNotB_And_NotAOrB_3(int A, int B) {
        before(((B ^ -1) | A) & ((A ^ -1) | B));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (~B | A) & (B | ~A) into ~(A ^ B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1627
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_AOrNotB_And_NotAOrB_4(int A, int B) {
        before(((B ^ -1) | A) & (B | (A ^ -1)));
        after((A ^ B) ^ -1);
    }

    /**
     * Convert (~(A & B) | C) & (~(A & C) | B) into ~((B ^ C) & A).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1758
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_And_Not_AAndC_OrB_(int A, int B, int C) {
        before((((A & B) ^ -1) | C) & ( ((A & C) ^ -1) | B));
        after(((B ^ C) & A) ^ -1);
    }

    /**
     * Convert (~(A & B) | C) & (~(B & C) | A) into ~((A ^ C) & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1768
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_And_Not_BAndC_OrA_(int A, int B, int C) {
        before((((A & B) ^ -1) | C) & ( ((B & C) ^ -1) | A));
        after(((A ^ C) & B) ^ -1);
    }

    /**
     * Convert (~(A & B) | C) & ~(A & C) into ~((B | C) & A).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1778
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_AndNot_AAndC_(int A, int B, int C) {
        before((((A & B) ^ -1) | C) & ((A & C) ^ -1));
        after(((B | C) & A) ^ -1);
    }

    /**
     * Convert ~(A & C) & (~(A & B) | C) into ~((B | C) & A).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1778
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_AndNot_AAndC_Sym(int A, int B, int C) {
        before(((A & C) ^ -1) & (((A & B) ^ -1) | C));
        after(((B | C) & A) ^ -1);
    }

    /**
     * Convert (~(A & B) | C) & ~(B & C) into ~((A | C) & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1785
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_AndNot_BAndC_(int A, int B, int C) {
        before((((A & B) ^ -1) | C) & ((B & C) ^ -1));
        after(((A | C) & B) ^ -1);
    }

    /**
     * Convert ~(B & C) & (~(A & B) | C) into ~((A | C) & B).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1785
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_Not_AAndB_OrC_AndNot_BAndC_Sym(int A, int B, int C) {
        before(((B & C) ^ -1) & (((A & B) ^ -1) | C));
        after(((A | C) & B) ^ -1);
    }

    /**
     * Convert (~A | B | C) & ~(A & B & C) into (~A | (B ^ C)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1823
     */
    @Pattern
    @Origin("LLVM")
    public void pNew__NotAOrB_OrC_AndNot__AAndB_AndC_(int A, int B, int C) {
        before((((A ^ -1) | B) | C) & (((A & B) & C) ^ -1));
        after((A ^ -1) | (B ^ C));
    }

    /**
     * Convert ~(A & B & C) & (~A | B | C) into (~A | (B ^ C)).
     * <p>
     * https://github.com/llvm/llvm-project/blob/103e1d934a353ba233f854d992e5429106d3fbac/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1823
     */
    @Pattern
    @Origin("LLVM")
    public void pNew__NotAOrB_OrC_AndNot__AAndB_AndC_Sym(int A, int B, int C) {
        before((((A & B) & C) ^ -1) & (((A ^ -1) | B) | C));
        after((A ^ -1) | (B ^ C));
    }

    /**
     * Convert (~A | B | C) & ~(A & B) into (C & ~B) | ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1728
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAOrBOrC_And_Not_AAndB_(int A, int B, int C) {
        before(((A ^ -1) | B | C) & ((A & B) ^ -1));
        after((C & (B ^ -1)) | (A ^ -1));
    }

    /**
     * Convert ~(A & B) & (~A | B | C) into (C & ~B) | ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1728
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAOrBOrC_And_Not_AAndB_Sym(int A, int B, int C) {
        before(((A & B) ^ -1) & ((A ^ -1) | B | C));
        after((C & (B ^ -1)) | (A ^ -1));
    }

    /**
     * Convert (~A | B | C) & ~(A & C) into (B & ~C) | ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1736
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAOrBOrC_And_Not_AAndC(int A, int B, int C) {
        before(((A ^ -1) | B | C) & ((A & C) ^ -1));
        after((B & (C ^ -1)) | (A ^ -1));
    }

    /**
     * Convert ~(A & C) & (~A | B | C) into (B & ~C) | ~A.
     * <p>
     * https://github.com/llvm/llvm-project/blob/3e52c0926c22575d918e7ca8369522b986635cd3/llvm/lib/Transforms/InstCombine/InstCombineAndOrXor.cpp#L1736
     */
    @Pattern
    @Origin("LLVM")
    public void pNew_NotAOrBOrC_And_Not_AAndCSym(int A, int B, int C) {
        before(((A & C) ^ -1) & ((A ^ -1) | B | C));
        after((B & (C ^ -1)) | (A ^ -1));
    }
}
