package barcute.model;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="places", uniqueConstraints = {@UniqueConstraint(columnNames = "placeId")})
public class Place implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="placeId")
    public int id;

    @Column(name="row")
    private int row;

    @Column(name="column")
    private int column;

    @Column(name="isSet")
    private boolean isSet;

    public Place() {
        isSet = false;
    }

    public Place(int row, int column, boolean isSet) {
        this.row = row;
        this.column = column;
        this.isSet = isSet;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }
}
