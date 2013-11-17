package info.vividcode.app.web.wiki.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
    name = "page_content"
)
public class PageContentRow {

    // XXX JPA の attribute としては DB 上のテーブルのカラム名ではなくクラスのフィールド名を使うっぽいので
    // カラム名とフィールド名を揃えておく。 (名前の違いによる面倒を避けるため。)
    public static class ColumnNames {
        public static final String PATH_ID = "pathId";
        public static final String CONTENT_SOURCE = "contentSourceText";
        public static final String CONTENT_HTML = "contentHtmlText";
    }

    @Column(name=ColumnNames.PATH_ID)
    @Id
    private long pathId;

    @Column(name = ColumnNames.CONTENT_SOURCE, length=25000, nullable = false)
    private String contentSourceText;

    @Column(name = ColumnNames.CONTENT_HTML, length=25000, nullable = false)
    private String contentHtmlText;

    public void setPathId(long pathId) {
        this.pathId = pathId;
    }

    public long getPathId() {
        return pathId;
    }

    public String getContentSource() {
        return contentSourceText;
    }

    public void setContentSource(String contentSource) {
        this.contentSourceText = contentSource;
    }

    public String getContentHtml() {
        return contentHtmlText;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtmlText = contentHtml;
    }

}
