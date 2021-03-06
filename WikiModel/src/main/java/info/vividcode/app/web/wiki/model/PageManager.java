package info.vividcode.app.web.wiki.model;

import info.vividcode.app.web.wiki.model.rdb.PageContentRow;
import info.vividcode.app.web.wiki.model.rdb.PagePathRow;
import info.vividcode.app.web.wiki.model.rdb.PageRow;
import info.vividcode.text.hatena.HatenaTextParser;
import info.vividcode.text.hatena.StructuredText;
import info.vividcode.text.hatena.StructuredTextToHtmlConverter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

public class PageManager implements AutoCloseable {

    private PageManager(EntityManagerFactory emf, Node esn) {
        emFactory = emf;
        esNode = esn;
    }

    public static PageManager create() {
        EntityManagerFactory emf = createEMFactory();
        Node esn = createESNode();
        return new PageManager(emf, esn);
    }

    @Override
    public void close() {
        esNode.close();
        emFactory.close();
    }

    private static final String PERSISTENCE_UNIT_NAME_WIKI = "wiki";

    private final EntityManagerFactory emFactory;
    private final Node esNode;

    public static final class PageSourceResource {
        public final String title;
        public final String contentSource;
        public PageSourceResource(String title, String content) {
            this.title = title;
            this.contentSource = content;
        }
    }

    public static final class PageHtmlResource {
        public final String title;
        public final String contentHtml;
        public PageHtmlResource(String title, String contentHtml) {
            this.title = title;
            this.contentHtml = contentHtml;
        }
    }

    public static final class PagePathResourcePair {
        public final String path;
        public final PageSourceResource resource;
        public PagePathResourcePair(String path, PageSourceResource res) {
            this.path = path;
            this.resource = res;
        }
    }

    private static <T> T findByField(
            EntityManager em, Class<T> rowClass, String fieldName, Object fieldValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(rowClass);
        Root<T> root = cq.from(rowClass);
        cq.select(root)
                .where(cb.equal(root.get(fieldName), fieldValue));
        TypedQuery<T> q = em.createQuery(cq);
        List<T> pagePathRows = q.getResultList();
        return (pagePathRows.size() > 0 ? pagePathRows.get(0) : null);
    }

