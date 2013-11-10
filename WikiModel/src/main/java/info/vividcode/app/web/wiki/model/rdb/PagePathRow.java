package info.vividcode.app.web.wiki.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "page_path",
    uniqueConstraints = @UniqueConstraint(columnNames=PagePathRow.ColumnNames.PATH)
)
public class PagePathRow {

    public static class ColumnNames {
        public static final String ID = "id";
        public static final String PATH = "path";
    }

    @Column(name = ColumnNames.ID)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = ColumnNames.PATH, nullable = false)
    private String path;

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
