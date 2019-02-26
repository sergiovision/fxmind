package com.fxmind.dao;

import com.fxmind.entity.Symbol;

import java.util.List;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
public interface SymbolDao extends GenericDao<Symbol> {

    Symbol find(Integer id);

    Symbol findByName(String name);

    List<Symbol> findAll();
}
