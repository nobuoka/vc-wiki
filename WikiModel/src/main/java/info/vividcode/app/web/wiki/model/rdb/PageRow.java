package info.vividcode.app.web.wiki.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
    name = "page"
)
public class PageRow {

    // XXX JPA の attribute としては DB 上のテーブルのカラム名ではなくクラスのフィールド名を使うっぽいので
    // カラム名とフィールド名を揃えておく。 (名前の違いによる面倒を避けるため。)
    public static class ColumnNames {
        public static final String PATH_ID = "pathId";
        public static final String TITLE = "title";
    }

    @Column(name=ColumnNames.PATH_ID)
    @Id
    private long pathId;

    @Column(name = ColumnNames.TITLE, nullable = false)
    private String title;

    public void setPathId(long pathId) {
        this.pathId = pathId;
    }

    public long getPathId() {
        return pathId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
