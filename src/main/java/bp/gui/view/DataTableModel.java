package bp.gui.view;

import bp.database.data.Row;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DataTableModel extends DefaultTableModel {

    private List<Row> rows;

    private void updateModel(){
        if (rows.isEmpty()) return;

        int columnCount = rows.get(0).getFields().keySet().size();
        Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
        Vector dataVector = new Vector(columnCount);

        for (Row row : rows) {
            dataVector.add(DefaultTableModel.convertToVector(row.getFields().values().toArray()));
        }

        setDataVector(dataVector, columnVector);
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
        updateModel();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) return row + 1;
        return super.getValueAt(row, column - 1);
    }

    @Override
    public int getColumnCount() {
        return super.getColumnCount() + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "";
        return super.getColumnName(column - 1);
    }

}
