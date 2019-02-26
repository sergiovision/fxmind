package com.fxmind.manager.view;

import com.fxmind.dao.NewsEventDao;
import com.fxmind.entity.Currency;
import com.fxmind.entity.Newsevent;
import com.fxmind.global.fxmindConstants;
import com.fxmind.service.AdminService;
import com.fxmind.utils.StringCleanUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import org.apache.xpath.operations.Bool;
import org.vaadin.addons.lazyquerycontainer.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sergei Zhuravlev
 */
public class NewsQuery implements Query {
    private final static String DATE_FORMAT = "dd-MM-yyyy";
    private final static String TIME_ZONE = "GMT+00:00";

    private AdminService adminService;
    private NewsEventDao eventDao;

    public NewsQuery(AdminService adminService, NewsEventDao events) {
        this.adminService = adminService;
        this.eventDao = events;
    }

    @Override
    public int size() {
        Integer maxSize = 2000;
        List<Newsevent> events = eventDao.GetUpcomingEvents(0, maxSize);
        if (events==null)
            return 0;
        int size = events.size();
        if (size > maxSize) {
            size = maxSize;
        }
        return size;
    }

    @Override
    public List<Item> loadItems(int startIndex, int count) {
        //Date tillDate = memoryController.loadPersonsSignInTillSearch();
        List<Newsevent> events  = eventDao.GetUpcomingEvents(startIndex, count);
        return createItems(events);
    }

    private List<Item> createItems(List<Newsevent> newsList) {
        List<Item> itemList = new ArrayList<Item>();
        /*List<String> props = new LinkedList<>();
        props.add(NewsView.COLUMN_HAPPENTIME);
        props.add(NewsView.COLUMN_CURRENCY);
        props.add(NewsView.COLUMN_NAME);
        props.add(NewsView.COLUMN_IMPORTANCE);
        props.add(NewsView.COLUMN_ACTUALVAL);
        props.add(NewsView.COLUMN_FORECASTVAL);
        props.add(NewsView.COLUMN_PREVIOUSVAL);
        props.add(NewsView.COLUMN_PARSETIME);
        props.add(NewsView.COLUMN_RAISED);*/

        for (Newsevent event : newsList) {

            BeanItem<Newsevent> item = new BeanItem<Newsevent>(event);
            try {

                //Button editButton = new NativeButton(messageProvider.getMessage("person.view.edit.button"));
                //editButton.addStyleName("small");
                //editButton.setData(person);

                //editButton.addClickListener(event -> {
                //    showPersonDialog((PersonDTO) event.getButton().getData(), item);
                //});

                String name = event.getName();
                LocalDateTime happentimeGMT = event.getHappentime().toLocalDateTime();
                LocalDateTime happentime = adminService.ConvertUtcToBrokerTime(happentimeGMT);

                Integer importance = new Integer(event.getImportance());
                String actual = event.getActualval();
                String forecastval = event.getForecastval();
                String previousval = event.getPreviousval();

                LocalDateTime parsetimeDt = adminService.ConvertUtcToBrokerTime(event.getParsetime().toLocalDateTime());
                //String parsetime = parsetimeDt.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
                Boolean Raised = event.getRaised();

                item.addItemProperty(NewsView.COLUMN_HAPPENTIME, new ObjectProperty(happentime));

                Currency cur = event.getCurrencyid();
                if (cur != null)
                    item.addItemProperty(NewsView.COLUMN_CURRENCY, new ObjectProperty(cur.getName()));
                else
                    item.addItemProperty(NewsView.COLUMN_CURRENCY, new ObjectProperty("ALL"));

                item.addItemProperty(NewsView.COLUMN_NAME, new ObjectProperty(name));
                item.addItemProperty(NewsView.COLUMN_IMPORTANCE, new ObjectProperty(importance));
                item.addItemProperty(NewsView.COLUMN_ACTUALVAL,
                        new ObjectProperty(StringCleanUtils.stringIsNullOrEmpty(actual)?"":actual));
                item.addItemProperty(NewsView.COLUMN_FORECASTVAL,
                        new ObjectProperty(StringCleanUtils.stringIsNullOrEmpty(forecastval)?"":forecastval));
                item.addItemProperty(NewsView.COLUMN_PREVIOUSVAL,
                        new ObjectProperty(StringCleanUtils.stringIsNullOrEmpty(previousval)?"":previousval));
                item.addItemProperty(NewsView.COLUMN_PARSETIME, new ObjectProperty(parsetimeDt));
                item.addItemProperty(NewsView.COLUMN_RAISED, new ObjectProperty(Raised));

            } catch ( Exception e) {
                item.addItemProperty(NewsView.COLUMN_NAME, new ObjectProperty(e.toString()));
            }
            itemList.add(item);
         }

        return itemList;
    }

    @Override
    public void saveItems(List<Item> items, List<Item> items2, List<Item> items3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteAllItems() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item constructItem() {
        return null;
    }

}
