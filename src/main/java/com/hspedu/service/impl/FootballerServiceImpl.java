package com.hspedu.service.impl;

import com.hspedu.entity.Footballer;
import com.hspedu.hzxspringmvc.annotation.Service;
import com.hspedu.service.FootballerService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zexi He.
 * @date 2023/4/17 16:02
 * @description:
 */
@Service(value = "footballerService")
public class FootballerServiceImpl implements FootballerService {

    private static List<Footballer> footballerList = new ArrayList<>();

    static {
        footballerList.add(new Footballer("1", "C.Ronaldo", "RM"));
        footballerList.add(new Footballer("2", "L.Messi", "PARIS"));
        footballerList.add(new Footballer("3", "Haland", "MC"));
    }

    @Override
    public List<Footballer> listAllFootballers() {
        return footballerList;
    }

    //该方法根据关键字查找对应的 footballer
    @Override
    public List<Footballer> listFootballersByName(String name) {
        List<Footballer> footballers = new ArrayList<>();
        for (Footballer footballer : footballerList) {
            if (footballer.getName().contains(name)) {
                footballers.add(footballer);
            }
        }
        return footballers;
    }
}