    private static <T> List<T> searchByField(
            EntityManager em, Class<T> rowClass, String fieldName, List<? extends Object> fieldValues) {
        if (fieldValues.size() == 0) {
            return Arrays.asList();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(rowClass);
        Root<T> root = cq.from(rowClass);
        cq.select(root)
                .where(root.get(fieldName).in(fieldValues));
        TypedQuery<T> q = em.createQuery(cq);
        return q.getResultList();
    }

    private static EntityManagerFactory createEMFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_WIKI);
    }

    private static Node createESNode() {
        ImmutableSettings.Builder settings =
                ImmutableSettings.settingsBuilder();
        settings.put("node.name", "wiki-node");
        settings.put("path.data", "wiki_elasticsearch");
        settings.put("http.enabled", false);
        return NodeBuilder.nodeBuilder()
                .settings(settings)
                .clusterName("wiki-cluster")
                .data(true).local(true).node();
    }

    public void createPage(String path, PageSourceResource res) {
        EntityManager em = emFactory.createEntityManager();
        Client client = esNode.client();
        try {
            // See: http://docs.oracle.com/javaee/7/tutorial/doc/persistence-criteria.htm

            em.getTransaction().begin();
            PagePathRow pagePathRow =
                    findByField(em, PagePathRow.class, PagePathRow.ColumnNames.PATH, path);
            if (pagePathRow == null) {
                pagePathRow = new PagePathRow();
                pagePathRow.setPath(path);
                em.persist(pagePathRow);
                // トランザクションのコミット前に DB とやりとりさせるために `flush` する
                // See: http://stackoverflow.com/questions/8169640/how-does-an-entity-get-an-id-before-a-transaction-is-committed-in-jpa-play
                em.flush();
            }
            long pathId = pagePathRow.getId();
            PageRow pageRow = new PageRow();
            pageRow.setPathId(pathId);
            pageRow.setTitle(res.title);
            em.persist(pageRow);

            HatenaTextParser parser = new HatenaTextParser();
            StructuredText t = parser.parse(res.contentSource);
            String contentHtml = convertToHtml(t);

            PageContentRow pageContentRow = new PageContentRow();
            pageContentRow.setPathId(pathId);
            pageContentRow.setContentSource(res.contentSource);
            pageContentRow.setContentHtml(contentHtml);
            em.persist(pageContentRow);

            putPageResourceIntoES(client, Long.toString(pathId), res);
            em.getTransaction().commit();
        } finally {
            // エラーの場合明示的なロールバックっているのかな
            client.close();
            em.close();
        }
    }

    private String convertToHtml(StructuredText t) {
        //System.out.println(t.getRawText());
        try (StringWriter sw = new StringWriter()) {
            StructuredTextToHtmlConverter conv =
                    new StructuredTextToHtmlConverter();
            conv.convert(t, sw);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String INDEX_WIKI = "wiki";
    private static final String TYPE_WIKI_PAGE = "wiki-page";

    private void putPageResourceIntoES(Client client, String pathId, PageSourceResource res) {
        Map<String,Object> source = new HashMap<>();
        source.put("title", res.title);
        source.put("content", res.contentSource);
        client.prepareIndex(INDEX_WIKI, TYPE_WIKI_PAGE, pathId)
                .setSource(source).execute().actionGet();
    }

    /*
    private PageSourceResource getPageResourceFromES(Client client, String pathId) {
        System.out.println("pathId: " + pathId);
        GetResponse res = client.prepareGet(INDEX_WIKI, TYPE_WIKI_PAGE, pathId)
                .execute().actionGet();
        if (!res.isExists()) return null;
        String title = (String) res.getSourceAsMap().get("title");
        String content = (String) res.getSourceAsMap().get("content");
        return new PageSourceResource(title, content);
    }
    */

    // TODO
    public void updatePage(String path, PageSourceResource res) {
        return;
    }

    // TODO
    public void deletePage(String path) {
        return;
    }

    public PageHtmlResource getPage(String path) {
        EntityManager em = emFactory.createEntityManager();
        Client client = esNode.client();
        try {
            // See: http://docs.oracle.com/javaee/7/tutorial/doc/persistence-criteria.htm

            PagePathRow pagePathRow =
                    findByField(em, PagePathRow.class, PagePathRow.ColumnNames.PATH, path);
            if (pagePathRow == null) return null;
            PageRow pageRow =
                    em.find(PageRow.class, pagePathRow.getId());
            PageContentRow pageContentRow =
                    em.find(PageContentRow.class, pagePathRow.getId());
            // TODO pageRow とかが null もありえる
            return new PageHtmlResource(pageRow.getTitle(), pageContentRow.getContentHtml());
        } finally {
            client.close();
            em.close();
        }
    }

    public List<PagePathResourcePair> searchPages(String text, int offset, int size) {
        EntityManager em = emFactory.createEntityManager();
        Client client = esNode.client();
        try {
            QueryBuilder queryStringBuilder = QueryBuilders
                    .queryString(text)
                    .field("title", 2)
                    .field("content");
            SearchRequestBuilder requestBuilder = client
                    .prepareSearch(INDEX_WIKI)
                    .setTypes(TYPE_WIKI_PAGE)
                    .setQuery(queryStringBuilder);
            SearchResponse response = requestBuilder.execute().actionGet();
            System.out.println("total hits: " + response.getHits().totalHits());
            // pathId -> pathStr のマップを作る
            List<String> pathIds = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                pathIds.add(hit.getId());
            }
            List<PagePathRow> ppr = searchByField(em, PagePathRow.class, PagePathRow.ColumnNames.ID, pathIds);
            Map<String,String> idPathMap = new HashMap<>();
            for (PagePathRow r : ppr) {
                idPathMap.put(Long.toString(r.getId()), r.getPath());
            }
            // (path, pageResource) の組のリストを作る
            List<PagePathResourcePair> pprps = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                String title = (String) hit.sourceAsMap().get("title");
                String content = (String) hit.sourceAsMap().get("content");
                String path = idPathMap.get(hit.getId());
                pprps.add(new PagePathResourcePair(path, new PageSourceResource(title, content)));
            }
            return pprps;
        } finally {
            em.close();
            client.close();
        }
    }

}
