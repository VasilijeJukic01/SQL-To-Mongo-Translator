package bp.parser.clauses;

import bp.parser.KeyWord;

public class UsingClause extends Clause {

    private String column;

    public UsingClause() {
        super(KeyWord.USING);
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public void build() {

    }

}
