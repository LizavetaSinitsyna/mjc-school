package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

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
	T save(T model);

	/**
	 * Reads entity with passed id.
	 * 
	 * @param id the id of entity to be read
	 * @return entity with passed id
	 */
	Optional<T> findById(long id);

	/**
	 * Reads entity with passed name.
	 * 
	 * @param name the name of entity to be read
	 * @return entity with passed name
	 */
	Optional<T> findByName(String name);

	/**
	 * Deletes entity with passed id.
	 * 
	 * @param id the id of entity to be deleted
	 * @return the number of deleted entities
	 */
	int delete(long id);

	/**
	 * Reads all entities according to passed parameters.
	 * 
	 * @param params the parameters which define choice of tags and their ordering
	 * @return tags which meet passed parameters
	 */
	List<T> findAll(int offset, int limit);

}
