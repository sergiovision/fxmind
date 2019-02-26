package com.fxmind.manager.view;
import com.fxmind.dao.NewsEventDao;
import com.fxmind.global.fxmindConstants;
import com.fxmind.service.AdminService;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import ru.xpoft.vaadin.VaadinView;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;


/**
 * @author Sergei Zhuravlev
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(NewsView.VIEW_NAME)
public class NewsView extends Panel implements View {
    public static final String VIEW_NAME = "news";

    private static final Integer PAGE_LENGTH = 50;


    public static final String COLUMN_HAPPENTIME = "Happen Time";
    public static final String COLUMN_CURRENCY = "Currency";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_IMPORTANCE = "Importance";
    public static final String COLUMN_ACTUALVAL = "Actual";
    public static final String COLUMN_FORECASTVAL = "Forecast";
    public static final String COLUMN_PREVIOUSVAL = "Previous";
    public static final String COLUMN_PARSETIME = "Parse Time";
    public static final String COLUMN_RAISED = "Raised";

    private final static String TOOLBAR_STYLE_NAME = "toolbar";
    private final static String MSS_TITLE_STYLE_NAME = "mss-title";
    private final static String DASHBOARD_VIEW_STYLE_NAME = "dashboard-view";

    @Autowired
    private AdminService adminService;

    @Autowired
    protected NewsEventDao newsEventDao;

    private LazyQueryContainer newsContainerDataSource;
    private Table newsTable;

    @PostConstruct
    private void init() {
        NewsQueryFactory personQueryFactory = new NewsQueryFactory(adminService, newsEventDao);
        newsContainerDataSource = new LazyQueryContainer(personQueryFactory, null, PAGE_LENGTH, false);

        newsContainerDataSource.addContainerProperty(COLUMN_HAPPENTIME, LocalDateTime.class, null, true, true);
        newsContainerDataSource.addContainerProperty(COLUMN_CURRENCY, String.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_NAME, String.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_IMPORTANCE, Integer.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_ACTUALVAL, String.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_FORECASTVAL, String.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_PREVIOUSVAL, String.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_PARSETIME, LocalDateTime.class, null);
        newsContainerDataSource.addContainerProperty(COLUMN_RAISED, Boolean.class, null);

        //newsContainerDataSource.sort(new Object[] {COLUMN_HAPPENTIME}, new boolean[]{true});

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setSizeFull();
        addStyleName(DASHBOARD_VIEW_STYLE_NAME);

        VerticalLayout personsContainerPanel = new VerticalLayout();
        personsContainerPanel.setSizeFull();

        //HorizontalLayout searchPanel = new HorizontalLayout();
        //searchPanel.setSizeUndefined();

        VerticalLayout personTableContainer = new VerticalLayout();
        personTableContainer.setSizeFull();

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName(TOOLBAR_STYLE_NAME);

        Label title = new Label("News View. Time shown in Broker Time Zone");
        title.addStyleName(MSS_TITLE_STYLE_NAME);
        title.setSizeUndefined();
        toolbar.addComponent(title);
        toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        newsTable = new Table()
        {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId,
                                                 Property property) {
                Object v = property.getValue();
                if (v instanceof LocalDateTime) {
                    LocalDateTime dateValue = (LocalDateTime) v;
                    return dateValue.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };

        newsTable.setWidth("100%");
        newsTable.setHeight("98%");
        newsTable.addStyleName("borderless");
        newsTable.setSelectable(true);
        newsTable.setNullSelectionAllowed(false);
        newsTable.setImmediate(true);
        newsTable.setSortEnabled(false);
        newsTable.setContainerDataSource(newsContainerDataSource);

        //newsTable.setCellStyleGenerator(new PersonTableCellStyleGenerator());

        newsTable.setColumnHeader(COLUMN_HAPPENTIME, COLUMN_HAPPENTIME);
        newsTable.setColumnHeader(COLUMN_CURRENCY, COLUMN_CURRENCY);
        newsTable.setColumnHeader(COLUMN_NAME, COLUMN_NAME);
        newsTable.setColumnHeader(COLUMN_IMPORTANCE, COLUMN_IMPORTANCE);
        newsTable.setColumnHeader(COLUMN_ACTUALVAL, COLUMN_ACTUALVAL);
        newsTable.setColumnHeader(COLUMN_FORECASTVAL, COLUMN_FORECASTVAL);
        newsTable.setColumnHeader(COLUMN_PREVIOUSVAL, COLUMN_PREVIOUSVAL);
        newsTable.setColumnHeader(COLUMN_PARSETIME, COLUMN_PARSETIME);
        newsTable.setColumnHeader(COLUMN_RAISED, COLUMN_RAISED);

        //newsTable.sort(new Object[] {COLUMN_HAPPENTIME}, new boolean[]{true});

        //personTableContainer.addComponent(searchPanel);
        personTableContainer.addComponent(newsTable);
        personTableContainer.setExpandRatio(newsTable, 1);

        Component wrappedTablePanel = createPanel(personTableContainer);

        personsContainerPanel.addComponent(toolbar);
        personsContainerPanel.addComponent(wrappedTablePanel);
        personsContainerPanel.setExpandRatio(wrappedTablePanel, 1);

        setContent(personsContainerPanel);


    }


    private CssLayout createPanel(Component content) {
        CssLayout panel = new CssLayout();
        panel.addStyleName("layout-panel");
        panel.setSizeFull();
        panel.addComponent(content);
        return panel;
    }

    private class NewsTableCellStyleGenerator implements Table.CellStyleGenerator {
        @Override
        public String getStyle(Table table, Object itemId, Object propertyId) {
            //Item item = table.getItem(itemId);
            //Property<Button> property = item.getItemProperty(COLUMN_EDIT_PROPERTY_ID);
            //Button personButton = property.getValue();
            //PersonDTO person =  (PersonDTO)personButton.getData();
            //if (person == null)
            //    return null;
            //if (person.isOnline() ) {
            //    return "lightgreen";
            //}
            return null;
        }
    }

}
