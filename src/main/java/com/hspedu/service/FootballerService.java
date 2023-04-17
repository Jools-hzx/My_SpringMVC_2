package com.hspedu.service;

import com.hspedu.entity.Footballer;

import java.util.List;

/**
 * @author Zexi He.
 * @date 2023/4/17 16:00
 * @description:
 */
public interface FootballerService {

    List<Footballer> listAllFootballers();

    List<Footballer> listFootballersByName(String name);
}
