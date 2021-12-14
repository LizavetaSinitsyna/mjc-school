package com.epam.esm.repository;

import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * 
 * Contains common methods for working with all entities during the interaction
 * with database.
 *
 */
public interface GeneralRepository<T> {
	/**
	 * Saves the passed entity.
	 * 
	 * @param model the entity to be saved
	 * @return saved entity
	 */
	T create(T model);

	/**
	 * Reads entity with passed id.
	 * 
	 * @param id the id of entity to be read
	 * @return entity with passed id
	 */
	T readById(long id);

	/**
	 * Reads entity with passed name.
	 * 
	 * @param name the name of entity to be read
	 * @return entity with passed name
	 */
	T readByName(String name);

	/**
	 * Reads all entities according to passed parameters.
	 * 
	 * @param params the parameters which define choice of entities and their
	 *               ordering
	 * @return entities which meet passed parameters
	 */
	List<T> readAll(MultiValueMap<String, String> params);

	/**
	 * Deletes entity with passed id.
	 * 
	 * @param id the id of entity to be deleted
	 * @return the number of deleted entities
	 */
	int delete(long id);

}
