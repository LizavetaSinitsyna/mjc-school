package com.epam.esm.repository;

import java.util.List;

import org.springframework.util.MultiValueMap;

public interface GeneralRepository<T> {
	T create(T model);

	T readById(long id);

	T readByName(String name);

	List<T> readAll(MultiValueMap<String, String> params);

	int delete(long id);

}
