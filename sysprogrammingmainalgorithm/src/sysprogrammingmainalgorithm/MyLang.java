package sysprogrammingmainalgorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class MyLang {
    private boolean create;
    private int LLK;
    private LinkedList<Node> language;
    private LinkedList<TableNode> lexemaTable;
    private int[] terminals;
    private int[] nonterminals;
    private LlkContext[] termLanguarge;
    private LlkContext[] firstK;

    public MyLang(final String fileLang, final int llk1) {
        this.LLK = llk1;
        this.create = false;
        this.language = new LinkedList<Node>();
        this.lexemaTable = new LinkedList<TableNode>();
        this.readGrammat(fileLang);
        this.terminals = this.createTerminals();
        this.nonterminals = this.createNonterminals();
        this.termLanguarge = this.createTerminalLang();
    }

    private int[] createTerminals() {
        int count = 0;
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaCode() > 0) {
                ++count;
            }
        }
        final int[] terminal = new int[count];
        count = 0;
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaCode() > 0) {
                terminal[count] = tmp.getLexemaCode();
                ++count;
            }
        }
        return terminal;
    }

    private int[] createNonterminals() {
        int count = 0;
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaCode() < 0) {
                ++count;
            }
        }
        final int[] nonterminal = new int[count];
        count = 0;
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaCode() < 0) {
                nonterminal[count] = tmp.getLexemaCode();
                ++count;
            }
        }
        return nonterminal;
    }

    private LlkContext[] createTerminalLang() {
        final LlkContext[] cont = new LlkContext[this.terminals.length];
        for (int ii = 0; ii < this.terminals.length; ++ii) {
            final int[] trmWord = { this.terminals[ii] };
            (cont[ii] = new LlkContext()).addWord(trmWord);
        }
        return cont;
    }


    public int[] getNonTerminals() {
        return this.nonterminals;
    }

    public LlkContext[] getFirstK() {
        return this.firstK;
    }

    public boolean isCreate() {
        return this.create;
    }

    public String getLexemaText(final int code) {
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaCode() == code) {
                return tmp.getLexemaText();
            }
        }
        return null;
    }

    public int[] getTerminals() {
        return this.terminals;
    }

    public LlkContext[] getLlkTrmContext() {
        return this.termLanguarge;
    }

    public int getLlkConst() {
        return this.LLK;
    }

    public void setFirstK(final LlkContext[] first) {
        this.firstK = first;
    }

    public void printGramma() {
        int count = 0;
        for (final Node tmp : this.language) {
            final int[] roole = tmp.getRoole();
            ++count;
            System.out.print("" + count);
            for (int ii = 0; ii < roole.length; ++ii) {
                if (ii == 1) {
                    System.out.print(" ::= ");
                }
                System.out.print(this.getLexemaText(roole[ii]) + " ");
                if (roole.length == 1) {
                    System.out.print(" ::= ");
                }
            }
            System.out.println("");
        }
    }

    private void readGrammat(final String fname) {
        final char[] lexema = new char[180];
        final int[] roole = new int[80];
        BufferedReader s;
        try {
            s = new BufferedReader(new FileReader(fname));
        }
        catch (FileNotFoundException ee) {
            System.out.print("File: " + fname + "not found\n");
            this.create = false;
            return;
        }
        for (int ii = 0; ii < lexema.length; ++ii) {
            lexema[ii] = '\0';
        }
        final int[] rule = new int[80];
        for (int ii2 = 0; ii2 < rule.length; ++ii2) {
            rule[ii2] = 0;
        }
        int pos = 0;
        int q = 0;
        int posRoole = 0;
        int line = 0;
        String readline = null;
        int readpos = 0;
        int readlen = 0;
        try {
            while (s.ready()) {
                if (readline == null || readpos >= readlen) {
                    readline = s.readLine();
                    if (line == 0 && readline.charAt(0) == '\ufeff') {
                        readline = readline.substring(1);
                    }
                    readlen = readline.length();
                    ++line;
                }
                for (readpos = 0; readpos < readlen; ++readpos) {
                    final char symbol = readline.charAt(readpos);
                    switch (q) {
                        case 0: {
                            if (symbol == ' ' || symbol == '\t' || symbol == '\r' || symbol == '\n') {
                                break;
                            }
                            if (symbol == '\b') {
                                break;
                            }
                            if (readpos == 0 && posRoole > 0 && (symbol == '!' || symbol == '#')) {
                                final Node nod = new Node(roole, posRoole);
                                this.language.add(nod);
                                if (symbol == '!') {
                                    posRoole = 1;
                                    break;
                                }
                                posRoole = 0;
                            }
                            pos = 0;
                            if ((lexema[pos++] = symbol) == '#') {
                                q = 1;
                                break;
                            }
                            if (symbol == '\\') {
                                --pos;
                                q = 3;
                                break;
                            }
                            q = 2;
                            break;
                        }
                        case 1: {
                            lexema[pos++] = symbol;
                            if (symbol == '#' || readpos == 0) {
                                final String strTmp = new String(lexema, 0, pos);
                                final TableNode nodeTmp = new TableNode(strTmp, Integer.MIN_VALUE);
                                int ii3 = 1;
                                for (final TableNode tmp : this.lexemaTable) {
                                    if (nodeTmp.equals(tmp)) {
                                        ii3 = 0;
                                        break;
                                    }
                                }
                                if (ii3 == 1) {
                                    this.lexemaTable.add(nodeTmp);
                                }
                                final int newLexemaCode = this.getLexemaCode(strTmp, Integer.MIN_VALUE);
                                roole[posRoole++] = newLexemaCode;
                                pos = 0;
                                q = 0;
                                break;
                            }
                            break;
                        }
                        case 2: {
                            if (symbol == '\\') {
                                --pos;
                                q = 3;
                                break;
                            }
                            if (symbol == ' ' || readpos == 0 || symbol == '#' || symbol == '\r' || symbol == '\t') {
                                final String strTmp = new String(lexema, 0, pos);
                                final TableNode nodeTmp = new TableNode(strTmp, 268435456);
                                int ii3 = 1;
                                for (final TableNode tmp : this.lexemaTable) {
                                    if (nodeTmp.equals(tmp)) {
                                        ii3 = 0;
                                        break;
                                    }
                                }
                                if (ii3 == 1) {
                                    this.lexemaTable.add(nodeTmp);
                                }
                                final int newLexemaCode = this.getLexemaCode(strTmp, 268435456);
                                roole[posRoole++] = newLexemaCode;
                                pos = 0;
                                q = 0;
                                --readpos;
                                break;
                            }
                            lexema[pos++] = symbol;
                            break;
                        }
                        case 3: {
                            lexema[pos++] = symbol;
                            q = 2;
                            break;
                        }
                    }
                }
            }
            if (pos != 0) {
                int code;
                if (q == 1) {
                    code = Integer.MIN_VALUE;
                }
                else {
                    code = 268435456;
                }
                final String strTmp2 = new String(lexema, 0, pos);
                final TableNode nodeTmp = new TableNode(strTmp2, code);
                int ii4 = 1;
                for (final TableNode tmp2 : this.lexemaTable) {
                    if (nodeTmp.equals(tmp2)) {
                        ii4 = 0;
                        break;
                    }
                }
                if (ii4 == 1) {
                    this.lexemaTable.add(nodeTmp);
                }
                final int newLexemaCode = this.getLexemaCode(strTmp2, code);
                roole[posRoole++] = newLexemaCode;
            }
            if (posRoole > 0) {
                final Node nod = new Node(roole, posRoole);
                this.language.add(nod);
            }
            this.create = true;
        }
        catch (IOException e) {
            System.out.println(e.toString());
            this.create = false;
        }
    }

    private int getLexemaCode(final String lexema, final int lexemaClass) {
        for (final TableNode tmp : this.lexemaTable) {
            if (tmp.getLexemaText().equals(lexema) && (tmp.getLexemaCode() & 0xFF000000) == lexemaClass) {
                return tmp.getLexemaCode();
            }
        }
        return 0;
    }

    private int[] newWord(final int LLK, final LlkContext[] tmp, final int[] mult, final int realCalc) {
        final int[] word = new int[LLK];
        int llkTmp = 0;
        for (int ii = 0; ii < realCalc; ++ii) {
            if (tmp[ii] == null) {
                System.out.println("\u041d\u0435 \u043e\u0442\u0440\u0438\u043c\u0430\u043b\u0438 \u043c\u043e\u0432\u0438 \u0437 \u0456\u043d\u0434\u0435\u0441\u043e\u043c " + ii);
            }
            final int[] wordtmp = tmp[ii].getWord(mult[ii]);
            if (wordtmp == null) {
                System.out.println("\u041d\u0435 \u043e\u0442\u0440\u0438\u043c\u0430\u043b\u0438 \u0435\u043b\u0435\u043c\u0435\u043d\u0442\u0430 \u043f\u043e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0456 " + ii + " \u0437 \u0456\u043d\u0434\u0435\u0441\u043e\u043c " + mult[ii]);
            }
            for (int ii2 = 0; ii2 < wordtmp.length && llkTmp != LLK; word[llkTmp++] = wordtmp[ii2], ++ii2) {}
            if (llkTmp == LLK) {
                break;
            }
        }
        final int[] word2 = new int[llkTmp];
        for (int ii = 0; ii < llkTmp; ++ii) {
            word2[ii] = word[ii];
        }
        return word2;
    }

    private boolean newCalcIndex(final int[] mult, final int[] maxmult, final int realCalc) {
        for (int ii = realCalc - 1; ii >= 0; --ii) {
            if (mult[ii] + 1 != maxmult[ii]) {
                final int n = ii;
                ++mult[n];
                return true;
            }
            mult[ii] = 0;
        }
        return false;
    }

    public LlkContext[] firstK() {
        int[] llkWord = new int[10];
        boolean upr;
        final int[] term = this.getTerminals();
        final int[] nonterm = this.getNonTerminals();
        final LlkContext[] llkTrmContext = this.getLlkTrmContext();
        final LlkContext[] result = new LlkContext[nonterm.length];
        final LlkContext[] tmpLlk = new LlkContext[40];
        final int[] mult = new int[40];
        final int[] maxmult = new int[40];
        for (int ii = 0; ii < result.length; ++ii) {
            result[ii] = new LlkContext();
        }
        do {
            upr = false;
            for (final Node tmp : this.language) {
                int ii = 0;
                int[] tmprole;
                for (tmprole = tmp.getRoole(), ii = 0; ii < nonterm.length && tmprole[0] != nonterm[ii]; ++ii) {}
                if (tmprole.length == 1) {
                    if (!result[ii].addWord(new int[0])) {
                        continue;
                    }
                    upr = true;
                }
                else {
                    int ii2;
                    for (ii2 = 1; ii2 < tmprole.length; ++ii2) {
                        if (tmprole[ii2] > 0) {
                            int ii3;
                            for (ii3 = 0; ii3 < term.length && term[ii3] != tmprole[ii2]; ++ii3) {}
                            tmpLlk[ii2 - 1] = llkTrmContext[ii3];
                        }
                        else {
                            int ii3;
                            for (ii3 = 0; ii3 < nonterm.length && nonterm[ii3] != tmprole[ii2]; ++ii3) {}
                            if (result[ii3].calcWords() == 0) {
                                break;
                            }
                            tmpLlk[ii2 - 1] = result[ii3];
                        }
                    }
                    if (ii2 != tmprole.length) {
                        continue;
                    }
                    int multCount;
                    int ii3;
                    for (multCount = tmprole.length - 1, ii3 = 0; ii3 < multCount; ++ii3) {
                        mult[ii3] = 0;
                        maxmult[ii3] = tmpLlk[ii3].calcWords();
                    }
                    int realCalc = 0;
                    for (ii3 = 0; ii3 < multCount; ++ii3) {
                        if (ii3 == 0) {
                            realCalc = tmpLlk[ii3].minLengthWord();
                        }
                        else {
                            final int minLength = tmpLlk[ii3].minLengthWord();
                            if (realCalc >= this.getLlkConst()) {
                                break;
                            }
                            realCalc += minLength;
                        }
                    }
                    realCalc = ii3;
                    do {
                        llkWord = this.newWord(this.getLlkConst(), tmpLlk, mult, realCalc);
                        if (result[ii].addWord(llkWord)) {
                            upr = true;
                        }
                    } while (this.newCalcIndex(mult, maxmult, realCalc));
                }
            }
        } while (upr);
        return result;
    }

    public void printFirstkContext() {
        final int[] nonterm = this.getNonTerminals();
        final LlkContext[] firstContext = this.getFirstK();
        if (firstContext == null) {
            return;
        }
        for (int j = 0; j < nonterm.length; ++j) {
            System.out.println("FirstK-\u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u043d\u0435\u0442\u0435\u0440\u043c\u0456\u043d\u0430\u043b\u0430: " + this.getLexemaText(nonterm[j]));
            final LlkContext tmp = firstContext[j];
            for (int ii = 0; ii < tmp.calcWords(); ++ii) {
                final int[] word = tmp.getWord(ii);
                if (word.length == 0) {
                    System.out.print("\u0415-\u0441\u043b\u043e\u0432\u043e");
                }
                else {
                    for (int ii2 = 0; ii2 < word.length; ++ii2) {
                        System.out.print(this.getLexemaText(word[ii2]) + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}
