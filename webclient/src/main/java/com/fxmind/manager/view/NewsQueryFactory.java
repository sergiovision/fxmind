package com.fxmind.manager.view;

import com.fxmind.dao.NewsEventDao;
import com.fxmind.service.AdminService;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

/**
 * Created by Sergei Zhuravlev
 */
public class NewsQueryFactory implements QueryFactory {
    private AdminService adminService;
    private NewsEventDao eventDao;

    public NewsQueryFactory(AdminService adminService, NewsEventDao events) {
        this.adminService = adminService;
        this.eventDao = events;
    }

    @Override
    public Query constructQuery(QueryDefinition queryDefinition) {
        return new NewsQuery(adminService, eventDao);
    }
}
